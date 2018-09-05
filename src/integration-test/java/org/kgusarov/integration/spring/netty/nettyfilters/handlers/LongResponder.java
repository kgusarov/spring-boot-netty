package org.kgusarov.integration.spring.netty.nettyfilters.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.kgusarov.integration.spring.netty.annotations.On;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;
import org.springframework.beans.factory.annotation.Autowired;

@ChannelHandler.Sharable
@On(serverName = "server1", dataType = Long.class, priority = 10)
public class LongResponder implements TcpEventHandler<Long> {
    @Autowired
    private HandlerCallStack handlerCallStack;

    @Override
    public void handle(final TcpEvent<Long> event) {
        handlerCallStack.add(getClass());

        //noinspection CodeBlock2Expr
        event.data().ifPresent(l -> {
            event.channel().writeAndFlush(l);
        });
    }
}
