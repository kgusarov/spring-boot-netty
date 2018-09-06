package org.kgusarov.integration.spring.netty.onconnect.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.etc.CyclicWaitForProcessingToComplete;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCallStack;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

@NettyController
public class OnConnectController {
    public static final Method ON_CONNECT1;
    public static final Method ON_CONNECT2;

    static {
        try {
            ON_CONNECT1 = OnConnectController.class.getDeclaredMethod("onConnect1",
                    ChannelHandlerContext.class, Channel.class);
            ON_CONNECT2 = OnConnectController.class.getDeclaredMethod("onConnect2");
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCallStack handlerCallStack;

    @Autowired
    private CyclicWaitForProcessingToComplete counter;

    @NettyOnConnect(serverName = "server1", priority = 1)
    private void onConnect1(final ChannelHandlerContext ctx, final Channel channel) {
        handlerCallStack.add(ON_CONNECT1);
        counter.arrive();

        final ByteBuf r1 = Unpooled.copyLong(92L);
        final ByteBuf r2 = Unpooled.copyLong(87L);

        ctx.writeAndFlush(r1);
        channel.writeAndFlush(r2);
    }

    @SuppressWarnings("WeakerAccess")
    @NettyOnConnect(serverName = "server1", priority = 2)
    ByteBuf onConnect2() {
        handlerCallStack.add(ON_CONNECT2);
        counter.arrive();
        return Unpooled.copyLong(106L);
    }
}
