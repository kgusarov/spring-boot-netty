package org.kgusarov.integration.spring.netty.configuration;

import io.netty.channel.ChannelHandler;
import org.kgusarov.integration.spring.netty.ChannelOptions;
import org.kgusarov.integration.spring.netty.TcpServer;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@SuppressWarnings("unchecked")
@Configuration
@Deprecated
@EnableConfigurationProperties(SpringNettyConfigurationProperties.class)
public class SpringNettyConfiguration {

    private final SpringNettyConfigurationProperties configurationProperties;
    private final ConfigurableListableBeanFactory beanFactory;

    @Autowired
    public SpringNettyConfiguration(final SpringNettyConfigurationProperties configurationProperties,
                                    final ConfigurableListableBeanFactory beanFactory) {

        this.configurationProperties = configurationProperties;
        this.beanFactory = beanFactory;
    }

    @Bean
    public NettyServers tcpServers() {
        final List<TcpServerProperties> servers = configurationProperties.getServers();
        final NettyServers result = new NettyServers();

        if (servers == null) {
            return result;
        }

        final Map<String, Object> filters = beanFactory.getBeansWithAnnotation(NettyFilter.class);

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

            addFilters(name, server, filters);
            result.add(server);
        }

        return result;
    }

    private <T extends Annotation> Set<String> collectServerNames(
            final Map<String, Object> handlerBeans, final Class<T> annotationClass,
            final Function<T, String> serverNameGetter) {

        return handlerBeans.entrySet().stream()
                .map(e -> beanFactory.findAnnotationOnBean(e.getKey(), annotationClass))
                .map(serverNameGetter)
                .collect(Collectors.toSet());
    }

    private void addFilters(final String serverName, final TcpServer server,
                            final Map<String, Object> filters) {

        final Optional<Object> nonHandlerBean = filters.values().stream()
                .filter(o -> !(o instanceof ChannelHandler))
                .findAny();

        if (nonHandlerBean.isPresent()) {
            throw new IllegalStateException("Bean annotated with @NettyFilter doesn't implement " +
                    "ChannelHandler: " + nonHandlerBean.get());
        }

        final AtomicInteger counter = new AtomicInteger(0);
        getHandlers(filters, NettyFilter.class, serverName, NettyFilter::priority, NettyFilter::serverName)
                .forEach(e -> addHandler(server, (ChannelHandler) e.getValue(),
                        "filter" + counter.incrementAndGet(), e.getKey()));
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
