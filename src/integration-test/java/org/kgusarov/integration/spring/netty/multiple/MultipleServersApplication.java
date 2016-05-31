package org.kgusarov.integration.spring.netty.multiple;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class MultipleServersApplication {
    public static void main(final String[] args) {
        SpringApplication.run(MultipleServersApplication.class, args);
    }
}
