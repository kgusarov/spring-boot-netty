package org.kgusarov.integration.spring.netty.errors.nonsharable;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class NonSharableApplication {
    public static void main(final String... args) {
        SpringApplication.run(NonSharableApplication.class, args);
    }
}
