package org.kgusarov.integration.spring.netty.empty;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class EmptyApplication {
    public static void main(final String[] args) {
        SpringApplication.run(EmptyApplication.class, args);
    }
}
