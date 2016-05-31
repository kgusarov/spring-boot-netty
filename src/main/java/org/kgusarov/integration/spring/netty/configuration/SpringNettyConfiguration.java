package org.kgusarov.integration.spring.netty.configuration;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import org.kgusarov.integration.spring.netty.ChannelOptions;
import org.kgusarov.integration.spring.netty.TcpServer;
import org.kgusarov.integration.spring.netty.annotations.On;
import org.kgusarov.integration.spring.netty.annotations.OnConnect;
import org.kgusarov.integration.spring.netty.annotations.OnDisconnect;
import org.kgusarov.integration.spring.netty.annotations.PreHandler;
import org.kgusarov.integration.spring.netty.events.TcpEventHandler;
import org.kgusarov.integration.spring.netty.handlers.OnConnectEventHandler;
import org.kgusarov.integration.spring.netty.handlers.OnDisconnectEventHandler;
import org.kgusarov.integration.spring.netty.handlers.OnMessageEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.immutableEntry;
import static com.google.common.collect.Sets.symmetricDifference;
import static com.google.common.collect.Sets.union;
import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@SuppressWarnings("unchecked")
@Configuration
@EnableConfigurationProperties(SpringNettyConfigurationProperties.class)
public class SpringNettyConfiguration {
    @Autowired
    private SpringNettyConfigurationProperties configurationProperties;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Bean
    public NettyServers tcpServers() {
        final List<TcpServerProperties> servers = configurationProperties.getServers();
        final NettyServers result = new NettyServers();

        if (servers == null) {
            return result;
        }

        final Map<String, Object> preHandlers = beanFactory.getBeansWithAnnotation(PreHandler.class);
        final Map<String, Object> connectHandlers = beanFactory.getBeansWithAnnotation(OnConnect.class);
        final Map<String, Object> disconnectHandlers = beanFactory.getBeansWithAnnotation(OnDisconnect.class);
        final Map<String, Object> messageHandlers = beanFactory.getBeansWithAnnotation(On.class);

        validateConfiguration(servers, preHandlers, connectHandlers, disconnectHandlers, messageHandlers);

        for (final TcpServerProperties serverProperties : servers) {
            final String name = serverProperties.getName();
            final Integer bossThreads = serverProperties.getBossThreads();
            final Integer workerThreads = serverProperties.getWorkerThreads();
            final ChannelOptions childOptions = serverProperties.getChildOptions();
            final ChannelOptions options = serverProperties.getOptions();
            final String host = serverProperties.getHost();
            final Integer port = serverProperties.getPort();

            final TcpServer server = new TcpServer(name);
            server.setHost(host);
            server.setPort(port);

            if (bossThreads != null) {
                server.setBossThreads(bossThreads);
            }

            if (workerThreads != null) {
                server.setWorkerThreads(workerThreads);
            }

            if (options != null) {
                server.setOptions(options);
            }

            if (childOptions != null) {
                server.setChildOptions(childOptions);
            }

            addPreHandlers(name, server, preHandlers);
            addConnectHandlers(name, server, connectHandlers);
            addDisconnectHandlers(name, server, disconnectHandlers);
            addAllMessageHandlers(name, server, messageHandlers);
            result.add(server);
        }

        return result;
    }

    private void validateConfiguration(final List<TcpServerProperties> servers,
                                       final Map<String, Object> preHandlers,
                                       final Map<String, Object> connectHandlers,
                                       final Map<String, Object> disconnectHandlers,
                                       final Map<String, Object> messageHandlers) {

        final Set<String> serversInConfig = servers.stream()
                .map(TcpServerProperties::getName)
                .distinct()
                .collect(Collectors.toSet());

        if (serversInConfig.size() != servers.size()) {
            throw new IllegalStateException("Configuration has duplicate server definitions");
        }

        final Set<String> serversInHandlers = union(
                union(collectServerNames(preHandlers, PreHandler.class, PreHandler::serverName),
                        collectServerNames(connectHandlers, OnConnect.class, OnConnect::serverName)),
                union(collectServerNames(disconnectHandlers, OnDisconnect.class, OnDisconnect::serverName),
                        collectServerNames(messageHandlers, On.class, On::serverName))
        );

        final Set<String> diff = symmetricDifference(serversInConfig, serversInHandlers);
        if (!diff.isEmpty()) {
            throw new IllegalStateException("Handlers are not present both in config and handler beans: " + diff);
        }
    }

    private <T extends Annotation> Set<String> collectServerNames(
            final Map<String, Object> handlerBeans, final Class<T> annotationClass,
            final Function<T, String> serverNameGetter) {

        return handlerBeans.entrySet().stream()
                .map(e -> beanFactory.findAnnotationOnBean(e.getKey(), annotationClass))
                .map(serverNameGetter)
                .collect(Collectors.toSet());
    }

    private void addDisconnectHandlers(final String serverName, final TcpServer server,
                                       final Map<String, Object> disconnectHandlers) {

        checkTcpEventHandlersArePresent(disconnectHandlers, OnDisconnect.class, (ignored) -> Void.class);

        final List<Supplier<TcpEventHandler>> underlying = createUnderlyingHandlerList(
                serverName, disconnectHandlers, OnDisconnect.class, OnDisconnect::priority, OnDisconnect::serverName);

        final Supplier<ChannelFutureListener> channelFutureListenerSupplier = () -> {
            List<TcpEventHandler<Void>> handlers = underlying.stream()
                    .map(Supplier::get)
                    .map(h -> (TcpEventHandler<Void>) h)
                    .collect(Collectors.toList());

            return new OnDisconnectEventHandler(handlers);
        };

        server.onDisconnect(channelFutureListenerSupplier);
    }

    private void addConnectHandlers(final String serverName, final TcpServer server,
                                    final Map<String, Object> connectHandlers) {

        checkTcpEventHandlersArePresent(connectHandlers, OnConnect.class, (ignored) -> Void.class);

        final List<Supplier<TcpEventHandler>> underlying = createUnderlyingHandlerList(
                serverName, connectHandlers, OnConnect.class, OnConnect::priority, OnConnect::serverName);

        final Supplier<ChannelHandler> channelHandlerSupplier = () -> {
            List<TcpEventHandler<Void>> handlers = underlying.stream()
                    .map(Supplier::get)
                    .map(h -> (TcpEventHandler<Void>) h)
                    .collect(Collectors.toList());

            return new OnConnectEventHandler(handlers);
        };

        server.onConnect(channelHandlerSupplier);
    }

    private void addAllMessageHandlers(final String serverName, final TcpServer server,
                                       final Map<String, Object> messageHandlers) {

        final Queue<Map.Entry<String, Object>> open = Queues.newArrayDeque(messageHandlers.entrySet());
        final Set<Map.Entry<String, Object>> closed = Sets.newHashSet();

        Class<?> messageType = null;
        final Map<String, Object> concreteHandlers = Maps.newHashMap();

        while (!open.isEmpty()) {
            final Map.Entry<String, Object> next = open.poll();
            if (closed.contains(next)) {
                addMessageHandlers(serverName, server, concreteHandlers, messageType);
                concreteHandlers.clear();
                closed.clear();

                messageType = null;
            }

            final Object handler = next.getValue();
            final On annotation = findAnnotation(handler.getClass(), On.class);

            assert annotation != null;
            final Class<?> dataType = annotation.dataType();

            if (messageType == null) {
                messageType = dataType;
                concreteHandlers.put(next.getKey(), next.getValue());
            } else if (messageType.equals(dataType)) {
                concreteHandlers.put(next.getKey(), next.getValue());
            } else {
                open.offer(next);
                closed.add(next);
            }
        }

        if (!concreteHandlers.isEmpty()) {
            addMessageHandlers(serverName, server, concreteHandlers, messageType);
        }
    }

    private <T> void addMessageHandlers(final String serverName, final TcpServer server,
                                        final Map<String, Object> messageHandlers, final Class<T> messageType) {

        checkTcpEventHandlersArePresent(messageHandlers, On.class, On::dataType);

        final List<Supplier<TcpEventHandler>> underlying = createUnderlyingHandlerList(
                serverName, messageHandlers, On.class, On::priority, On::serverName);

        final List<TcpEventHandler<T>> handlers = underlying.stream()
                .map(Supplier::get)
                .map(h -> (TcpEventHandler<T>) h)
                .collect(Collectors.toList());

        final Supplier<ChannelHandler> channelHandlerSupplier = () -> new OnMessageEventHandler<>(handlers, messageType);

        server.onConnect(channelHandlerSupplier);
    }

    private <T extends Annotation> List<Supplier<TcpEventHandler>> createUnderlyingHandlerList(final String serverName,
                                                                                               final Map<String, Object> handlers,
                                                                                               final Class<T> annotationClass,
                                                                                               final Function<T, Integer> priorityGetter,
                                                                                               final Function<T, String> serverNameGetter) {

        return getHandlers(handlers, annotationClass, serverName, priorityGetter, serverNameGetter)
                .map(e -> immutableEntry(e.getKey(), (TcpEventHandler) e.getValue()))
                .map(e -> createHandlerBeanSupplier(e.getValue(), e.getValue().getClass(), e.getKey()))
                .collect(Collectors.toList());
    }

    private void addPreHandlers(final String serverName, final TcpServer server,
                                final Map<String, Object> preHandlers) {

        final Optional<Object> nonHandlerBean = preHandlers.values().stream()
                .filter(o -> !(o instanceof ChannelHandler))
                .findAny();

        if (nonHandlerBean.isPresent()) {
            throw new IllegalStateException("Bean annotated with @PreHandler doesn't implement " +
                    "ChannelHandler: " + nonHandlerBean.get());
        }

        final AtomicInteger counter = new AtomicInteger(0);
        getHandlers(preHandlers, PreHandler.class, serverName, PreHandler::priority, PreHandler::serverName)
                .forEach(e -> addHandler(server, (ChannelHandler) e.getValue(),
                        "preHandler" + counter.incrementAndGet(), e.getKey()));
    }

    private <T extends Annotation> void checkTcpEventHandlersArePresent(final Map<String, Object> handlers,
                                                                        final Class<T> annotationClass,
                                                                        final Function<T, Class<?>> processedTypeGetter) {

        final Optional<Object> nonHandlerBean = handlers.values().stream()
                .filter(o -> !(o instanceof TcpEventHandler))
                .findAny();

        if (nonHandlerBean.isPresent()) {
            throw new IllegalStateException("Bean annotated with " + annotationClass.getSimpleName() + " doesn't " +
                    "implement TcpEventHandler: " + nonHandlerBean.get());
        }

        handlers.values().stream()
                .filter(o -> o instanceof TcpEventHandler)
                .map(o -> (TcpEventHandler) o)
                .forEach(h -> {
                    final Class<? extends TcpEventHandler> handlerClass = h.getClass();
                    final Class<?>[] args = resolveTypeArguments(handlerClass, TcpEventHandler.class);

                    final T t = findAnnotation(handlerClass, annotationClass);
                    final Class<?> expectedClass = processedTypeGetter.apply(t);

                    if ((args == null) || (args.length == 0) || !expectedClass.isAssignableFrom(args[0])) {
                        throw new IllegalStateException(h.getClass() + " should implement TcpEventHandler<"
                                + expectedClass.getSimpleName() + '>');
                    }
                });
    }

    private <T extends Annotation> Stream<Map.Entry<String, Object>> getHandlers(
            final Map<String, Object> handlers, final Class<T> annotationClass, final String serverName,
            final Function<T, Integer> priorityGetter, final Function<T, String> serverNameGetter) {

        return handlers.entrySet().stream()
                .filter(e -> {
                    final T annotation = beanFactory.findAnnotationOnBean(e.getKey(), annotationClass);
                    return serverName.equals(serverNameGetter.apply(annotation));
                })
                .sorted((a, b) -> {
                    final T aA = beanFactory.findAnnotationOnBean(a.getKey(), annotationClass);
                    final T bA = beanFactory.findAnnotationOnBean(b.getKey(), annotationClass);

                    return Integer.compare(priorityGetter.apply(aA), priorityGetter.apply(bA));
                });
    }

    private void addHandler(final TcpServer server, final ChannelHandler h, final String handlerName,
                            final String beanName) {

        final Supplier<ChannelHandler> supplier = createHandlerBeanSupplier(h, h.getClass(), beanName);
        server.addHandler(handlerName, supplier);
    }

    private <T> Supplier<T> createHandlerBeanSupplier(final T h, final Class<? extends T> handlerClass,
                                                      final String beanName) {

        final ChannelHandler.Sharable sharable = findAnnotation(handlerClass, ChannelHandler.Sharable.class);
        if (sharable == null) {
            final Scope scope = findAnnotation(handlerClass, Scope.class);
            if ((scope == null) || !ConfigurableBeanFactory.SCOPE_PROTOTYPE.equals(scope.value())) {
                throw new IllegalStateException("Non-sharable handler should be presented by a " +
                        "prototype bean");
            }
        }

        return (sharable == null) ?
                () -> beanFactory.getBean(beanName, handlerClass) :
                () -> h;
    }
}
