package org.kgusarov.integration.spring.netty.support.resolvers.impl;

import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnConnectParameterResolver;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

/**
 * Internal API: resolver for {@link ChannelHandlerContext}
 */
@Component
public class ChannelHandlerContextOnConnectResolver implements NettyOnConnectParameterResolver {
    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        final Class<?> parameterType = methodParameter.getParameterType();
        return ChannelHandlerContext.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolve(final ChannelHandlerContext ctx) {
        return ctx;
    }
}
