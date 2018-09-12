package org.kgusarov.integration.spring.netty.support.resolvers.impl;

import io.netty.channel.ChannelFuture;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnDisconnectParameterResolver;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

/**
 * Internal API: resolver for {@link ChannelFuture}
 */
@Component
public class ChannelFutureOnDisconnectResolver implements NettyOnDisconnectParameterResolver {
    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        final Class<?> parameterType = methodParameter.getParameterType();
        return ChannelFuture.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolve(final ChannelFuture future) {
        return future;
    }
}
