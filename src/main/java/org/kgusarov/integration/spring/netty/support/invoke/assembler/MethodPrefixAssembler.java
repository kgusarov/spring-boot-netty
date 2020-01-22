package org.kgusarov.integration.spring.netty.support.invoke.assembler;

import org.objectweb.asm.MethodVisitor;

/**
 * Internal API: code generation support - specific method prefix
 */
@FunctionalInterface
public interface MethodPrefixAssembler {
    void assemble(String invokerInternalName, MethodVisitor m, int firstVarIdx);
}
