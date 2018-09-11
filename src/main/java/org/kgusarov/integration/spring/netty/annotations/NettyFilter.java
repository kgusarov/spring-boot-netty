package org.kgusarov.integration.spring.netty.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * This annotation can be used to mark so called "Filters" - handlers that will be
 * invoked before any {@link NettyController}
 * instances will start message processing or after it will be finished. So basically, handlers will
 * work in a way similar to servlet filters. This may include encoders/decoders and another
 * stuff that can be used to preprocess the message before handling it or post-process it afterwards.
 *
 * Class should implement {@code io.netty.channel.ChannelHandler}
 * interface. This is enforced during appropriate bean construction
 */
@Component
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NettyFilter {
    /**
     * Get logical name of the server annotated event handler should be attached to
     *
     * @return          Associated server's name
     */
    String serverName();

    /**
     * Get the priority of the given handler. Priority is used to determine in what order
     * event handlers will be called
     *
     * @return          Priority for the given handler
     */
    int priority() default 0;
}
