package org.kgusarov.integration.spring.netty.errors.duplicatemessagebodies;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class DuplicateMessageBodiesApplication {
    public static void main(final String... args) {
        SpringApplication.run(DuplicateMessageBodiesApplication.class, args);
    }
}
