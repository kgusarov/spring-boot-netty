package org.kgusarov.integration.spring.netty.etc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
