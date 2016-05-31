package org.kgusarov.integration.spring.netty.etc;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private final SettableFuture<Long>[] responseHolders;
    private int currentResponse;

    public ClientHandler(final SettableFuture<Long>... responseHolders) {
        this.responseHolders = responseHolders;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final ByteBuf buf = (ByteBuf) msg;
        final long i = buf.readLong();

        responseHolders[currentResponse++].set(i);
    }
}
