package org.kgusarov.integration.spring.netty.support.invoke.assembler;

import com.google.common.primitives.Primitives;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.kgusarov.integration.spring.netty.support.invoke.assembler.Descriptors.*;
import static org.kgusarov.integration.spring.netty.support.invoke.assembler.Descriptors.MH_DESCRIPTOR;
import static org.kgusarov.integration.spring.netty.support.invoke.assembler.LabelAssembler.createLabel;
import static org.objectweb.asm.Opcodes.*;

/**
 * Internal API: code generation support - method handle private static final field
 */
public final class MethodHandleFieldAssembler {
    private MethodHandleFieldAssembler() {
    }

    public static void assembleMethodHandleField(final ClassWriter cw, final Method targetMethod, final String invokerName) {
        final String desc = Type.getDescriptor(MethodHandle.class);
        cw.visitField(ACC_PRIVATE | ACC_FINAL + ACC_STATIC, "HANDLE", desc, null, null);
        cw.visitEnd();

        final MethodVisitor ctor = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        final Label cs = createLabel();
        final Label ce = createLabel();

        ctor.visitCode();
        ctor.visitLabel(cs);

        final String clName = targetMethod.getDeclaringClass().getCanonicalName();
        final String targetMethodName = targetMethod.getName();
        ctor.visitLdcInsn(clName);
        ctor.visitLdcInsn(targetMethodName);

        final Parameter[] parameters = targetMethod.getParameters();
        ctor.visitLdcInsn(parameters.length);

        final Type classType = Type.getType(Class.class);
        final String descriptor = classType.getInternalName();
        ctor.visitTypeInsn(ANEWARRAY, descriptor);

        for (int i = 0; i < parameters.length; i++) {
            ctor.visitInsn(DUP);
            ctor.visitLdcInsn(i);

            final Class<?> paramClass = parameters[i].getType();
            if (paramClass.isPrimitive()) {
                final Class<?> wrapper = Primitives.wrap(paramClass);
                final String holderName = Type.getType(wrapper).getInternalName();

                ctor.visitFieldInsn(GETSTATIC, holderName, "TYPE", CL_DESCRIPTOR);
            } else {
                final Type parameterType = Type.getType(paramClass);
                ctor.visitLdcInsn(parameterType);
            }

            ctor.visitInsn(AASTORE);
        }

        ctor.visitMethodInsn(INVOKESTATIC, MHC_INTERNAL_NAME, "createUniversal",
                '(' + STR_DESCRIPTOR + STR_DESCRIPTOR + CLA_DESCRIPTOR + ')' + MH_DESCRIPTOR, false);
        ctor.visitFieldInsn(PUTSTATIC, invokerName, "HANDLE", MH_DESCRIPTOR);
        ctor.visitInsn(RETURN);

        ctor.visitLabel(ce);
        ctor.visitEnd();

        ctor.visitMaxs(0, 0);
    }
}
