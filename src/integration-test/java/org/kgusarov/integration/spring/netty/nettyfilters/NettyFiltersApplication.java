package org.kgusarov.integration.spring.netty.nettyfilters;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class NettyFiltersApplication {
    public static void main(final String[] args) {
        SpringApplication.run(NettyFiltersApplication.class, args);
    }
}
