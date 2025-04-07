package org.phinix.lib.server.context;

import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.worker.Worker;

import java.util.List;

/**
 * Class representing the context of the server, including connected clients.
 */
public class Context {
    protected final AbstractServer server; // The server associated with this context

    /**
     * Constructs a new Context with the specified server.
     *
     * @param server the server
     */
    public Context(AbstractServer server) {
        this.server = server;
    }

    /**
     * Returns a list of connected clients.
     *
     * @return a list of connected clients
     */
    public List<Worker> getConnectedClients() {
        return server.getConnectedClients();
    }
}
