package org.kgusarov.integration.spring.netty.onmessage.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.nio.charset.Charset;
import java.util.List;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@NettyFilter(serverName = "server1", priority = 1)
public class Decoder extends ReplayingDecoder<Object> {
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        final byte b = in.readByte();
        if (b == 0) {
            out.add(in.readLong());
        } else {
            final int size = in.readInt();
            final byte[] bytes = new byte[size];

            in.readBytes(bytes, 0, size);

            out.add(new String(bytes, Charset.forName("UTF-8")));
        }
    }
}
