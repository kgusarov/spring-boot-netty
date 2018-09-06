package org.kgusarov.integration.spring.netty.configuration;

import com.google.common.annotations.VisibleForTesting;
import org.kgusarov.integration.spring.netty.ChannelOptions;
import org.kgusarov.integration.spring.netty.TcpServer;
import org.kgusarov.integration.spring.netty.TcpServerLifeCycle;
import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.support.SkeletonChannelHandler;
import org.kgusarov.integration.spring.netty.support.invoke.OnConnectMethodInvoker;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyCallbackParameterResolver;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnConnectParameterResolver;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Author: Konstantin Gusarov
 */
@Configuration
@ComponentScan(basePackageClasses = SkeletonChannelHandler.class)
@EnableConfigurationProperties(SpringNettyConfigurationProperties.class)
public class NettyControllerConfiguration {
    @VisibleForTesting
    static final Comparator<Method> ON_CONNECT_METHOD_COMPARATOR = Comparator.comparingInt(m -> {
        final NettyOnConnect ann = AnnotationUtils.findAnnotation(m, NettyOnConnect.class);
        if (ann == null) {
            throw new IllegalStateException();
        }

        return ann.priority();
    });

    private final SpringNettyConfigurationProperties configurationProperties;
    private final ConfigurableListableBeanFactory beanFactory;

    @Autowired
    public NettyControllerConfiguration(final SpringNettyConfigurationProperties configurationProperties,
                                        final ConfigurableListableBeanFactory beanFactory) {

        this.configurationProperties = configurationProperties;
        this.beanFactory = beanFactory;
    }

    @Bean
    public TcpServerLifeCycle tcpServerLifeCycle() {
        final NettyServers nettyServers = tcpServers();
        return new TcpServerLifeCycle(nettyServers);
    }

    @Bean
    public NettyServers tcpServers() {
        final List<TcpServerProperties> servers = configurationProperties.getServers();
        final NettyServers result = new NettyServers();

        if (servers == null) {
            return result;
        }

        checkDuplicateDefinitions(servers);

        final Map<String, SortedSet<Method>> connectHandlers = new HashMap<>();

        final Map<String, Object> controllers = beanFactory.getBeansWithAnnotation(NettyController.class);
        controllers.forEach((ignored, bean) -> {
            final Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);
            final Method[] methods = ReflectionUtils.getAllDeclaredMethods(beanClass);

            for (final Method method : methods) {
                checkForConnectHandler(connectHandlers, method);
            }
        });

        final Collection<NettyOnConnectParameterResolver> connectParameterResolvers =
                beanFactory.getBeansOfType(NettyOnConnectParameterResolver.class).values();

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

            final List<OnConnectMethodInvoker> onConnectMethodInvokers =
                    buildConnectMethodInvokers(name, connectHandlers, connectParameterResolvers);

            server.addHandler("springNettyHandler", () -> {
                //noinspection CodeBlock2Expr
                return new SkeletonChannelHandler(onConnectMethodInvokers);
            });

            result.add(server);
        }

        return result;
    }

    private List<OnConnectMethodInvoker> buildConnectMethodInvokers(final String serverName, final Map<String, SortedSet<Method>> connectHandlers,
                                                                    final Collection<NettyOnConnectParameterResolver> connectParameterResolvers) {

        final SortedSet<Method> onConnect = connectHandlers.getOrDefault(serverName, new TreeSet<>());
        final List<OnConnectMethodInvoker> onConnectMethodInvokers = new ArrayList<>();
        for (final Method method : onConnect) {
            final List<NettyOnConnectParameterResolver> resolvers = buildMethodParameterResolvers(method, connectParameterResolvers);
            final Class<?> returnType = method.getReturnType();
            final boolean sendInvocationResultBack = !void.class.equals(returnType);

            ReflectionUtils.makeAccessible(method);

            final Class<?> declaringClass = method.getDeclaringClass();
            final Object bean = beanFactory.getBean(declaringClass);
            final OnConnectMethodInvoker invoker = new OnConnectMethodInvoker(bean, method, resolvers, sendInvocationResultBack);
            onConnectMethodInvokers.add(invoker);
        }

        return onConnectMethodInvokers;
    }

    private static <T extends NettyCallbackParameterResolver> List<T> buildMethodParameterResolvers(final Method method, final Collection<T> candidates) {
        final int parameterCount = method.getParameterCount();
        return IntStream.range(0, parameterCount)
                .mapToObj(i -> new MethodParameter(method, i))
                .map(methodParameter -> findMethodParameterResolver(candidates, methodParameter)
                )
                .collect(Collectors.toList());
    }

    private static <T extends NettyCallbackParameterResolver> T findMethodParameterResolver(final Collection<T> candidates,
                                                                                            final MethodParameter methodParameter) {

        //noinspection NestedMethodCall
        return candidates.stream()
                .filter(r -> r.canResolve(methodParameter))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unable to find resolver for " + methodParameter));
    }

    private static void checkForConnectHandler(final Map<String, SortedSet<Method>> connectHandlers, final Method method) {
        final NettyOnConnect annotation = AnnotationUtils.findAnnotation(method, NettyOnConnect.class);
        if (annotation != null) {
            final String serverName = annotation.serverName();
            connectHandlers.computeIfAbsent(serverName, k -> new TreeSet<>(ON_CONNECT_METHOD_COMPARATOR))
                    .add(method);
        }
    }

    private static void checkDuplicateDefinitions(final List<TcpServerProperties> servers) {
        final Set<String> serversInConfig = servers.stream()
                .map(TcpServerProperties::getName)
                .distinct()
                .collect(Collectors.toSet());

        if (serversInConfig.size() != servers.size()) {
            throw new IllegalStateException("Configuration has duplicate server definitions");
        }
    }
}
