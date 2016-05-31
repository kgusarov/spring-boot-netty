package org.kgusarov.integration.spring.netty.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * This annotation can be used to mark a class that will become a handler for the given
 * TCP event with data.
 * Class should implement {@code org.kgusarov.integration.spring.netty.events.TcpEventHandler}
 * interface. This is enforced during appropriate bean construction
 */
@Component
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface On {
    /**
     * Get logical name of the server annotated events handler should be attached to
     *
     * @return          Associated server's name
     */
    String serverName();

    /**
     * Get type of the data that should be handled by annotated events handler
     *
     * @return          Event data type
     */
    Class<?> dataType();

    /**
     * Get the priority of the given handler. Priority is used to determine in what order
     * events handlers will be called when appropriate message arrives
     *
     * @return          Priority for the given handler
     */
    int priority() default 0;
}
