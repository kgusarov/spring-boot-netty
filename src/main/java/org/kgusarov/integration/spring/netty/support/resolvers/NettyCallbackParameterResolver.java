package org.kgusarov.integration.spring.netty.support.resolvers;

import org.springframework.core.MethodParameter;

/**
 * Marker interface for parameter resolvers
 */
public interface NettyCallbackParameterResolver {
    /**
     * If this resolver can be used with appropriate method parameter
     *
     * @param methodParameter       Method parameter to check
     * @return                      {@code true} if this resolver can extract value for given method parameter
     */
    boolean canResolve(MethodParameter methodParameter);
}
