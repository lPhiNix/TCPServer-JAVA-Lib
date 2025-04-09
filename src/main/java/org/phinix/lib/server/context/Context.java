package org.phinix.lib.server.context;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.core.worker.AbstractWorker;
import org.phinix.lib.server.core.Server;

import java.util.List;

/**
 * {@code Context} class representing the context of the server, including connected clients.
 * This class acts as a mediator between the {@link Server} instance and {@link Worker} instances because
 * it encapsulates server data from client connections.
 * <p>
 * It is recommended to write a context subclass to add specific server dependencies and its concrete
 * dependencies that you want workers to access.
 *
 * @see ContextFactory
 * @see Server
 * @see AbstractServer
 * @see Worker
 * @see AbstractWorker
 */
public class Context {
    private static final Logger logger = LogManager.getLogger();

    protected final AbstractServer server; // The server associated with this context

    /**
     * Constructs a new Context with the specified server.
     *
     * @param server the server
     */
    public Context(AbstractServer server) {
        this.server = server;
        // Log the creation of the Context instance
        logger.log(Level.DEBUG, "Context created for server: {}", server.getClass().getSimpleName());
    }

    /**
     * Returns a list of connected clients.
     * This method fetches the current list of workers (clients) connected to the server.
     *
     * @return a list of connected clients
     */
    public List<Worker> getConnectedClients() {
        logger.log(Level.DEBUG, "Fetching list of connected clients...");
        return server.getConnectedClients();
    }
}
