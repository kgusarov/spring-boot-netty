package org.kgusarov.integration.spring.netty.onconnect.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCallStack;
import org.kgusarov.integration.spring.netty.etc.WaitForProcessingToComplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@NettyController
public class TransactionalOnConnectController {
    public static final Method ON_CONNECT;

    static {
        try {
            ON_CONNECT = TransactionalOnConnectController.class.getDeclaredMethod("onConnect");
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCallStack handlerCallStack;

    @Autowired
    private WaitForProcessingToComplete waitForProcessingToComplete;

    @Transactional
    @NettyOnConnect(serverName = "server1", priority = 3)
    public void onConnect() {
        handlerCallStack.add(ON_CONNECT);
        waitForProcessingToComplete.countDown();
    }
}
