package org.kgusarov.integration.spring.netty.errors.mulhandler3.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;
import org.kgusarov.integration.spring.netty.annotations.NettyOnMessage;

@NettyController
public class WrongController {
    @NettyOnMessage(serverName = "server1")
    @NettyOnDisconnect(serverName = "server1")
    public void onConnect() {
    }
}
