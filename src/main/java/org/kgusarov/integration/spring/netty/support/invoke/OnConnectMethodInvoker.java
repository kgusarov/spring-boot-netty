package org.kgusarov.integration.spring.netty.support.invoke;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnConnectParameterResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import static org.kgusarov.integration.spring.netty.support.invoke.InvokerMethods.ONC_INVOKE_HANDLER;
import static org.kgusarov.integration.spring.netty.support.invoke.assembler.Descriptors.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Internal API: invocation support for {@link NettyOnConnect}
 */
public final class OnConnectMethodInvoker extends AbstractMethodInvoker {
    @SuppressWarnings("AbstractClassNeverImplemented")
    abstract static class Invoker extends InvokerBase {
        NettyOnConnectParameterResolver[] resolvers;

        abstract void invokeHandler(final Channel channel, final ChannelHandlerContext ctx);
    }

    private final Invoker invoker;

    public OnConnectMethodInvoker(final Object bean, final Method method,
                                  final List<NettyOnConnectParameterResolver> parameterResolvers,
                                  final boolean sendResult) {

        invoker = buildInvoker(Invoker.class, method, ONC_INVOKE_HANDLER, sendResult, (invokerInternalName, m, firstVarIdx) -> {
            final Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                m.visitIntInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, invokerInternalName, "resolvers", ONC_RESA_DESCRIPTOR);
                m.visitIntInsn(BIPUSH, i);
                m.visitInsn(AALOAD);
                m.visitIntInsn(ALOAD, 2);
                m.visitMethodInsn(INVOKEINTERFACE, ONC_RES_INTERNAL_NAME, "resolve", ONC_RESOLVE_DESCRIPTOR, true);
                m.visitIntInsn(ASTORE, firstVarIdx + i);
            }
        });

        invoker.bean = bean;
        invoker.resolvers = parameterResolvers.toArray(new NettyOnConnectParameterResolver[0]);
    }

    public void channelActive(final ChannelHandlerContext ctx) {
        final Channel channel = ctx.channel();
        invoker.invokeHandler(channel, ctx);
    }
}
