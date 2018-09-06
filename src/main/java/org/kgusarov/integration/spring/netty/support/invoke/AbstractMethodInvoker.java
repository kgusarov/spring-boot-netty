package org.kgusarov.integration.spring.netty.support.invoke;

import io.netty.channel.Channel;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Internal API: invocation support for {@link org.kgusarov.integration.spring.netty.annotations.NettyController}
 * methods
 */
abstract class AbstractMethodInvoker {
    private static final Object[] EMPTY_ARGS = new Object[0];

    private final Object bean;
    private final Method method;
    private final boolean sendResult;

    AbstractMethodInvoker(final Object bean, final Method method, final boolean sendResult) {
        this.bean = bean;
        this.method = method;
        this.sendResult = sendResult;
    }

    void invokeHandler(final Channel channel, final Object[] args) {
        final Object result = args.length == 0?
                ReflectionUtils.invokeMethod(method, bean, EMPTY_ARGS) :
                ReflectionUtils.invokeMethod(method, bean, args);

        if (sendResult) {
            channel.writeAndFlush(result);
        }
    }
}
