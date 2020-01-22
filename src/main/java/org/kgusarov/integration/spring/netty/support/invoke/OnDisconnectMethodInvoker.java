package org.kgusarov.integration.spring.netty.support.invoke;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnDisconnectParameterResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import static org.kgusarov.integration.spring.netty.support.invoke.InvokerMethods.OND_INVOKE_HANDLER;
import static org.kgusarov.integration.spring.netty.support.invoke.assembler.Descriptors.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Internal API: invocation support for {@link NettyOnDisconnect}
 */
public final class OnDisconnectMethodInvoker extends AbstractMethodInvoker {
    @SuppressWarnings("AbstractClassNeverImplemented")
    abstract static class Invoker extends InvokerBase {
        NettyOnDisconnectParameterResolver[] resolvers;

        abstract void invokeHandler(final Channel channel, final ChannelFuture channelFuture);
    }

    private final Invoker invoker;

    public OnDisconnectMethodInvoker(final Object bean, final Method method,
                                     final List<NettyOnDisconnectParameterResolver> parameterResolvers) {

        invoker = buildInvoker(Invoker.class, method, OND_INVOKE_HANDLER, false, (invokerInternalName, m, firstVarIdx) -> {
            final Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                m.visitIntInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, invokerInternalName, "resolvers", OND_RESA_DESCRIPTOR);
                m.visitIntInsn(BIPUSH, i);
                m.visitInsn(AALOAD);
                m.visitIntInsn(ALOAD, 2);
                m.visitMethodInsn(INVOKEINTERFACE, OND_RES_INTERNAL_NAME, "resolve", OND_RESOLVE_DESCRIPTOR, true);
                m.visitIntInsn(ASTORE, firstVarIdx + i);
            }
        });

        invoker.bean = bean;
        invoker.resolvers = parameterResolvers.toArray(new NettyOnDisconnectParameterResolver[0]);
    }

    public void channelClosed(final ChannelFuture channelFuture) {
        final Channel channel = channelFuture.channel();
        invoker.invokeHandler(channel, channelFuture);
    }
}
