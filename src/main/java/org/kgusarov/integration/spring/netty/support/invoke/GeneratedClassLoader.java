package org.kgusarov.integration.spring.netty.support.invoke;

import java.lang.invoke.MethodHandle;

/**
 * Internal API: fast invocation support
 */
final class GeneratedClassLoader {
    private static final MethodHandle DEFINE_CLASS_HANDLE;

    static {
        try {
            DEFINE_CLASS_HANDLE = MethodHandleCreator.create(ClassLoader.class, "defineClass",
                    String.class, byte[].class, int.class, int.class);
        } catch (final IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    <T> Class<T> load(final byte[] bytecode, final String fqcn) {
        final ClassLoader classLoader = getClass().getClassLoader();
        try {
            return (Class<T>) DEFINE_CLASS_HANDLE.invokeExact(classLoader, fqcn, bytecode, 0, bytecode.length);
        } catch (final Throwable t) {
            throw new IllegalStateException(t);
        }
    }
}
