package org.kgusarov.integration.spring.netty.support.invoke.assembler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.kgusarov.integration.spring.netty.support.invoke.assembler.LabelAssembler.createLabel;
import static org.objectweb.asm.Opcodes.*;

/**
 * Internal API: code generation support - constructor
 */
public final class ConstructorAssembler {
    private ConstructorAssembler() {
    }

    public static void assembleConstructor(final Type invokerType, final String parentName, final ClassWriter cw) {
        final MethodVisitor ctor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        final Label cs = createLabel();
        final Label ce = createLabel();

        ctor.visitCode();
        ctor.visitLabel(cs);
        ctor.visitVarInsn(ALOAD, 0);
        ctor.visitMethodInsn(INVOKESPECIAL, parentName, "<init>", "()V", false);
        ctor.visitInsn(RETURN);
        ctor.visitLabel(ce);
        final String cn = invokerType.getDescriptor();
        ctor.visitLocalVariable("this", cn, null, cs, ce, 0);
        ctor.visitEnd();

        ctor.visitMaxs(0, 0);
    }
}
