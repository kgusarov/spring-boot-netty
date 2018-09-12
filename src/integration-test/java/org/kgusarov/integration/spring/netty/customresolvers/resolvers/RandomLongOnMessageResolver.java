package org.kgusarov.integration.spring.netty.customresolvers.resolvers;

import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnMessageParameterResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Component
public class RandomLongOnMessageResolver implements NettyOnMessageParameterResolver {
    private final RNG rng;

    @Autowired
    public RandomLongOnMessageResolver(final RNG rng) {
        this.rng = rng;
    }

    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        final Class<?> parameterType = methodParameter.getParameterType();
        return long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolve(final ChannelHandlerContext ctx, final Object msg) {
        return rng.nextLong();
    }
}
