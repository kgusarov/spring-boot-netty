package org.kgusarov.integration.spring.netty.support.invoke;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnConnectParameterResolver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Internal API: invocation support for {@link org.kgusarov.integration.spring.netty.annotations.NettyOnConnect}
 */
@SuppressWarnings("Guava")
public class OnConnectMethodInvoker extends AbstractMethodInvoker {
    private final List<NettyOnConnectParameterResolver> parameterResolvers;

    public OnConnectMethodInvoker(final Object bean, final Method method,
                                  final List<NettyOnConnectParameterResolver> parameterResolvers,
                                  final boolean sendResult) {

        super(bean, method, sendResult);
        this.parameterResolvers = parameterResolvers;
    }

    public void channelActive(final ChannelHandlerContext ctx) {
        final Function<NettyOnConnectParameterResolver, Object> fn = pr -> pr.resolve(ctx);

        @SuppressWarnings("StaticPseudoFunctionalStyleMethod")
        final Object[] args = Lists.transform(parameterResolvers, fn).toArray();
        final Channel channel = ctx.channel();
        invokeHandler(channel, args);
    }
}
