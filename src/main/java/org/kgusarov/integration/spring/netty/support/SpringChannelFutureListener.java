package org.kgusarov.integration.spring.netty.support;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.kgusarov.integration.spring.netty.support.invoke.OnDisconnectMethodInvoker;

import java.util.List;

/**
 * Handler that is part of internal API and is used to invoke appropriate
 * {@link org.kgusarov.integration.spring.netty.annotations.NettyController}
 * {@link org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect}
 * annotated methods
 */
public class SpringChannelFutureListener implements ChannelFutureListener {
    private final List<OnDisconnectMethodInvoker> onDisconnectCallbacks;

    public SpringChannelFutureListener(final List<OnDisconnectMethodInvoker> onDisconnectCallbacks) {
        this.onDisconnectCallbacks = onDisconnectCallbacks;
    }

    @Override
    @SuppressWarnings("CodeBlock2Expr")
    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
        onDisconnectCallbacks.forEach(cb -> {
            cb.channelClosed(channelFuture);
        });
    }
}
