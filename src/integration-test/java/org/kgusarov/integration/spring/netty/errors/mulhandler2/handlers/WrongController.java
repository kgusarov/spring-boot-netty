package org.kgusarov.integration.spring.netty.errors.mulhandler2.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;

@NettyController
public class WrongController {
    @NettyOnConnect(serverName = "server1")
    @NettyOnDisconnect(serverName = "server1")
    public void onConnect() {
    }
}
