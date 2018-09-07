package org.kgusarov.integration.spring.netty.onmessage.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnMessage;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@NettyController
public class TransactionalOnMessageController {
    public static final Method ON_MESSAGE;

    static {
        try {
            ON_MESSAGE = TransactionalOnMessageController.class.getDeclaredMethod("onMessage",
                    ChannelHandlerContext.class, Channel.class);
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCalls calls;

    @Autowired
    private ProcessingCounter counter;

    @Transactional
    @NettyOnMessage(serverName = "server1", priority = 5)
    public void onMessage(final ChannelHandlerContext ctx, final Channel channel) {
        calls.add(ON_MESSAGE);
        counter.arrive();

        ctx.writeAndFlush(889L);
        channel.writeAndFlush(26576374L);
    }
}
