package org.kgusarov.integration.spring.netty.errors.nonsharable2.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@NettyFilter(serverName = "server1")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Decoder extends ReplayingDecoder<Object> {
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        out.add(null);
    }
}
