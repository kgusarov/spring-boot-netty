package org.kgusarov.integration.spring.netty.errors.nodefs;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class NoDefsApplication {
    public static void main(final String... args) {
        SpringApplication.run(NoDefsApplication.class, args);
    }
}
