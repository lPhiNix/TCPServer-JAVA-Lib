package org.phinix.lib.server.core;

import org.phinix.lib.server.core.task.Task;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;

/**
 * {@code Server} interface represents a multi-threaded server that
 * is capable of running {@link Worker} threads to manage asynchronous remote client
 * operations.
 * <p>
 * It also extends the {@link Manageable} interface, allowing this server to execute global asynchronous {@link Task}
 * instances using an {@link AbstractTaskExecutor}.
 *
 * @see Manageable
 * @see AbstractServer
 */
public interface Server extends Manageable {

    /**
     * Starts the server and begins accepting client connections.
     * Implementations should initialize resources and launch worker threads.
     */
    void start();

    /**
     * Stops the server and closes all client connections.
     * Implementations should cleanly shut down all threads and release resources.
     */
    void stop();
}
