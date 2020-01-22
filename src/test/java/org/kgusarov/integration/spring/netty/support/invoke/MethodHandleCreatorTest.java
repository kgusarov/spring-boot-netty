package org.kgusarov.integration.spring.netty.support.invoke;

import org.junit.Test;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.invoke.MethodHandle;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"CodeBlock2Expr", "ProhibitedExceptionDeclared"})
public class MethodHandleCreatorTest {
    private static final Victim VICTIM = new Victim();

    @Test
    public void nonExistentMethod() {
        assertThrows(NoSuchMethodException.class, () -> {
            MethodHandleCreator.create(Victim.class, "non-existent");
        });
    }

    @Test
    public void noArgUniversal() throws Throwable {
        final MethodHandle methodHandle = MethodHandleCreator.createUniversal(Victim.class.getName(), "noArg");
        final Object result = methodHandle.invokeExact((Object) VICTIM);
        assertNull(result);
    }

    @Test
    public void noArgMethod() throws NoSuchMethodException, IllegalAccessException {
        final MethodHandle methodHandle = MethodHandleCreator.create(Victim.class, "noArg");
        assertDoesNotThrow(() -> {
            methodHandle.invokeExact(VICTIM);
        });
    }

    @Test
    public void privateMethod() throws Throwable {
        final MethodHandle methodHandle = MethodHandleCreator.create(Victim.class, "concat", int.class, int.class);
        assertEquals("1-2", (String) methodHandle.invokeExact(VICTIM, 1, 2));
    }

    @Test
    public void packagePrivateMethod() throws Throwable {
        final MethodHandle methodHandle = MethodHandleCreator.create(Victim.class, "concat", int.class, int.class, String.class);
        assertEquals("1-2-str", (String) methodHandle.invokeExact(VICTIM, 1, 2, "str"));
    }

    @Test
    public void proxiedMethod() throws Throwable {
        final MethodHandle methodHandle = MethodHandleCreator.create(Victim.class.getName(), "proxied");
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Victim.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            final int result = (int) proxy.invokeSuper(obj, args);
            return result + 1;
        });
        final Victim proxy = (Victim) enhancer.create();

        assertEquals(2, (int) methodHandle.invokeExact(proxy));
    }
}