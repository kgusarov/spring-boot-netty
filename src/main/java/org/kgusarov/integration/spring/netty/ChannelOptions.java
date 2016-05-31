package org.kgusarov.integration.spring.netty;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A typesafe configuration for {@code io.netty.channel.ChannelOption} options
 */
public final class ChannelOptions implements Supplier<Map<ChannelOption, Object>> {
    private final Map<ChannelOption, Object> channelOptions = Maps.newHashMap();

    public void setAllocator(final ByteBufAllocator allocator) {
        channelOptions.put(ChannelOption.ALLOCATOR, allocator);
    }

    public void setRecvBufAllocator(final RecvByteBufAllocator allocator) {
        channelOptions.put(ChannelOption.RCVBUF_ALLOCATOR, allocator);
    }

    public void setConnectTimeout(final int milliseconds) {
        channelOptions.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, milliseconds);
    }

    public void setWriteSpinCount(final int count) {
        channelOptions.put(ChannelOption.WRITE_SPIN_COUNT, count);
    }

    public void setWriteBufferWaterMark(final WriteBufferWaterMark mark) {
        channelOptions.put(ChannelOption.WRITE_BUFFER_WATER_MARK, mark);
    }

    public void setAllowHalfClosure(final boolean allow) {
        channelOptions.put(ChannelOption.ALLOW_HALF_CLOSURE, allow);
    }

    public void setAutoRead(final boolean autoRead) {
        channelOptions.put(ChannelOption.AUTO_READ, autoRead);
    }

    public void setSoBroadcast(final boolean broadcast) {
        channelOptions.put(ChannelOption.SO_BROADCAST, broadcast);
    }

    public void setSoKeepAlive(final boolean keepAlive) {
        channelOptions.put(ChannelOption.SO_KEEPALIVE, keepAlive);
    }

    public void setSoSndBuf(final int buf) {
        channelOptions.put(ChannelOption.SO_SNDBUF, buf);
    }

    public void setSoRcvBuf(final int buf) {
        channelOptions.put(ChannelOption.SO_RCVBUF, buf);
    }

    public void setSoReuseAddr(final boolean reuseAddr) {
        channelOptions.put(ChannelOption.SO_REUSEADDR, reuseAddr);
    }

    public void setSoLinger(final int linger) {
        channelOptions.put(ChannelOption.SO_LINGER, linger);
    }

    public void setSoBacklog(final int backlog) {
        channelOptions.put(ChannelOption.SO_BACKLOG, backlog);
    }

    public void setSoTimeout(final int timeout) {
        channelOptions.put(ChannelOption.SO_TIMEOUT, timeout);
    }

    public void setIpTos(final int tos) {
        channelOptions.put(ChannelOption.IP_TOS, tos);
    }

    public void setIpMulticastAddr(final InetAddress addr) {
        channelOptions.put(ChannelOption.IP_MULTICAST_ADDR, addr);
    }

    public void setIpMulticastIf(final NetworkInterface iface) {
        channelOptions.put(ChannelOption.IP_MULTICAST_IF, iface);
    }

    public void setIpMulticastTtl(final int ttl) {
        channelOptions.put(ChannelOption.IP_MULTICAST_TTL, ttl);
    }

    public void setIpMulticastLoopDisabled(final boolean loopDisabled) {
        channelOptions.put(ChannelOption.IP_MULTICAST_LOOP_DISABLED, loopDisabled);
    }

    public void setTcpNodelay(final boolean noDelay) {
        channelOptions.put(ChannelOption.TCP_NODELAY, noDelay);
    }

    @Override
    public Map<ChannelOption, Object> get() {
        return channelOptions;
    }
}
