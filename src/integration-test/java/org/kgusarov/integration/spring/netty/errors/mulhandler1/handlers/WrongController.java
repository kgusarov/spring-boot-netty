package org.kgusarov.integration.spring.netty.errors.mulhandler1.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.annotations.NettyOnMessage;

@NettyController
public class WrongController {
    @NettyOnMessage(serverName = "server1")
    @NettyOnConnect(serverName = "server1")
    public void onConnect() {
    }
}
