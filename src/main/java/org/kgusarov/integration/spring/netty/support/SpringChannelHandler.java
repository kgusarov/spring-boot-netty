package org.kgusarov.integration.spring.netty.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kgusarov.integration.spring.netty.support.invoke.OnConnectMethodInvoker;
import org.kgusarov.integration.spring.netty.support.invoke.OnMessageMethodInvoker;

import java.util.List;

/**
 * Handler that is part of internal API and is used to invoke appropriate
 * {@link org.kgusarov.integration.spring.netty.annotations.NettyController}
 * annotated methods
 */
@SuppressWarnings("CodeBlock2Expr")
public class SpringChannelHandler extends ChannelInboundHandlerAdapter{
    private final List<OnConnectMethodInvoker> onConnectCallbacks;
    private final List<OnMessageMethodInvoker> onMessageCallbacks;

    public SpringChannelHandler(final List<OnConnectMethodInvoker> onConnectCallbacks,
                                final List<OnMessageMethodInvoker> onMessageCallbacks) {

        this.onConnectCallbacks = onConnectCallbacks;
        this.onMessageCallbacks = onMessageCallbacks;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        onConnectCallbacks.forEach(cb -> {
            cb.channelActive(ctx);
        });

        super.channelActive(ctx);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        onMessageCallbacks.forEach(cb -> {
            cb.channelRead(ctx, msg);
        });

        super.channelRead(ctx, msg);
    }
}
