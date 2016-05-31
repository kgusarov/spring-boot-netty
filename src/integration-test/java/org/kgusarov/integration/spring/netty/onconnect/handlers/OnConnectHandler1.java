package org.kgusarov.integration.spring.netty.onconnect.handlers;

import io.netty.channel.ChannelHandler;
import org.kgusarov.integration.spring.netty.annotations.OnConnect;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.kgusarov.integration.spring.netty.etc.TcpEventStack;
import org.kgusarov.integration.spring.netty.etc.WaitForProcessingToComplete;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;
import org.springframework.beans.factory.annotation.Autowired;

@ChannelHandler.Sharable
@OnConnect(serverName = "server1", priority = 0)
public class OnConnectHandler1 implements TcpEventHandler<Void> {
    @Autowired
    private HandlerCallStack handlerCallStack;

    @Autowired
    private TcpEventStack tcpEventStack;

    @Autowired
    private WaitForProcessingToComplete waitForProcessingToComplete;

    @Override
    public void handle(final TcpEvent<Void> event) {
        handlerCallStack.add(getClass());
        tcpEventStack.add(event);
        waitForProcessingToComplete.countDown();
    }
}
