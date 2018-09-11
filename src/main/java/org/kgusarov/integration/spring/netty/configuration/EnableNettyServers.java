package org.kgusarov.integration.spring.netty.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation to enable Netty TCP servers.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(SpringNettyConfiguration.class)
public @interface EnableNettyServers {
}
