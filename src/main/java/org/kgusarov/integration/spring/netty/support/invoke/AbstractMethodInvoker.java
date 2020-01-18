package org.kgusarov.integration.spring.netty.support.invoke;

import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyCallbackParameterResolver;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

/**
 * Internal API: invocation support for {@link org.kgusarov.integration.spring.netty.annotations.NettyController}
 * methods
 */
abstract class AbstractMethodInvoker {
    private final Object bean;
    private final Method method;
    private final boolean sendResult;
    private final Supplier<List<Object>> argListBuilder;

    @SuppressWarnings("NestedMethodCall")
    AbstractMethodInvoker(final Object bean, final Method method, final boolean sendResult,
                          final List<? extends NettyCallbackParameterResolver> parameterResolvers) {

        this.bean = bean;
        this.method = method;
        this.sendResult = sendResult;
        argListBuilder = () -> Lists.newArrayListWithExpectedSize(parameterResolvers.size());
    }

    final List<Object> buildArgList() {
        return argListBuilder.get();
    }

    final void invokeHandler(final Channel channel, final Object[] args) {
        final Object result = ReflectionUtils.invokeMethod(method, bean, args);
        if (sendResult) {
            channel.writeAndFlush(result);
        }
    }
}
