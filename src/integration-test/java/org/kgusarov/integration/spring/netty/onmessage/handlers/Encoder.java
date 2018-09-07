package org.kgusarov.integration.spring.netty.onmessage.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.nio.charset.Charset;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@NettyFilter(serverName = "server1", priority = 2)
public class Encoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) {
        if (msg instanceof Long) {
            out.writeByte(0);
            out.writeLong((Long) msg);
        } else {
            final String s = (String) msg;
            final byte[] bytes = s.getBytes(Charset.forName("UTF-8"));

            out.writeByte(1);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
