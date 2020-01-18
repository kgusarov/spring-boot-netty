package org.kgusarov.integration.spring.netty.support.invoke;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnDisconnectParameterResolver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Internal API: invocation support for {@link NettyOnDisconnect}
 */
public final class OnDisconnectMethodInvoker extends AbstractMethodInvoker {
    private final List<NettyOnDisconnectParameterResolver> parameterResolvers;

    public OnDisconnectMethodInvoker(final Object bean, final Method method,
                                     final List<NettyOnDisconnectParameterResolver> parameterResolvers) {

        super(bean, method, false, parameterResolvers);
        this.parameterResolvers = parameterResolvers;
    }

    public void channelClosed(final ChannelFuture channelFuture) {
        final List<Object> argList = buildArgList();
        for (final NettyOnDisconnectParameterResolver pr : parameterResolvers) {
            final Object arg = pr.resolve(channelFuture);
            argList.add(arg);
        }

        final Object[] args = argList.toArray();
        final Channel channel = channelFuture.channel();

        invokeHandler(channel, args);
    }
}
