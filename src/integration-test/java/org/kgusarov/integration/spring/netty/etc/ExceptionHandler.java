package org.kgusarov.integration.spring.netty.etc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelHandlerAdapter {
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
