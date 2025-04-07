package org.phinix.lib.server.context;

import org.phinix.lib.server.core.AbstractServer;

/**
 * Factory interface for creating {@link Context} instances.
 *
 * @see Context
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