package org.kgusarov.integration.spring.netty.ondisconnect.handlers;

import org.kgusarov.integration.spring.netty.annotations.OnDisconnect;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.kgusarov.integration.spring.netty.etc.HandlerStack;
import org.kgusarov.integration.spring.netty.etc.TcpEventStack;
import org.kgusarov.integration.spring.netty.etc.WaitForProcessingToComplete;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@OnDisconnect(serverName = "server1", priority = 0)
public class OnDisconnectHandler1 implements TcpEventHandler<Void> {
    @Autowired
    private HandlerCallStack handlerCallStack;

    @Autowired
    private TcpEventStack tcpEventStack;

    @Autowired
    private WaitForProcessingToComplete waitForProcessingToComplete;

    @Autowired
    private HandlerStack handlers;

    @Override
    public void handle(final TcpEvent<Void> event) {
        handlerCallStack.add(getClass());
        tcpEventStack.add(event);
        handlers.add(this);
        waitForProcessingToComplete.countDown();
    }
}
