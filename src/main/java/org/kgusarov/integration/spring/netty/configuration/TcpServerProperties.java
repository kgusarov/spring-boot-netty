package org.kgusarov.integration.spring.netty.configuration;

import org.kgusarov.integration.spring.netty.ChannelOptions;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Configuration properties for a single TCP server instance
 */
public class TcpServerProperties {
    private @NotBlank String name;
    private @NotBlank String host;
    private @NotNull Integer port;

    private Integer bossThreads;
    private Integer workerThreads;
    private ChannelOptions options;
    private ChannelOptions childOptions;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(final Integer bossThreads) {
        this.bossThreads = bossThreads;
    }

    public Integer getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(final Integer workerThreads) {
        this.workerThreads = workerThreads;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public ChannelOptions getOptions() {
        return options;
    }

    public void setOptions(final ChannelOptions options) {
        this.options = options;
    }

    public ChannelOptions getChildOptions() {
        return childOptions;
    }

    public void setChildOptions(final ChannelOptions childOptions) {
        this.childOptions = childOptions;
    }
}
