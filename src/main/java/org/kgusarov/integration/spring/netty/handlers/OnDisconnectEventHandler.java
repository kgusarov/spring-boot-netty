package org.kgusarov.integration.spring.netty.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;

import java.util.List;

/**
 * Handler that notifies all {@code TcpEventHandler} instances about client disconnection. This is part of
 * internal API.
 */
public final class OnDisconnectEventHandler implements ChannelFutureListener {
    private final List<TcpEventHandler<Void>> handlerList;

    /**
     * Create new handler instance
     *
     * @param handlerList           List of underlying handlers
     */
    public OnDisconnectEventHandler(final List<TcpEventHandler<Void>> handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public void operationComplete(final ChannelFuture future) {
        final Channel channel = future.channel();
        final TcpEvent<Void> event = new TcpEvent<>(channel);

        handlerList.forEach(h -> h.handle(event));
    }
}
