package org.kgusarov.integration.spring.netty.onmessage.handlers;

import io.netty.channel.ChannelHandler;
import org.kgusarov.integration.spring.netty.annotations.On;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;

@ChannelHandler.Sharable
@On(serverName = "server1", priority = 2, dataType = Long.class)
public class OnLong2Handler implements TcpEventHandler<Long> {
    @Override
    public void handle(final TcpEvent<Long> event) {
        // Do nothing...
    }
}
