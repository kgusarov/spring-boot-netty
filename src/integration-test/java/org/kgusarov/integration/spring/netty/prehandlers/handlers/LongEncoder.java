package org.kgusarov.integration.spring.netty.prehandlers.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.kgusarov.integration.spring.netty.annotations.PreHandler;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@PreHandler(serverName = "server1", priority = 1)
public class LongEncoder extends MessageToByteEncoder<Long> {
    @Autowired
    private HandlerCallStack handlerCallStack;

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Long msg,
                          final ByteBuf out) throws Exception {

        handlerCallStack.add(getClass());
        out.writeLong(msg);
    }
}
