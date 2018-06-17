package org.kgusarov.integration.spring.netty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Instance of the named TCP server
 */
public final class TcpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final Map<String, Supplier<ChannelHandler>> handlers = Maps.newLinkedHashMap();
    private final List<Supplier<ChannelFutureListener>> closeFutureListeners = Lists.newArrayList();
    private final List<Supplier<ChannelHandler>> channelActiveHandlers = Lists.newArrayList();
    private final String name;

    private int bossThreads = Runtime.getRuntime().availableProcessors();
    private int workerThreads = Runtime.getRuntime().availableProcessors();
    private String host;
    private int port;
    private ChannelOptions options = new ChannelOptions();
    private ChannelOptions childOptions = new ChannelOptions();

    private EventLoopGroup bossThreadGroup;
    private EventLoopGroup workerThreadGroup;

    /**
     * Create TCP server instance of the given name
     *
     * @param name Name of the TCP server
     */
    public TcpServer(final String name) {
        this.name = name;
    }

    /**
     * Set the number of the Netty boss treads
     *
     * @param bossThreads Boss thread count
     */
    public void setBossThreads(final int bossThreads) {
        checkState().bossThreads = bossThreads;
    }

    /**
     * Set the number of the Netty worker treads
     *
     * @param workerThreads Worker thread count
     */
    public void setWorkerThreads(final int workerThreads) {
        checkState().workerThreads = workerThreads;
    }

    /**
     * Set the host the server will listen on
     *
     * @param host Server host
     */
    public void setHost(final String host) {
        checkState().host = host;
    }

    /**
     * Set the port the server will listen on
     *
     * @param port Server port
     */
    public void setPort(final int port) {
        checkState().port = port;
    }

    /**
     * Set the options for the acceptor channel
     *
     * @param options Channel options
     */
    public void setOptions(final ChannelOptions options) {
        checkState().options = options;
    }

    /**
     * Set the options for the newly created channels
     *
     * @param childOptions Channel options
     */
    public void setChildOptions(final ChannelOptions childOptions) {
        checkState().childOptions = childOptions;
    }

    /**
     * Stops the current server
     */
    void stop() {
        LOGGER.info("Stopping Netty server @{}:{}", host, port);

        workerThreadGroup.shutdownGracefully();
        bossThreadGroup.shutdownGracefully();

        workerThreadGroup.terminationFuture().syncUninterruptibly();
        bossThreadGroup.terminationFuture().syncUninterruptibly();
    }

    /**
     * Add new disconnection listener
     *
     * @param listener Listener to be added
     */
    public void onDisconnect(final Supplier<ChannelFutureListener> listener) {
        checkState().closeFutureListeners.add(listener);
    }

    /**
     * Add new connect listener
     *
     * @param handler Listener to be added
     */
    public void onConnect(final Supplier<ChannelHandler> handler) {
        checkState().channelActiveHandlers.add(handler);
    }

    /**
     * Add new named channel handler
     *
     * @param name           Handler name
     * @param channelHandler Handler to be added
     */
    public void addHandler(final String name, final Supplier<ChannelHandler> channelHandler) {
        checkState().handlers.put(name, channelHandler);
    }

    /**
     * Start the current server
     *
     * @return              Empty future that will resolve when server will actually start
     */
    ListenableFuture<Void> start() {
        if (!channelActiveHandlers.isEmpty()) {
            int i = 1;
            for (final Supplier<ChannelHandler> channelActiveHandler : channelActiveHandlers) {
                addHandler("channelActive" + i++, channelActiveHandler);
            }
        }

        LOGGER.info("Starting Netty server @{}:{} with {} boss threads and {} worker threads",
                host, port, bossThreads, workerThreads);

        final SettableFuture<Void> result = SettableFuture.create();
        final ServerBootstrap serverBootstrap = checkState().createServerBootstrap();
        final Channel ch = serverBootstrap
                .bind(host, port)
                .syncUninterruptibly()
                .channel();

        new Thread(() -> {
            result.set(null);
            ch.closeFuture().syncUninterruptibly();
        }, name).start();

        return result;
    }

    private ServerBootstrap createServerBootstrap() {
        bossThreadGroup = new NioEventLoopGroup(bossThreads);
        workerThreadGroup = new NioEventLoopGroup(workerThreads);

        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossThreadGroup, workerThreadGroup);

        setOptions(bootstrap);
        initialized.set(true);

        return initServerBoostrap(bootstrap);
    }

    private ServerBootstrap initServerBoostrap(final ServerBootstrap bootstrap) {
        return bootstrap.channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel ch) {
                        TcpServer.this.initChildChannel(ch);
                    }
                });
    }

    private void initChildChannel(final SocketChannel ch) {
        final ChannelPipeline pipeline = ch.pipeline();
        for (final Map.Entry<String, Supplier<ChannelHandler>> entry : handlers.entrySet()) {
            final ChannelHandler handler = entry.getValue().get();
            final String key = entry.getKey();

            pipeline.addLast(key, handler);
        }

        if (!closeFutureListeners.isEmpty()) {
            for (final Supplier<ChannelFutureListener> listener : closeFutureListeners) {
                ch.closeFuture().addListener(listener.get());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setOptions(final ServerBootstrap bootstrap) {
        final Map<ChannelOption, Object> channelOptions = options.get();
        final Map<ChannelOption, Object> childChannelOptions = childOptions.get();

        for (final Map.Entry<ChannelOption, Object> entry : channelOptions.entrySet()) {
            final ChannelOption key = entry.getKey();
            final Object value = entry.getValue();

            bootstrap.option(key, value);
        }

        for (final Map.Entry<ChannelOption, Object> entry : childChannelOptions.entrySet()) {
            final ChannelOption key = entry.getKey();
            final Object value = entry.getValue();

            bootstrap.childOption(key, value);
        }
    }

    private TcpServer checkState() {
        if (initialized.get()) {
            throw new IllegalStateException("Server already initialized");
        }

        return this;
    }
}
