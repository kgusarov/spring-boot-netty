package org.kgusarov.integration.spring.netty.errors.noconnectparamresolver;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class NoConnectParamResolverApplication {
    public static void main(final String... args) {
        SpringApplication.run(NoConnectParamResolverApplication.class, args);
    }
}
