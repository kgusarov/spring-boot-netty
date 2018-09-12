package org.kgusarov.integration.spring.netty.customresolvers.resolvers;

import io.netty.channel.ChannelFuture;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnDisconnectParameterResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Component
public class RandomLongOnDisconnectResolver implements NettyOnDisconnectParameterResolver {
    private final RNG rng;

    @Autowired
    public RandomLongOnDisconnectResolver(final RNG rng) {
        this.rng = rng;
    }

    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        final Class<?> parameterType = methodParameter.getParameterType();
        return long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolve(final ChannelFuture future) {
        return rng.nextLong();
    }
}
