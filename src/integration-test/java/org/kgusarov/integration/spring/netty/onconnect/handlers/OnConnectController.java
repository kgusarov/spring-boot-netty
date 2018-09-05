package org.kgusarov.integration.spring.netty.onconnect.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCallStack;
import org.kgusarov.integration.spring.netty.etc.WaitForProcessingToComplete;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

@NettyController
public class OnConnectController {
    public static final Method ON_CONNECT1;
    public static final Method ON_CONNECT2;

    static {
        try {
            ON_CONNECT1 = OnConnectController.class.getDeclaredMethod("onConnect1");
            ON_CONNECT2 = OnConnectController.class.getDeclaredMethod("onConnect2");
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCallStack handlerCallStack;

    @Autowired
    private WaitForProcessingToComplete waitForProcessingToComplete;

    @NettyOnConnect(serverName = "server1", priority = 1)
    private void onConnect1() {
        handlerCallStack.add(ON_CONNECT1);
        waitForProcessingToComplete.countDown();
    }

    @SuppressWarnings("WeakerAccess")
    @NettyOnConnect(serverName = "server1", priority = 2)
    void onConnect2() {
        handlerCallStack.add(ON_CONNECT2);
        waitForProcessingToComplete.countDown();
    }
}
