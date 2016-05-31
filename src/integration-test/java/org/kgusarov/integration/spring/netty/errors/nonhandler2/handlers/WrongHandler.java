package org.kgusarov.integration.spring.netty.errors.nonhandler2.handlers;

import org.kgusarov.integration.spring.netty.annotations.OnConnect;

@OnConnect(serverName = "server1", priority = 3)
public class WrongHandler {
}
