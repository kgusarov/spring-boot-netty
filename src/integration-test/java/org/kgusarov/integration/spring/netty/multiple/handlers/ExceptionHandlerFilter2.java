package org.kgusarov.integration.spring.netty.multiple.handlers;

import io.netty.channel.ChannelHandler;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.kgusarov.integration.spring.netty.etc.ExceptionHandler;

@ChannelHandler.Sharable
@NettyFilter(serverName = "server2", priority = Integer.MIN_VALUE)
public class ExceptionHandlerFilter2 extends ExceptionHandler {
    // No additional fields...
}
