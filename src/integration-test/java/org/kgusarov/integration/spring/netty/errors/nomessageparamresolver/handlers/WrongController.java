package org.kgusarov.integration.spring.netty.errors.nomessageparamresolver.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnMessage;

@NettyController
public class WrongController {
    @NettyOnMessage(serverName = "server1")
    public void onDisconnect(final boolean provideMe) {
    }
}
