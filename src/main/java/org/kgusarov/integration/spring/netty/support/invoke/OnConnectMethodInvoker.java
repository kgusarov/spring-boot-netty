package org.kgusarov.integration.spring.netty.support.invoke;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnConnectParameterResolver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Internal API: invocation support for {@link NettyOnConnect}
 */
public final class OnConnectMethodInvoker extends AbstractMethodInvoker {
    private final List<NettyOnConnectParameterResolver> parameterResolvers;

    public OnConnectMethodInvoker(final Object bean, final Method method,
                                  final List<NettyOnConnectParameterResolver> parameterResolvers,
                                  final boolean sendResult) {

        super(bean, method, sendResult, parameterResolvers);
        this.parameterResolvers = parameterResolvers;
    }

    public void channelActive(final ChannelHandlerContext ctx) {
        final List<Object> argList = buildArgList();
        for (final NettyOnConnectParameterResolver pr : parameterResolvers) {
            final Object arg = pr.resolve(ctx);
            argList.add(arg);
        }

        final Object[] args = argList.toArray();
        final Channel channel = ctx.channel();

        invokeHandler(channel, args);
    }
}
