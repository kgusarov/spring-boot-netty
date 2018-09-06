package org.kgusarov.integration.spring.netty.support.resolvers.impl;

import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.annotations.NettyMessageBody;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnMessageParameterResolver;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

/**
 * Internal API: resolver for message body
 */
@Component
public class MessageBodyOnMessageResolver implements NettyOnMessageParameterResolver {
    @Override
    public boolean canResolve(final MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(NettyMessageBody.class);
    }

    @Override
    public Object resolve(final ChannelHandlerContext ctx, final Object msg) {
        return msg;
    }
}
