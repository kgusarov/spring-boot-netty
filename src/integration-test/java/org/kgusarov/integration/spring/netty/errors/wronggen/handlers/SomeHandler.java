package org.kgusarov.integration.spring.netty.errors.wronggen.handlers;

import io.netty.channel.ChannelHandler;
import org.kgusarov.integration.spring.netty.annotations.On;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;

@ChannelHandler.Sharable
@On(serverName = "server1", priority = 1, dataType = Integer.class)
public class SomeHandler implements TcpEventHandler<Long> {
    @Override
    public void handle(final TcpEvent<Long> event) {
        // Do nothing
    }
}
