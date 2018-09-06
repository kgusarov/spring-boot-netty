package org.kgusarov.integration.spring.netty;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.Future;

public class ServerClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerClient.class);

    private final String host;
    private final ChannelHandler[] handlers;
    private final int port;
    private Channel channel;

    public ServerClient(final int port, final String host, final ChannelHandler... handlers) {
        this.port = port;
        this.host = host;
        this.handlers = handlers;
    }

    public ChannelFuture writeAndFlush(final Object msg) {
        return channel.writeAndFlush(msg);
    }

    public void disconnect() {
        channel.close().syncUninterruptibly();
    }

    public Future<ServerClient> connect() {
        final EventLoopGroup group = new NioEventLoopGroup();
        final SettableFuture<ServerClient> result = SettableFuture.create();

        try {
            final Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(final SocketChannel ch) {
                            final ChannelPipeline p = ch.pipeline();
                            Arrays.stream(handlers).forEach(p::addLast);
                        }
                    });

            // Make the connection attempt.
            final ChannelFuture f = b.connect(host, port).syncUninterruptibly();
            channel = f.channel();

            new Thread(() -> {
                result.set(this);

                // Wait until the connection is closed.
                channel.closeFuture().syncUninterruptibly();
                group.shutdownGracefully();
            }).start();
        } catch (final Exception e) {
            LOGGER.error("Exception while connecting", e);
            result.setException(e);
        }

        return result;
    }
}
