package org.kgusarov.integration.spring.netty.support.invoke;

import com.google.common.primitives.Primitives;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.annotations.NettyMessageBody;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnMessageParameterResolver;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.kgusarov.integration.spring.netty.support.invoke.InvokerMethods.ONM_INVOKE_HANDLER;
import static org.kgusarov.integration.spring.netty.support.invoke.assembler.Descriptors.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Internal API: invocation support for {@link org.kgusarov.integration.spring.netty.annotations.NettyOnMessage}
 */
public final class OnMessageMethodInvoker extends AbstractMethodInvoker {
    @SuppressWarnings("AbstractClassNeverImplemented")
    abstract static class Invoker extends InvokerBase {
        NettyOnMessageParameterResolver[] resolvers;

        abstract void invokeHandler(final Channel channel, final ChannelHandlerContext ctx, final Object msg);
    }

    private final Invoker invoker;
    private final Class<?> messageBodyType;

    @SuppressWarnings("NestedMethodCall")
    public OnMessageMethodInvoker(final Object bean, final Method method,
                                  final List<NettyOnMessageParameterResolver> parameterResolvers,
                                  final boolean sendResult) {

        final int parameterCount = method.getParameterCount();

        //noinspection NestedMethodCall
        final List<MethodParameter> messageBodyArgs = IntStream.range(0, parameterCount)
                .mapToObj(i -> new MethodParameter(method, i))
                .filter(mp -> mp.hasParameterAnnotation(NettyMessageBody.class))
                .collect(Collectors.toList());

        if (messageBodyArgs.size() > 1) {
            throw new IllegalArgumentException(method + " has more than one NettyMessageBody annotated parameters");
        }

        messageBodyType = Optional.ofNullable(
                messageBodyArgs.size() == 1 ? messageBodyArgs.get(0).getParameterType() : null
        )
                .map(c -> c.isPrimitive() ? Primitives.wrap(c) : c)
                .orElse(null);

        invoker = buildInvoker(Invoker.class, method, ONM_INVOKE_HANDLER, sendResult, (invokerInternalName, m, firstVarIdx) -> {
            final Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                m.visitIntInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, invokerInternalName, "resolvers", ONM_RESA_DESCRIPTOR);
                m.visitIntInsn(BIPUSH, i);
                m.visitInsn(AALOAD);
                m.visitIntInsn(ALOAD, 2);
                m.visitIntInsn(ALOAD, 3);
                m.visitMethodInsn(INVOKEINTERFACE, ONM_RES_INTERNAL_NAME, "resolve", ONM_RESOLVE_DESCRIPTOR, true);
                m.visitIntInsn(ASTORE, firstVarIdx + i);
            }
        });

        invoker.bean = bean;
        invoker.resolvers = parameterResolvers.toArray(new NettyOnMessageParameterResolver[0]);
    }

    public Class<?> getMessageBodyType() {
        return messageBodyType;
    }

    @SuppressWarnings("NestedMethodCall")
    public boolean channelRead(final ChannelHandlerContext ctx, final Object msg) {
        if ((messageBodyType != null) && !messageBodyType.isAssignableFrom(msg.getClass())) {
            return false;
        }

        final Channel channel = ctx.channel();
        invoker.invokeHandler(channel, ctx, msg);
        return true;
    }
}
