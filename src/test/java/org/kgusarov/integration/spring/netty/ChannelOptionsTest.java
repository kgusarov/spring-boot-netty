package org.kgusarov.integration.spring.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.WriteBufferWaterMark;
import org.junit.Test;

import java.util.Map;

import static java.net.InetAddress.getLoopbackAddress;
import static java.net.NetworkInterface.getNetworkInterfaces;

public class ChannelOptionsTest {
    @Test
    public void testOptionsHaveCorrectTypes() throws Exception {
        final ServerBootstrap bootstrap = new ServerBootstrap();
        final ChannelOptions options = new ChannelOptions();

        options.setAllocator(new PooledByteBufAllocator());
        options.setRecvBufAllocator(new AdaptiveRecvByteBufAllocator());
        options.setConnectTimeout(1);
        options.setWriteSpinCount(1);
        options.setWriteBufferWaterMark(new WriteBufferWaterMark(8192, 32768));
        options.setAllowHalfClosure(true);
        options.setAutoRead(true);
        options.setSoBroadcast(true);
        options.setSoKeepAlive(true);
        options.setSoReuseAddr(true);
        options.setSoSndBuf(8192);
        options.setSoRcvBuf(8192);
        options.setSoLinger(0);
        options.setSoBacklog(0);
        options.setSoTimeout(0);
        options.setIpTos(0);
        options.setIpMulticastAddr(getLoopbackAddress());
        options.setIpMulticastIf(getNetworkInterfaces().nextElement());
        options.setIpMulticastTtl(300);
        options.setIpMulticastLoopDisabled(true);
        options.setTcpNodelay(true);

        final Map<ChannelOption, Object> channelOptionMap = options.get();
        for (final Map.Entry<ChannelOption, Object> entry : channelOptionMap.entrySet()) {
            bootstrap.option(entry.getKey(), entry.getValue());
            bootstrap.childOption(entry.getKey(), entry.getValue());
        }
    }
}