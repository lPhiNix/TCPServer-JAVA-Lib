package org.phinix.lib.server.core;

/**
 * {@code Server} interface represents a server that can be managed and controlled.
 * This interface extends {@link Manageable} and provides methods to start and stop the server.
 *
 * @see Manageable
 */
public interface Server extends Manageable {

    /**
     * Starts the server and begins accepting client connections.
     */
    void start();

    /**
     * Stops the server and closes all client connections.
     */
    void stop();
}
