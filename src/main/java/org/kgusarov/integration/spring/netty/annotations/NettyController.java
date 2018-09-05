package org.kgusarov.integration.spring.netty.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Class that is marked with this annotation is considered to be a controller that is able to handle
 * various events received from Netty server. One might think of it as a concept similart
 * to Controller from spring-webmvc.
 */
@Component
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NettyController {
}
