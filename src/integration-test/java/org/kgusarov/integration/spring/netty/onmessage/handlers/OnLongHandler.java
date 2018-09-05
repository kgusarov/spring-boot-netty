package org.kgusarov.integration.spring.netty.onmessage.handlers;

import io.netty.channel.ChannelHandler;
import org.kgusarov.integration.spring.netty.annotations.On;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;

@ChannelHandler.Sharable
@On(serverName = "server1", priority = 1, dataType = Long.class)
public class OnLongHandler implements TcpEventHandler<Long> {
    @Override
    public void handle(final TcpEvent<Long> event) {
        //noinspection CodeBlock2Expr
        event.data().ifPresent(data -> {
            event.channel().writeAndFlush(data);
        });
    }
}
