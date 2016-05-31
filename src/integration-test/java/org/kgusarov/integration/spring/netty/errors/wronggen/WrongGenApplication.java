package org.kgusarov.integration.spring.netty.errors.wronggen;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class WrongGenApplication {
    public static void main(final String... args) {
        SpringApplication.run(WrongGenApplication.class, args);
    }
}
