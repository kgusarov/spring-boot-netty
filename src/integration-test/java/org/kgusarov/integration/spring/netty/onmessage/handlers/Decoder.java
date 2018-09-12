package org.kgusarov.integration.spring.netty.onmessage.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@NettyFilter(serverName = "server1", priority = 1)
public class Decoder extends ReplayingDecoder<Object> {
    public static final Method DECODE;

    static {
        try {
            DECODE = Decoder.class.getDeclaredMethod("decode", ChannelHandlerContext.class,
                    ByteBuf.class, List.class);
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCalls calls;

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

        if (calls != null) {
            calls.add(DECODE);
        }
    }
}
