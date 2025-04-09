package org.phinix.lib.server.context;

import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.core.worker.AbstractWorker;

/**
 * {@code ContextFactory} interface is factory interface for creating {@link Context} instances.
 * This interface is used for create instance of a concrete Context class (subclass) type.
 * <p>
 * This interface implements {@code Factory Design Pattern}
 *
 * @see Context
 * @see Worker
 * @see AbstractWorker
 */
public interface ContextFactory {

    /**
     * Creates a new {@link Context} for the specified server.
     *
     * @param server the server
     * @return the created Context
     */
    Context createServerContext(AbstractServer server);
}