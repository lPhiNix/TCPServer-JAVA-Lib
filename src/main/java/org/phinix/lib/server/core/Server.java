package org.phinix.lib.server.core;

import org.phinix.lib.server.core.task.Task;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;

/**
 * {@code Server} interface represents a multi-thread server that
 * can be able to run {@link Worker} threads to manage async remote clients
 * operations.
 * <p>
 * {@code Manageable} interface permits this server run global async {@link Task}
 * using {@link AbstractTaskExecutor} instance.
 *
 * @see Manageable
 * @see AbstractServer
 *
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
