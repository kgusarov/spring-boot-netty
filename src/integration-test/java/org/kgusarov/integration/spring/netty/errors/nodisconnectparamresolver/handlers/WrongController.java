package org.kgusarov.integration.spring.netty.errors.nodisconnectparamresolver.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;

@NettyController
public class WrongController {
    @NettyOnDisconnect(serverName = "server1")
    public void onDisconnect(final boolean provideMe) {
    }
}
