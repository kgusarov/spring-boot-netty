package org.kgusarov.integration.spring.netty.support.invoke;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnDisconnectParameterResolver;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * Internal API: invocation support for {@link NettyOnDisconnect}
 */
public final class OnDisconnectMethodInvoker extends AbstractMethodInvoker {
    private final List<NettyOnDisconnectParameterResolver> parameterResolvers;

    public OnDisconnectMethodInvoker(final Object bean, final Method method,
                                     final List<NettyOnDisconnectParameterResolver> parameterResolvers) {

        super(bean, method, false);
        this.parameterResolvers = parameterResolvers;
    }

    public void channelClosed(final ChannelFuture channelFuture) {
        final Function<NettyOnDisconnectParameterResolver, @Nullable Object> fn = pr -> pr.resolve(channelFuture);
        final Object[] args = parameterResolvers.stream()
                .map(fn)
                .toArray();
        final Channel channel = channelFuture.channel();

        invokeHandler(channel, args);
    }
}
