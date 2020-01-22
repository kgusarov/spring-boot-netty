package org.kgusarov.integration.spring.netty.support.invoke;

import org.kgusarov.integration.spring.netty.support.invoke.assembler.MethodPrefixAssembler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

import static org.kgusarov.integration.spring.netty.support.invoke.assembler.ConstructorAssembler.assembleConstructor;
import static org.kgusarov.integration.spring.netty.support.invoke.assembler.InvokerMethodAssembler.assembleInvokerMethod;
import static org.kgusarov.integration.spring.netty.support.invoke.assembler.MethodHandleFieldAssembler.assembleMethodHandleField;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

/**
 * Internal API: invocation support for {@link org.kgusarov.integration.spring.netty.annotations.NettyController}
 * methods
 */
abstract class AbstractMethodInvoker {
    @SuppressWarnings("AbstractClassNeverImplemented")
    abstract static class InvokerBase {
        Object bean;
    }

    private static final String[] EMPTY_INTERFACES = new String[0];
    private static final GeneratedClassLoader CLASS_LOADER = new GeneratedClassLoader();
    private static final AtomicLong COUNTER = new AtomicLong(1);

    final <T extends InvokerBase> T buildInvoker(final Class<?> parent, final Method targetMethod, final Method invokerMethod,
                                                 final boolean sendResult, final MethodPrefixAssembler prefixAssembler) {

        final String packageName = parent.getPackage().getName();
        final String invokerClassName = packageName + ".DynamicInvoker$$GeneratedClass$$" + COUNTER.getAndIncrement();

        final String invokerInternalName = invokerClassName.replace('.', '/');
        final String parentInternalName = Type.getInternalName(parent);

        final ClassWriter cw = new ClassWriter(COMPUTE_MAXS);
        cw.visit(V1_8, ACC_SUPER | ACC_PUBLIC, invokerInternalName, null, parentInternalName, EMPTY_INTERFACES);

        final Type invokerType = Type.getObjectType(invokerInternalName);
        final String invokerDescriptor = invokerType.getDescriptor();

        assembleConstructor(invokerType, parentInternalName, cw);
        assembleMethodHandleField(cw, targetMethod, invokerInternalName);
        assembleInvokerMethod(cw, invokerDescriptor, invokerInternalName, invokerMethod, targetMethod, sendResult, prefixAssembler);

        cw.visitEnd();

        final byte[] bytecode = cw.toByteArray();
        final Class<T> cl = CLASS_LOADER.load(bytecode, invokerClassName);

        try {
            return cl.getDeclaredConstructor().newInstance();
        } catch (final IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
