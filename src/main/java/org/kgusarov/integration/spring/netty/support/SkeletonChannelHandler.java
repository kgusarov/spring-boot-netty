package org.kgusarov.integration.spring.netty.support;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kgusarov.integration.spring.netty.support.invoke.OnConnectMethodInvoker;

import java.util.List;

/**
 * Skeleton handler that is part of internal API and is used to invoke appropriate
 * {@link org.kgusarov.integration.spring.netty.annotations.NettyController}
 * annotated methods
 */
public class SkeletonChannelHandler extends ChannelInboundHandlerAdapter implements ChannelFutureListener {
    private final List<OnConnectMethodInvoker> onConnectCallbacks;

    public SkeletonChannelHandler(final List<OnConnectMethodInvoker> onConnectCallbacks) {
        this.onConnectCallbacks = onConnectCallbacks;
    }

    @Override
    @SuppressWarnings("CodeBlock2Expr")
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        onConnectCallbacks.forEach(cb -> {
            cb.channelActive(ctx);
        });

        super.channelActive(ctx);
    }

    @Override
    public void operationComplete(final ChannelFuture channelFuture) throws Exception {

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
