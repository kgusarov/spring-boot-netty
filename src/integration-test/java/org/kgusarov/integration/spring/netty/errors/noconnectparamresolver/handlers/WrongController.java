package org.kgusarov.integration.spring.netty.errors.noconnectparamresolver.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;

@NettyController
public class WrongController {
    @NettyOnConnect(serverName = "server1")
    public void onConnect(final boolean provideMe) {
    }
}
