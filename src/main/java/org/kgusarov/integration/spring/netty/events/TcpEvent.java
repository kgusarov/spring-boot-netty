package org.kgusarov.integration.spring.netty.events;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Class that holds the TCP event (connected, disconnected, message received etc.)
 *
 * @param <T>       Type of an event-associated data
 */
public final class TcpEvent<T> {
    private final T data;
    private final ChannelHandlerContext ctx;
    private final Channel channel;

    /**
     * Create new instance of the TCP event using {@code io.netty.channel.ChannelHandlerContext} and
     * data related to it
     *
     * @param ctx           Channel handler context object
     * @param data          Data related to the TCP event
     */
    public TcpEvent(final ChannelHandlerContext ctx, final T data) {
        this.ctx = ctx;
        this.data = data;
        channel = ctx.channel();
    }

    /**
     * Create new instance of the TCP event using {@code io.netty.channel.ChannelHandlerContext}
     *
     * @param ctx           Channel handler context object
     */
    public TcpEvent(final ChannelHandlerContext ctx) {
        this(ctx, null);
    }

    /**
     * Create new instance of the TCP event using {@code io.netty.channel.Channel}
     *
     * @param channel       Netty channel
     */
    public TcpEvent(final Channel channel) {
        this.channel = channel;
        ctx = null;
        data = null;
    }

    /**
     * Get channel handler context object related to the given event
     *
     * @return              Channel handler context object
     */
    @Nullable
    public ChannelHandlerContext ctx() {
        return ctx;
    }

    /**
     * Get channel object related to the given event
     *
     * @return              Netty channel
     */
    @Nonnull
    public Channel channel() {
        return channel;
    }

    /**
     * Get data related to the given event
     *
     * @return              Data related to the TCP event
     */
    public Optional<T> data() {
        return Optional.ofNullable(data);
    }
}
