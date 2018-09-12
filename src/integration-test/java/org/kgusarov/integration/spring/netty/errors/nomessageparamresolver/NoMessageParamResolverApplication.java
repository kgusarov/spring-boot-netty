package org.kgusarov.integration.spring.netty.errors.nomessageparamresolver;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNettyServers
@SpringBootApplication
public class NoMessageParamResolverApplication {
    public static void main(final String... args) {
        SpringApplication.run(NoMessageParamResolverApplication.class, args);
    }
}
