package org.kgusarov.integration.spring.netty.support.invoke;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnConnectParameterResolver;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * Internal API: invocation support for {@link NettyOnConnect}
 */
public final class OnConnectMethodInvoker extends AbstractMethodInvoker {
    private final List<NettyOnConnectParameterResolver> parameterResolvers;

    public OnConnectMethodInvoker(final Object bean, final Method method,
                                  final List<NettyOnConnectParameterResolver> parameterResolvers,
                                  final boolean sendResult) {

        super(bean, method, sendResult);
        this.parameterResolvers = parameterResolvers;
    }

    public void channelActive(final ChannelHandlerContext ctx) {
        final Function<NettyOnConnectParameterResolver, @Nullable Object> fn = pr -> pr.resolve(ctx);
        final Object[] args = parameterResolvers.stream()
                .map(fn)
                .toArray();
        final Channel channel = ctx.channel();

        invokeHandler(channel, args);
    }
}
