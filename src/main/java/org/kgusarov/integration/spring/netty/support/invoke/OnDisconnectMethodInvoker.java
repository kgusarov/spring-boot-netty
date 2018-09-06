package org.kgusarov.integration.spring.netty.support.invoke;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnDisconnectParameterResolver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Internal API: invocation support for {@link org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect}
 */
@SuppressWarnings("Guava")
public final class OnDisconnectMethodInvoker extends AbstractMethodInvoker {
    private final List<NettyOnDisconnectParameterResolver> parameterResolvers;

    public OnDisconnectMethodInvoker(final Object bean, final Method method,
                                     final List<NettyOnDisconnectParameterResolver> parameterResolvers) {

        super(bean, method, false);
        this.parameterResolvers = parameterResolvers;
    }

    public void channelClosed(final ChannelFuture channelFuture) {
        final Function<NettyOnDisconnectParameterResolver, Object> fn = pr -> pr.resolve(channelFuture);

        @SuppressWarnings("StaticPseudoFunctionalStyleMethod")
        final Object[] args = Lists.transform(parameterResolvers, fn).toArray();
        final Channel channel = channelFuture.channel();
        invokeHandler(channel, args);
    }
}
