package org.kgusarov.integration.spring.netty.nettyfilters.handlers;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@NettyFilter(serverName = "server1", priority = 9)
public class AroundResponderFilter extends ChannelDuplexHandler {
    @Autowired
    private HandlerCallStack handlerCallStack;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        handlerCallStack.add(getClass());
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        handlerCallStack.add(getClass());
        super.write(ctx, msg, promise);
    }
}
