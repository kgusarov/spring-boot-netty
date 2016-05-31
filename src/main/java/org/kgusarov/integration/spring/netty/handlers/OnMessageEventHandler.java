package org.kgusarov.integration.spring.netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;

import java.util.List;

/**
 * Helper class used to call appropriate request handlers This is part of
 * internal API.
 *
 * @param <T>           Type of messages being processed by this processor
 */
@SuppressWarnings("unchecked")
public final class OnMessageEventHandler<T> extends ChannelInboundHandlerAdapter {
    private final List<TcpEventHandler<T>> handlerList;
    private final Class<T> messageType;

    /**
     * Create new handler instance
     *
     * @param handlerList           List of underlying handlers
     * @param messageType           Type of handled message
     */
    public OnMessageEventHandler(final List<TcpEventHandler<T>> handlerList, final Class<T> messageType) {
        this.handlerList = handlerList;
        this.messageType = messageType;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (messageType.isAssignableFrom(msg.getClass())) {
            final TcpEvent<T> event = new TcpEvent<>(ctx, (T) msg);
            handlerList.forEach(h -> h.handle(event));
        }

        super.channelRead(ctx, msg);
    }
}
