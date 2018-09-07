package org.kgusarov.integration.spring.netty.ondisconnect.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

@NettyController
public class OnDisconnectController {
    public static final Method ON_DISCONNECT1;
    public static final Method ON_DISCONNECT2;

    static {
        try {
            ON_DISCONNECT1 = OnDisconnectController.class.getDeclaredMethod("onDisconnect1",
                    ChannelFuture.class, Channel.class);
            ON_DISCONNECT2 = OnDisconnectController.class.getDeclaredMethod("onDisconnect2");
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCalls calls;

    @Autowired
    private ProcessingCounter counter;

    @NettyOnDisconnect(serverName = "server1", priority = 1)
    private void onDisconnect1(final ChannelFuture future, final Channel channel) {
        calls.add(ON_DISCONNECT1);
        counter.arrive();

        final ByteBuf r1 = Unpooled.copyLong(92L);
        final ByteBuf r2 = Unpooled.copyLong(87L);

        try {
            future.channel().writeAndFlush(r1);
            channel.writeAndFlush(r2);
        } catch (final Exception ignored) {
        }
    }

    @SuppressWarnings("WeakerAccess")
    @NettyOnDisconnect(serverName = "server1", priority = 2)
    ByteBuf onDisconnect2() {
        calls.add(ON_DISCONNECT2);
        counter.arrive();
        return Unpooled.copyLong(106L);
    }
}
