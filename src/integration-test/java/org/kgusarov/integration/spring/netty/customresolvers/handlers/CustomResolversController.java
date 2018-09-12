package org.kgusarov.integration.spring.netty.customresolvers.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;
import org.kgusarov.integration.spring.netty.annotations.NettyOnMessage;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
import org.springframework.beans.factory.annotation.Autowired;

@NettyController
public class CustomResolversController {
    @Autowired
    private ProcessingCounter counter;

    @NettyOnConnect(serverName = "server1")
    public ByteBuf onConnect(final long rnd) {
        counter.arrive();
        return Unpooled.copyLong(rnd);
    }

    @NettyOnMessage(serverName = "server1")
    public ByteBuf onMessage(final Long rnd) {
        counter.arrive();
        return Unpooled.copyLong(rnd);
    }

    @SuppressWarnings("unused")
    @NettyOnDisconnect(serverName = "server1")
    public void onDisconnect(final long rnd) {
        counter.arrive();
    }
}
