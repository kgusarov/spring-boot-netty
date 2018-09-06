package org.kgusarov.integration.spring.netty.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to mark a method that will become a disconnect event
 * handler for the given TCP server.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NettyOnDisconnect {
    /**
     * Get logical name of the server annotated event handler should be attached to
     *
     * @return          Associated server's name
     */
    String serverName();

    /**
     * Get the priority of the given handler. Priority is used to determine in what order
     * event handlers will be called when client disconnects from server with appropriate name
     *
     * @return          Priority for the given handler
     */
    int priority() default 0;
}
