package org.kgusarov.integration.spring.netty.nettyfilters.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyMessageBody;
import org.kgusarov.integration.spring.netty.annotations.NettyOnMessage;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.springframework.beans.factory.annotation.Autowired;

@NettyController
public class LongResponder {
    @Autowired
    private HandlerCallStack handlerCallStack;

    @NettyOnMessage(serverName = "server1")
    public long onMessage(final ChannelHandlerContext ctx, @NettyMessageBody final long msg) {
        handlerCallStack.add(getClass());
        return msg;
    }
}
