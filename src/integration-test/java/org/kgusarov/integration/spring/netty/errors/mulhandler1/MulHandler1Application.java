package org.kgusarov.integration.spring.netty.errors.mulhandler1;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class MulHandler1Application {
    public static void main(final String... args) {
        SpringApplication.run(MulHandler1Application.class, args);
    }
}
