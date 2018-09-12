package org.kgusarov.integration.spring.netty.support.invoke;

import com.google.common.primitives.Primitives;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kgusarov.integration.spring.netty.annotations.NettyMessageBody;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnMessageParameterResolver;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Internal API: invocation support for {@link org.kgusarov.integration.spring.netty.annotations.NettyOnMessage}
 */
public final class OnMessageMethodInvoker extends AbstractMethodInvoker {
    private final List<NettyOnMessageParameterResolver> parameterResolvers;
    private final Class<?> messageBodyType;

    public OnMessageMethodInvoker(final Object bean, final Method method,
                                  final List<NettyOnMessageParameterResolver> parameterResolvers,
                                  final boolean sendResult) {

        super(bean, method, sendResult);
        this.parameterResolvers = parameterResolvers;

        final int parameterCount = method.getParameterCount();

        //noinspection NestedMethodCall
        final List<MethodParameter> messageBodyArgs = IntStream.range(0, parameterCount)
                .mapToObj(i -> new MethodParameter(method, i))
                .filter(mp -> mp.hasParameterAnnotation(NettyMessageBody.class))
                .collect(Collectors.toList());

        if (messageBodyArgs.size() > 1) {
            throw new IllegalArgumentException(method + " has more than one NettyMessageBody annotated parameters");
        }

        //noinspection NestedMethodCall
        messageBodyType = Optional.ofNullable(
                messageBodyArgs.size() == 1 ?
                        messageBodyArgs.get(0).getParameterType()
                        : null)
                .map(c -> c.isPrimitive() ? Primitives.wrap(c) : c)
                .orElse(null);
    }

    public Class<?> getMessageBodyType() {
        return messageBodyType;
    }

    @SuppressWarnings("NestedMethodCall")
    public boolean channelRead(final ChannelHandlerContext ctx, final Object msg) {
        if ((messageBodyType != null) && !messageBodyType.isAssignableFrom(msg.getClass())) {
            return false;
        }

        final Function<NettyOnMessageParameterResolver, @Nullable Object> fn = pr -> pr.resolve(ctx, msg);
        final Object[] args = parameterResolvers.stream()
                .map(fn)
                .toArray();

        final Channel channel = ctx.channel();
        invokeHandler(channel, args);
        return true;
    }
}
