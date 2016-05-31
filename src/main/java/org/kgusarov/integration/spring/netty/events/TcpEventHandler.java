package org.kgusarov.integration.spring.netty.events;

/**
 * Handler for the given TCP event
 *
 * @param <T>           Type of an event-associated data
 */
@FunctionalInterface
public interface TcpEventHandler<T> {
    /**
     * Handle the TCP event
     *
     * @param event     Event to be handled
     */
    void handle(TcpEvent<T> event);
}
