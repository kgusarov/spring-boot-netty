package org.kgusarov.integration.spring.netty.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "netty")
public class SpringNettyConfigurationProperties {
    private List<TcpServerProperties> servers;

    public List<TcpServerProperties> getServers() {
        return servers;
    }

    public void setServers(final List<TcpServerProperties> servers) {
        this.servers = servers;
    }
}
