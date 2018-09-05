package org.kgusarov.integration.spring.netty.support.resolvers;

import org.springframework.core.MethodParameter;

/**
 * Classes that implement this interface are used to resolve the arguments
 * for the {@link org.kgusarov.integration.spring.netty.annotations.NettyOnConnect}
 * handler methods
 */
public interface NettyOnConnectParameterResolver {
    /**
     * If this resolver can be used with appropriate method parameter
     *
     * @param methodParameter       Method parameter to check
     * @return                      {@code true} if this resolver can extract value for given method parameter
     */
    boolean canResolve(MethodParameter methodParameter);
}
