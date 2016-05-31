package org.kgusarov.integration.spring.netty.errors.nonsharable.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.kgusarov.integration.spring.netty.annotations.PreHandler;

import java.util.List;

@PreHandler(serverName = "server1", priority = 0)
public class Decoder extends ReplayingDecoder<Object> {
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in,
                          final List<Object> out) throws Exception {
        out.add(null);
    }
}
