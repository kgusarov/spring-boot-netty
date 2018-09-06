package org.kgusarov.integration.spring.netty.ondisconnect.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;
import org.kgusarov.integration.spring.netty.etc.CyclicWaitForProcessingToComplete;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCallStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@NettyController
public class TransactionalOnDisconnectController {
    public static final Method ON_DISCONNECT;

    static {
        try {
            ON_DISCONNECT = TransactionalOnDisconnectController.class.getDeclaredMethod("onDisconnect");
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCallStack handlerCallStack;

    @Autowired
    private CyclicWaitForProcessingToComplete counter;

    @Transactional
    @NettyOnDisconnect(serverName = "server1", priority = 3)
    public void onDisconnect() {
        handlerCallStack.add(ON_DISCONNECT);
        counter.arrive();
    }
}
