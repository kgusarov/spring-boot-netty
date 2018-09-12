package org.kgusarov.integration.spring.netty.customresolvers.resolvers;

import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnConnectParameterResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Component
public class RandomLongOnConnectResolver implements NettyOnConnectParameterResolver {
    private final RNG rng;

    @Autowired
    public RandomLongOnConnectResolver(final RNG rng) {
        this.rng = rng;
    }

    @Override
    public Object resolve(final ChannelHandlerContext ctx) {
        return rng.nextLong();
    }

    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        final Class<?> parameterType = methodParameter.getParameterType();
        return long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType);
    }
}
