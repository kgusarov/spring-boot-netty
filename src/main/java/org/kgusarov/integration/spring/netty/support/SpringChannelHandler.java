package org.kgusarov.integration.spring.netty.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kgusarov.integration.spring.netty.support.invoke.OnConnectMethodInvoker;
import org.kgusarov.integration.spring.netty.support.invoke.OnMessageMethodInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handler that is part of internal API and is used to invoke appropriate
 * {@link org.kgusarov.integration.spring.netty.annotations.NettyController}
 * annotated methods
 */
@SuppressWarnings("CodeBlock2Expr")
public class SpringChannelHandler extends ChannelInboundHandlerAdapter{
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringChannelHandler.class);

    private final List<OnConnectMethodInvoker> onConnectCallbacks;
    private final List<OnMessageMethodInvoker> onMessageCallbacks;

    public SpringChannelHandler(final List<OnConnectMethodInvoker> onConnectCallbacks,
                                final List<OnMessageMethodInvoker> onMessageCallbacks) {

        this.onConnectCallbacks = onConnectCallbacks;
        this.onMessageCallbacks = onMessageCallbacks;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        onConnectCallbacks.forEach(cb -> {
            cb.channelActive(ctx);
        });

        super.channelActive(ctx);
    }

    @Override
    @SuppressWarnings("NestedMethodCall")
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final boolean processed = onMessageCallbacks.stream()
                .map(cb -> cb.channelRead(ctx, msg))
                .reduce(false, (a, b) -> a || b);

        if (!processed) {
            LOGGER.warn("Message " + msg + " was not processed by any handler");
        }

        super.channelRead(ctx, msg);
    }
}
