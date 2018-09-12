package org.kgusarov.integration.spring.netty.onmessagenohandler;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableNettyServers
@SpringBootApplication
public class OnMessageNoHandlerApplication {
    public static void main(final String[] args) {
        SpringApplication.run(OnMessageNoHandlerApplication.class, args);
    }

    @Bean
    public ProcessingCounter counter() {
        return new ProcessingCounter(1);
    }
}
