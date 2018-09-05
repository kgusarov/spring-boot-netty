package org.kgusarov.integration.spring.netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;

import java.util.List;

/**
 * Handler that notifies all {@code TcpEventHandler} instances about client connection. This is part of
 * internal API.
 */
@Deprecated
public final class OnConnectEventHandler extends ChannelInboundHandlerAdapter {
    private final List<TcpEventHandler<Void>> handlerList;

    /**
     * Create new handler instance
     *
     * @param handlerList           List of underlying handlers
     */
    public OnConnectEventHandler(final List<TcpEventHandler<Void>> handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().remove(this);

        final TcpEvent<Void> event = new TcpEvent<>(ctx);

        //noinspection CodeBlock2Expr
        handlerList.forEach(h -> {
            h.handle(event);
        });

        super.channelActive(ctx);
    }
}
