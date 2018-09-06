package org.kgusarov.integration.spring.netty.support.resolvers;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.core.MethodParameter;

/**
 * Classes that implement this interface are used to resolve the arguments
 * for the {@link org.kgusarov.integration.spring.netty.annotations.NettyOnConnect}
 * handler methods
 */
public interface NettyOnConnectParameterResolver extends NettyCallbackParameterResolver {
    /**
     * Resolve the value of appropriate method parameter
     *
     * @param ctx                   Context provided by Netty callback
     * @return                      Resolved value for method parameter
     */
    Object resolve(final ChannelHandlerContext ctx);
}
