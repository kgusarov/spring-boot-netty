package org.kgusarov.integration.spring.netty.nettyfilters.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@NettyFilter(serverName = "server1", priority = 1)
public class LongDecoder extends ReplayingDecoder<Long> {
    @Autowired
    private HandlerCallStack handlerCallStack;

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        handlerCallStack.add(getClass());
        out.add(in.readLong());
    }
}
