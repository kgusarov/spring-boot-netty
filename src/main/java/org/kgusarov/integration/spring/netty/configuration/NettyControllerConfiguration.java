package org.kgusarov.integration.spring.netty.configuration;

import org.kgusarov.integration.spring.netty.ChannelOptions;
import org.kgusarov.integration.spring.netty.TcpServer;
import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Author: Konstantin Gusarov
 */
@Configuration
@EnableConfigurationProperties(SpringNettyConfigurationProperties.class)
public class NettyControllerConfiguration {
    private final SpringNettyConfigurationProperties configurationProperties;
    private final ConfigurableListableBeanFactory beanFactory;

    @Autowired
    public NettyControllerConfiguration(final SpringNettyConfigurationProperties configurationProperties,
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

        final Map<String, Object> controllers = beanFactory.getBeansWithAnnotation(NettyController.class);

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

            result.add(server);
        }

        return result;
    }
}
