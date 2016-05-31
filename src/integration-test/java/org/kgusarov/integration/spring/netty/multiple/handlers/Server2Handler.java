package org.kgusarov.integration.spring.netty.multiple.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.kgusarov.integration.spring.netty.annotations.PreHandler;

import static io.netty.buffer.Unpooled.copiedBuffer;

@ChannelHandler.Sharable
@PreHandler(serverName = "server2")
public class Server2Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final ByteBuf byteBuf = (ByteBuf) msg;
        final String hexDump = ByteBufUtil.hexDump(byteBuf);
        final ByteBuf response = copiedBuffer(hexDump, CharsetUtil.UTF_8);

        ctx.writeAndFlush(response);
    }
}
