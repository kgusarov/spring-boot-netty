package org.kgusarov.integration.spring.netty;

import com.google.common.util.concurrent.Futures;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Component that performs initialization of all the netty servers
 */
@Component
@ConditionalOnBean(NettyServers.class)
public final class TcpServerLifeCycle {
    @Autowired
    private NettyServers nettyServers;

    /**
     * Start all servers
     */
    @PostConstruct
    public void start() {
        try {
            Futures.allAsList(nettyServers.stream()
                    .map(TcpServer::start)
                    .collect(Collectors.toList())).get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new BeanInitializationException("Failed to start NETTY servers", e);
        }
    }

    /**
     * Stop all servers
     */
    @PreDestroy
    public void stop() {
        nettyServers.stream().forEach(TcpServer::stop);
    }
}
