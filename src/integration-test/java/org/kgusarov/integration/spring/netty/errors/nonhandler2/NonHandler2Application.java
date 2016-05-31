package org.kgusarov.integration.spring.netty.errors.nonhandler2;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class NonHandler2Application {
    public static void main(final String... args) {
        SpringApplication.run(NonHandler2Application.class, args);
    }
}
