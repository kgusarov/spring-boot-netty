package org.kgusarov.integration.spring.netty.support.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Internal API: fast invocation support
 */
public final class MethodHandleCreator {
    private MethodHandleCreator() {
    }

    static MethodHandle createUniversal(final String className, final String methodName, final Class<?> ...params)
            throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException {

        final MethodHandle h = create(className, methodName, params);
        final MethodType mt = h.type();
        final Class<?>[] parameterArray = mt.parameterArray();
        final Class<?>[] adaptedParameterArray = new Class<?>[parameterArray.length];

        Arrays.fill(adaptedParameterArray, Object.class);

        final MethodType adaptedMt = MethodType.methodType(Object.class, adaptedParameterArray);
        return h.asType(adaptedMt);
    }

    static MethodHandle create(final String className, final String methodName, final Class<?> ...params)
            throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException {

        final Class<?> clazz = Class.forName(className);
        return create(clazz, methodName, params);
    }

    static MethodHandle create(final Class<?> clazz, final String methodName, final Class<?> ...params)
            throws IllegalAccessException, NoSuchMethodException {

        final MethodHandles.Lookup caller = MethodHandles.lookup();
        final Method method = clazz.getDeclaredMethod(methodName, params);
        method.setAccessible(true);

        return caller.unreflect(method);
    }
}
