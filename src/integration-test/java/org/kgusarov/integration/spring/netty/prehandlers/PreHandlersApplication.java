package org.kgusarov.integration.spring.netty.prehandlers;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class PreHandlersApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PreHandlersApplication.class, args);
    }
}
