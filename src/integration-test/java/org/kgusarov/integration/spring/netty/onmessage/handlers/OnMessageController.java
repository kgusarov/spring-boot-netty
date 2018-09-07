package org.kgusarov.integration.spring.netty.onmessage.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyMessageBody;
import org.kgusarov.integration.spring.netty.annotations.NettyOnMessage;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

@NettyController
@SuppressWarnings("WeakerAccess")
public class OnMessageController {
    public static final Method ON_MESSAGE1;
    public static final Method ON_MESSAGE2;
    public static final Method ON_MESSAGE3;
    public static final Method ON_STRING_MESSAGE;

    static {
        try {
            ON_MESSAGE1 = OnMessageController.class.getDeclaredMethod("onMessage1",
                    ChannelHandlerContext.class, Channel.class, Long.class);
            ON_MESSAGE2 = OnMessageController.class.getDeclaredMethod("onMessage2", long.class);
            ON_MESSAGE3 = OnMessageController.class.getDeclaredMethod("onMessage3");
            ON_STRING_MESSAGE = OnMessageController.class.getDeclaredMethod("onStringMessage", String.class);
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCalls calls;

    @Autowired
    private ProcessingCounter counter;

    @NettyOnMessage(serverName = "server1", priority = 1)
    public String onStringMessage(@NettyMessageBody final String msg) {
        calls.add(ON_MESSAGE3);
        counter.arrive();

        return msg;
    }

    @NettyOnMessage(serverName = "server1", priority = 4)
    private void onMessage3() {
        calls.add(ON_MESSAGE3);
        counter.arrive();
    }

    @NettyOnMessage(serverName = "server1", priority = 3)
    long onMessage2(@NettyMessageBody final long msg) {
        calls.add(ON_MESSAGE2);
        counter.arrive();

        return msg;
    }

    @NettyOnMessage(serverName = "server1", priority = 2)
    void onMessage1(final ChannelHandlerContext ctx, final Channel channel, @NettyMessageBody final Long msg) {

        calls.add(ON_MESSAGE1);
        counter.arrive();

        ctx.writeAndFlush(msg);
        channel.writeAndFlush(msg);
    }
}
