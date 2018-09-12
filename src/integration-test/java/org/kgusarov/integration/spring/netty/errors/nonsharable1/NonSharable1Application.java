package org.kgusarov.integration.spring.netty.errors.nonsharable1;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class NonSharable1Application {
    public static void main(final String... args) {
        SpringApplication.run(NonSharable1Application.class, args);
    }
}
