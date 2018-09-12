package org.kgusarov.integration.spring.netty.support;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kgusarov.integration.spring.netty.support.invoke.OnConnectMethodInvoker;
import org.kgusarov.integration.spring.netty.support.invoke.OnMessageMethodInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final ListMultimap<Class<?>, OnMessageMethodInvoker> typedCallbackMap;
    private final Set<Class<?>> typedCallbackClasses;

    public SpringChannelHandler(final List<OnConnectMethodInvoker> onConnectCallbacks,
                                final List<OnMessageMethodInvoker> onMessageCallbacks) {

        this.onConnectCallbacks = onConnectCallbacks;
        this.onMessageCallbacks = onMessageCallbacks;

        final Multimap<Class<?>, OnMessageMethodInvoker> onMessageInvokers = ArrayListMultimap.create();
        final Set<? extends Class<?>> messageBodyTypes = onMessageCallbacks.stream()
                .map(OnMessageMethodInvoker::getMessageBodyType)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        messageBodyTypes.forEach(mbt -> {
            onMessageCallbacks.forEach(invoker -> {
                final Class<?> messageBodyType = invoker.getMessageBodyType();
                if ((messageBodyType == null) || mbt.isAssignableFrom(messageBodyType)) {
                    onMessageInvokers.put(mbt, invoker);
                }
            });
        });

        typedCallbackMap = ImmutableListMultimap.copyOf(onMessageInvokers);
        typedCallbackClasses = typedCallbackMap.keySet();
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
        final Class<?> messageClass = msg.getClass();
        final List<OnMessageMethodInvoker> callbacks = typedCallbackClasses.stream()
                .filter(clazz -> clazz.isAssignableFrom(messageClass))
                .findFirst()
                .map(typedCallbackMap::get)
                .orElse(onMessageCallbacks);

        final boolean processed = callbacks.stream()
                .map(cb -> cb.channelRead(ctx, msg))
                .reduce(false, (a, b) -> a || b);

        if (!processed) {
            LOGGER.warn("Message " + msg + " was not processed by any handler");
        }

        super.channelRead(ctx, msg);
    }
}
