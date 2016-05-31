package org.kgusarov.integration.spring.netty.onmessage.handlers;

import org.kgusarov.integration.spring.netty.annotations.On;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@On(serverName = "server1", priority = 0, dataType = String.class)
public class OnStringHandler implements TcpEventHandler<String> {
    @Override
    public void handle(final TcpEvent<String> event) {
        event.channel().get().writeAndFlush(event.data().get());
    }
}
