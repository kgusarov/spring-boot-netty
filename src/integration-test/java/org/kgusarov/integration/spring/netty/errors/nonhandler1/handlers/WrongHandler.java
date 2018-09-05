package org.kgusarov.integration.spring.netty.errors.nonhandler1.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyFilter;

@NettyFilter(serverName = "server1", priority = 3)
public class WrongHandler {
}
