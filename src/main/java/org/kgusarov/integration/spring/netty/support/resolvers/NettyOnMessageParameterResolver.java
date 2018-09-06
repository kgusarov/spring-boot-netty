package org.kgusarov.integration.spring.netty.support.resolvers;

import io.netty.channel.ChannelHandlerContext;

/**
 * Classes that implement this interface are used to resolve the arguments
 * for the {@link org.kgusarov.integration.spring.netty.annotations.NettyOnMessage}
 * handler methods
 */
public interface NettyOnMessageParameterResolver extends NettyCallbackParameterResolver {
    /**
     * Resolve the value of appropriate method parameter
     *
     * @param ctx                   Context provided by Netty callback
     * @param msg                   Message received from upstream
     * @return                      Resolved value for method parameter
     */
    Object resolve(ChannelHandlerContext ctx, Object msg);
}
