package org.phinix.lib.server.context;

import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.core.worker.AbstractWorker;
import org.phinix.lib.server.core.Server;

import java.util.List;

/**
 * {@code Context} class representing the context of the server, including connected clients.
 * This class act as mediator class between {@link Server} instance and {@link Worker} instances because
 * encapsulate server data from client connections.
 * <p>
 * It's recommended write a context subclass to add a specific server dependence and its concrete
 * dependence that you want to worker can access.
 *
 * @see ContextFactory
 * @see Server
 * @see AbstractServer
 * @see Worker
 * @see AbstractWorker
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
