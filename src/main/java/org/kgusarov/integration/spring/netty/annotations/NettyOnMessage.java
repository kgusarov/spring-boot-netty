package org.kgusarov.integration.spring.netty.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to mark a method that will become a handler for the given
 * TCP event with data.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NettyOnMessage {
    /**
     * Get logical name of the server annotated events handler should be attached to
     *
     * @return          Associated server's name
     */
    String serverName();

    /**
     * Get the priority of the given handler. Priority is used to determine in what order
     * events handlers will be called when appropriate message arrives
     *
     * @return          Priority for the given handler
     */
    int priority() default 0;
}
