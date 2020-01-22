package org.kgusarov.integration.spring.netty.support.invoke.assembler;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Parameter;

import static org.kgusarov.integration.spring.netty.support.invoke.assembler.Descriptors.OBJ_DESCRIPTOR;

/**
 * Internal API: code generation support - local variables
 */
final class LocalVariableAssembler {
    private LocalVariableAssembler() {
    }

    static int getResultIdx(final Parameter[] invokeParameters) {
        return invokeParameters.length + 1;
    }

    static int getFirstVarIdx(final Parameter[] invokeParameters, final boolean sendResult) {
        final int resultIdx = getResultIdx(invokeParameters);
        return sendResult ? resultIdx + 1 : resultIdx;
    }

    static void assembleLocalVariables(final MethodVisitor m, final Label ms, final Label me,
                                              final String invokerDescriptor, final Parameter[] invokeParameters,
                                              final Parameter[] targetMethodParameters, final boolean sendResult) {

        final int resultIdx = getResultIdx(invokeParameters);
        final int firstVarIdx = getFirstVarIdx(invokeParameters, sendResult);
        m.visitLocalVariable("this", invokerDescriptor, null, ms, me, 0);

        for (int i = 0; i < invokeParameters.length; i++) {
            final Parameter parameter = invokeParameters[i];
            final Class<?> parameterType = parameter.getType();
            final String descriptor = Type.getDescriptor(parameterType);

            //noinspection StringConcatenationMissingWhitespace
            m.visitLocalVariable("arg" + i, descriptor, null, ms, me, i + 1);
        }

        if (sendResult) {
            m.visitLocalVariable("result", OBJ_DESCRIPTOR, null, ms, me, resultIdx);
        }

        for (int i = 0; i < targetMethodParameters.length; i++) {
            //noinspection StringConcatenationMissingWhitespace
            m.visitLocalVariable("o" + i, OBJ_DESCRIPTOR, null, ms, me, i + firstVarIdx);
        }
    }
}
