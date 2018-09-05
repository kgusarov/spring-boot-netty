package org.kgusarov.integration.spring.netty.support;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Skeleton handler that is part of internal API and is used to invoke appropriate
 * {@link org.kgusarov.integration.spring.netty.annotations.NettyController}
 * annotated methods
 */
public class SkeletonChannelHandler extends ChannelInboundHandlerAdapter implements ChannelFutureListener {
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
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
