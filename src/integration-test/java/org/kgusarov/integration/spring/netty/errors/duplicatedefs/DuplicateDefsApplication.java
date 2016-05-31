package org.kgusarov.integration.spring.netty.errors.duplicatedefs;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class DuplicateDefsApplication {
    public static void main(final String... args) {
        SpringApplication.run(DuplicateDefsApplication.class, args);
    }
}
