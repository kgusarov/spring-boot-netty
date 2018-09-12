package org.kgusarov.integration.spring.netty.support.resolvers.impl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnDisconnectParameterResolver;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

/**
 * Internal API: resolver for {@link Channel}
 */
@Component
public class ChannelOnDisconnectResolver implements NettyOnDisconnectParameterResolver {
    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        final Class<?> parameterType = methodParameter.getParameterType();
        return Channel.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolve(final ChannelFuture future) {
        return future.channel();
    }
}
