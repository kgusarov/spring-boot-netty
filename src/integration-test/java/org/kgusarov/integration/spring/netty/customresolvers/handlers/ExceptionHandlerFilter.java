package org.kgusarov.integration.spring.netty.customresolvers.handlers;

import io.netty.channel.ChannelHandler;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.kgusarov.integration.spring.netty.etc.ExceptionHandler;

@ChannelHandler.Sharable
@NettyFilter(serverName = "server1", priority = Integer.MIN_VALUE)
public class ExceptionHandlerFilter extends ExceptionHandler {
    // No additional fields...
}
