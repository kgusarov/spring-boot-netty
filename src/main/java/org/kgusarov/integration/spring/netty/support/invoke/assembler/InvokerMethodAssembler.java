package org.kgusarov.integration.spring.netty.support.invoke.assembler;

import io.netty.channel.Channel;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.IntStream;

import static org.kgusarov.integration.spring.netty.support.invoke.assembler.Descriptors.*;
import static org.kgusarov.integration.spring.netty.support.invoke.assembler.LabelAssembler.createLabel;
import static org.kgusarov.integration.spring.netty.support.invoke.assembler.LocalVariableAssembler.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Internal API: code generation support - invoker method
 */
public final class InvokerMethodAssembler {
    private InvokerMethodAssembler() {
    }

    public static void assembleInvokerMethod(final ClassWriter cw, final String invokerDescriptor, final String invokerInternalName,
                                             final Method invokerMethod, final Method targetMethod, final boolean sendResult,
                                             final MethodPrefixAssembler prefixAssembler) {

        final String methodDescriptor = Type.getMethodDescriptor(invokerMethod);
        final String methodName = invokerMethod.getName();

        final MethodVisitor m = cw.visitMethod(ACC_PUBLIC, methodName, methodDescriptor, null, null);
        final Label ms = createLabel();
        final Label me = createLabel();

        m.visitCode();
        m.visitLabel(ms);

        final Parameter[] invokeParameters = invokerMethod.getParameters();
        final int resultIdx = getResultIdx(invokeParameters);
        final int firstVarIdx = getFirstVarIdx(invokeParameters, sendResult);
        prefixAssembler.assemble(invokerInternalName, m, firstVarIdx);

        m.visitFieldInsn(GETSTATIC, invokerInternalName, "HANDLE", MH_DESCRIPTOR);
        m.visitIntInsn(ALOAD, 0);
        m.visitFieldInsn(GETFIELD, invokerInternalName, "bean", OBJ_DESCRIPTOR);

        final Parameter[] targetMethodParameters = targetMethod.getParameters();
        for (int i = 0; i < targetMethodParameters.length; i++) {
            m.visitIntInsn(ALOAD, i + firstVarIdx);
        }

        final String[] paramDescriptors = new String[targetMethodParameters.length + 1];
        Arrays.fill(paramDescriptors, OBJ_DESCRIPTOR);

        final String tmd = '(' + String.join("", paramDescriptors) + ')' + OBJ_DESCRIPTOR;
        m.visitMethodInsn(INVOKEVIRTUAL, MH_INTERNAL_NAME, "invokeExact", tmd, false);

        if (sendResult) {
            final int channelArgIdx = IntStream.range(0, invokeParameters.length)
                    .filter(i -> {
                        final Parameter parameter = invokeParameters[i];
                        final Class<?> type = parameter.getType();
                        return Channel.class.isAssignableFrom(type);
                    })
                    .findFirst()
                    .orElse(-2) + 1;

            m.visitIntInsn(ASTORE, resultIdx);
            m.visitIntInsn(ALOAD, channelArgIdx);
            m.visitIntInsn(ALOAD, resultIdx);

            m.visitMethodInsn(INVOKEINTERFACE, CHANNEL_INTERNAL_NAME, "writeAndFlush",
                    CHANNEL_WRITE_AND_FLUSH_DESCRIPTOR, true);
        }

        m.visitInsn(POP);
        m.visitInsn(RETURN);

        m.visitLabel(me);
        assembleLocalVariables(m, ms, me, invokerDescriptor, invokeParameters, targetMethodParameters, sendResult);

        m.visitEnd();
        m.visitMaxs(0, 0);
    }
}
