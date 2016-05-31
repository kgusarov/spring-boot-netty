package org.kgusarov.integration.spring.netty.onmessage.handlers;

import io.netty.channel.ChannelHandler;
import org.kgusarov.integration.spring.netty.annotations.On;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;

@ChannelHandler.Sharable
@On(serverName = "server1", priority = 0, dataType = Long.class)
public class OnLongHandler implements TcpEventHandler<Long> {
    @Override
    public void handle(final TcpEvent<Long> event) {
        event.ctx().get().writeAndFlush(event.data().get());
    }
}
