package org.kgusarov.integration.spring.netty.errors.nodisconnectparamresolver;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class NoDisconnectParamResolverApplication {
    public static void main(final String... args) {
        SpringApplication.run(NoDisconnectParamResolverApplication.class, args);
    }
}
