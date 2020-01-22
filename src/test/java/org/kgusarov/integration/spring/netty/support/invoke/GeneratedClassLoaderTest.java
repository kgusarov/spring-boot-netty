package org.kgusarov.integration.spring.netty.support.invoke;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.Assert.*;

@SuppressWarnings({"CodeBlock2Expr", "StringConcatenationMissingWhitespace"})
public class GeneratedClassLoaderTest {
    private final GeneratedClassLoader cl = new GeneratedClassLoader();

    @Test
    public void invalidClass() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            cl.load(new byte[] {1, 2, 3}, "org.kgusarov.Test");
        });
    }

    @Test
    public void success() throws NoSuchFieldException {
        final byte[] bytes = Base64.getDecoder().decode(
                "yv66vgAAADQALwEAXW9yZy9rZ3VzYXJvdi9pbnRlZ3JhdGlvbi9zcHJpbmcvbmV0dHkvc3VwcG9ydC9pbnZva2UvRHlu" +
                        "YW1pY0ludm9rZXIkJEdlbmVyYXRlZENsYXNzJCQxMDAwMDAwNQcAAQEAU29yZy9rZ3VzYXJvdi9pbnRlZ3JhdGlvb" +
                        "i9zcHJpbmcvbmV0dHkvc3VwcG9ydC9pbnZva2UvT25Db25uZWN0TWV0aG9kSW52b2tlciRJbnZva2VyBwADAQAGPG" +
                        "luaXQ+AQADKClWDAAFAAYKAAQABwEABHRoaXMBAF9Mb3JnL2tndXNhcm92L2ludGVncmF0aW9uL3NwcmluZy9uZXR" +
                        "0eS9zdXBwb3J0L2ludm9rZS9EeW5hbWljSW52b2tlciQkR2VuZXJhdGVkQ2xhc3MkJDEwMDAwMDA1OwEABkhBTkRM" +
                        "RQEAH0xqYXZhL2xhbmcvaW52b2tlL01ldGhvZEhhbmRsZTsBAAg8Y2xpbml0PgEAWW9yZy5rZ3VzYXJvdi5pbnRlZ" +
                        "3JhdGlvbi5zcHJpbmcubmV0dHkub25jb25uZWN0LmhhbmRsZXJzLlRyYW5zYWN0aW9uYWxPbkNvbm5lY3RDb250cm" +
                        "9sbGVyCAAOAQAJb25Db25uZWN0CAAQAwAAAAABAA9qYXZhL2xhbmcvQ2xhc3MHABMBAEhvcmcva2d1c2Fyb3YvaW5" +
                        "0ZWdyYXRpb24vc3ByaW5nL25ldHR5L3N1cHBvcnQvaW52b2tlL01ldGhvZEhhbmRsZUNyZWF0b3IHABUBAA9jcmVh" +
                        "dGVVbml2ZXJzYWwBAFcoTGphdmEvbGFuZy9TdHJpbmc7TGphdmEvbGFuZy9TdHJpbmc7W0xqYXZhL2xhbmcvQ2xhc" +
                        "3M7KUxqYXZhL2xhbmcvaW52b2tlL01ldGhvZEhhbmRsZTsMABcAGAoAFgAZDAALAAwJAAIAGwEADWludm9rZUhhbm" +
                        "RsZXIBAEUoTGlvL25ldHR5L2NoYW5uZWwvQ2hhbm5lbDtMaW8vbmV0dHkvY2hhbm5lbC9DaGFubmVsSGFuZGxlckN" +
                        "vbnRleHQ7KVYBAARiZWFuAQASTGphdmEvbGFuZy9PYmplY3Q7DAAfACAJAAIAIQEAHWphdmEvbGFuZy9pbnZva2Uv" +
                        "TWV0aG9kSGFuZGxlBwAjAQALaW52b2tlRXhhY3QBACYoTGphdmEvbGFuZy9PYmplY3Q7KUxqYXZhL2xhbmcvT2JqZ" +
                        "WN0OwwAJQAmCgAkACcBAARhcmcwAQAaTGlvL25ldHR5L2NoYW5uZWwvQ2hhbm5lbDsBAARhcmcxAQAoTGlvL25ldH" +
                        "R5L2NoYW5uZWwvQ2hhbm5lbEhhbmRsZXJDb250ZXh0OwEABENvZGUBABJMb2NhbFZhcmlhYmxlVGFibGUAIQACAAQ" +
                        "AAAABABoACwAMAAAAAwABAAUABgABAC0AAAAjAAEAAQAAAAUqtwAIsQAAAAEALgAAAAwAAQAAAAUACQAKAAAACAAN" +
                        "AAYAAQAtAAAAHAADAAAAAAAQEg8SERISvQAUuAAaswAcsQAAAAAAAQAdAB4AAQAtAAAAPwACAAMAAAANsgAcGQC0A" +
                        "CK2AChXsQAAAAEALgAAACAAAwAAAA0ACQAKAAAAAAANACkAKgABAAAADQArACwAAgAA");

        final Class<Object> clazz = cl.load(bytes,
                "org.kgusarov.integration.spring.netty.support.invoke.DynamicInvoker$$GeneratedClass$$10000005");
        assertNotNull(clazz);

        final Field handle = clazz.getDeclaredField("HANDLE");
        assertNotNull(handle);
    }
}