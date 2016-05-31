package org.kgusarov.integration.spring.netty.multiple.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kgusarov.integration.spring.netty.annotations.PreHandler;

@ChannelHandler.Sharable
@PreHandler(serverName = "server1")
public class Server1Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        ctx.writeAndFlush(msg);
    }
}
