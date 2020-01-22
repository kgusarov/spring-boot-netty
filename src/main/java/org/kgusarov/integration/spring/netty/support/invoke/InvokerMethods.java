package org.kgusarov.integration.spring.netty.support.invoke;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

/**
 * Internal API: code generation support - invoker methods
 */
final class InvokerMethods {
    static final Method ONC_INVOKE_HANDLER;
    static final Method OND_INVOKE_HANDLER;
    static final Method ONM_INVOKE_HANDLER;

    static {
        try {
            ONC_INVOKE_HANDLER = OnConnectMethodInvoker.Invoker.class.getDeclaredMethod("invokeHandler",
                    Channel.class, ChannelHandlerContext.class);

            OND_INVOKE_HANDLER = OnDisconnectMethodInvoker.Invoker.class.getDeclaredMethod("invokeHandler",
                    Channel.class, ChannelFuture.class);

            ONM_INVOKE_HANDLER = OnMessageMethodInvoker.Invoker.class.getDeclaredMethod("invokeHandler",
                    Channel.class, ChannelHandlerContext.class, Object.class);
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private InvokerMethods() {
    }
}
