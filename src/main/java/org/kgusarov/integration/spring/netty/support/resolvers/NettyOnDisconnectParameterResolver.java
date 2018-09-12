package org.kgusarov.integration.spring.netty.support.resolvers;

import io.netty.channel.ChannelFuture;

/**
 * Classes that implement this interface are used to resolve the arguments
 * for the {@link org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect}
 * handler methods
 */
public interface NettyOnDisconnectParameterResolver extends NettyCallbackParameterResolver {
    /**
     * Resolve the value of appropriate method parameter
     *
     * @param future                Netty channel close future
     * @return                      Resolved value for method parameter
     */
    Object resolve(ChannelFuture future);
}
