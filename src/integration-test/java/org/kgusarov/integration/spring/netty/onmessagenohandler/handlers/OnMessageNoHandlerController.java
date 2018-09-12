package org.kgusarov.integration.spring.netty.onmessagenohandler.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyMessageBody;
import org.kgusarov.integration.spring.netty.annotations.NettyOnMessage;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

@NettyController
class OnMessageNoHandlerController {
    private static final Method ON_MESSAGE;

    static {
        try {
            ON_MESSAGE = OnMessageNoHandlerController.class.getDeclaredMethod("onMessage", long.class);
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Autowired
    private HandlerMethodCalls calls;

    @Autowired
    private ProcessingCounter counter;

    @SuppressWarnings("unused")
    @NettyOnMessage(serverName = "server1")
    private void onMessage(@NettyMessageBody final long body) {
        calls.add(ON_MESSAGE);
    }

    @NettyOnMessage(serverName = "server1")
    private void onAnyMessage() {
        counter.arrive();
    }
}
