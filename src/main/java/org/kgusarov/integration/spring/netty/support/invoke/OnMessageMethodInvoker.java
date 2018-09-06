package org.kgusarov.integration.spring.netty.support.invoke;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnMessageParameterResolver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Internal API: invocation support for {@link org.kgusarov.integration.spring.netty.annotations.NettyOnMessage}
 */
@SuppressWarnings("Guava")
public final class OnMessageMethodInvoker extends AbstractMethodInvoker {
    private final List<NettyOnMessageParameterResolver> parameterResolvers;

    public OnMessageMethodInvoker(final Object bean, final Method method,
                                  final List<NettyOnMessageParameterResolver> parameterResolvers,
                                  final boolean sendResult) {

        super(bean, method, sendResult);
        this.parameterResolvers = parameterResolvers;
    }

    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final Function<NettyOnMessageParameterResolver, Object> fn = pr -> pr.resolve(ctx, msg);

        @SuppressWarnings("StaticPseudoFunctionalStyleMethod")
        final Object[] args = Lists.transform(parameterResolvers, fn).toArray();
        final Channel channel = ctx.channel();
        invokeHandler(channel, args);
    }
}
