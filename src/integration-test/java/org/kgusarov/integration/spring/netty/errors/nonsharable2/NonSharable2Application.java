package org.kgusarov.integration.spring.netty.errors.nonsharable2;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class NonSharable2Application {
    public static void main(final String... args) {
        SpringApplication.run(NonSharable2Application.class, args);
    }
}
