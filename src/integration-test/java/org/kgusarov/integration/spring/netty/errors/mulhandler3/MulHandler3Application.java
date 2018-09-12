package org.kgusarov.integration.spring.netty.errors.mulhandler3;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class MulHandler3Application {
    public static void main(final String... args) {
        SpringApplication.run(MulHandler3Application.class, args);
    }
}
