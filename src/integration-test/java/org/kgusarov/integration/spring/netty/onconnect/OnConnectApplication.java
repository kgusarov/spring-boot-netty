package org.kgusarov.integration.spring.netty.onconnect;

import org.kgusarov.integration.spring.netty.configuration.EnableNettyServers;
import org.kgusarov.integration.spring.netty.etc.CyclicWaitForProcessingToComplete;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableNettyServers
@SpringBootApplication
public class OnConnectApplication {
    public static void main(final String[] args) {
        SpringApplication.run(OnConnectApplication.class, args);
    }

    @Bean
    public CyclicWaitForProcessingToComplete counter() {
        return new CyclicWaitForProcessingToComplete(3);
    }
}
