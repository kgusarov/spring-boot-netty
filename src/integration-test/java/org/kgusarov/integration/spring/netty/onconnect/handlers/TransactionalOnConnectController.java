package org.kgusarov.integration.spring.netty.onconnect.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
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
    private HandlerMethodCalls calls;

    @Autowired
    private ProcessingCounter counter;

    @Transactional
    @NettyOnConnect(serverName = "server1", priority = 3)
    public void onConnect() {
        calls.add(ON_CONNECT);
        counter.arrive();
    }
}
