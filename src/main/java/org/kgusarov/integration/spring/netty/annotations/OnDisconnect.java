package org.kgusarov.integration.spring.netty.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * This annotation can be used to mark a class that will become a disconnection event
 * handler for the given TCP event.
 * Class should implement {@code org.kgusarov.integration.spring.netty.events.TcpEventHandler}
 * interface. This is enforced during appropriate bean construction
 */
@Component
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnDisconnect {
    /**
     * Get logical name of the server annotated event handler should be attached to
     *
     * @return          Associated server's name
     */
    String serverName();

    /**
     * Get the priority of the given handler. Priority is used to determine in what order
     * event handlers will be called when client disconnects from the server with appropriate name
     *
     * @return          Priority for the given handler
     */
    int priority() default 0;
}
