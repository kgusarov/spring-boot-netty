package org.kgusarov.integration.spring.netty.onmessage.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.Method;
import java.nio.charset.Charset;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@NettyFilter(serverName = "server1", priority = 2)
public class Encoder extends MessageToByteEncoder<Object> {
    public static final Method ENCODE;

    static {
        try {
            ENCODE = Encoder.class.getDeclaredMethod("encode", ChannelHandlerContext.class,
                    Object.class, ByteBuf.class);
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCalls calls;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) {
        if (calls != null) {
            calls.add(ENCODE);
        }

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
