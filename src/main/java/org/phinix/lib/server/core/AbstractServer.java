package org.phinix.lib.server.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.context.ContextFactory;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.core.worker.WorkerFactory;
import org.phinix.lib.server.service.AbstractServiceRegister;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// todo example does not finish
/**
 * {@code AbstractServer} class is an abstract implementation of the {@link Server} interface.
 * This class provides basic functionality for starting, stopping,
 * and managing client connections to the server.
 * <p>
 * Use example:
 * <pre>
 *     public class ServerImpl extends AbstractServer {
 *     public ServerImpl(int port, int maxUsers) {
 *         super(
 *
 *         );
 *     }
 * }
 * </pre>
 *
 * @see Server
 * @see Manageable
 * @see Worker
 * @see WorkerFactory
 * @see AbstractServiceRegister
 * @see AbstractTaskExecutor
 */
public abstract class AbstractServer implements Server {
    private static final Logger logger = LogManager.getLogger();

    protected final int port; // Port number on which the server listens
    protected final int maxUsers; // Maximum number of concurrent users
    protected final AbstractServiceRegister serviceRegister; // Service manager for saving current running service in server
    protected final AbstractTaskExecutor asyncGlobalTaskExecutor; // Executor for global asynchronous tasks
    protected ServerSocket serverSocket; // Server socket for accepting client connections
    protected boolean isRunning; // Flag indicating whether the server is running

    private final List<Worker> connectedClients; // List of connected clients

    private final WorkerFactory workerFactory; // Factory for creating Worker instances
    private final ContextFactory contextFactory; // Factory for creating Context instances
    private final ExecutorService threadPool; // Thread pool for handling client connections

    /**
     * Constructs an AbstractServer with the specified parameters.
     *
     * @param port the port number on which the server listens
     * @param maxUsers the maximum number of concurrent users
     * @param contextFactory the factory for creating Context instances
     * @param workerFactory the factory for creating Worker instances
     * @param serviceRegister the manager for handling running service
     * @param taskExecutor the executor for global asynchronous tasks
     */
    public AbstractServer(int port, int maxUsers, ContextFactory contextFactory, WorkerFactory workerFactory, AbstractServiceRegister serviceRegister, AbstractTaskExecutor taskExecutor) {
        logger.log(Level.DEBUG, "Initializing");

        this.port = port;
        this.maxUsers = maxUsers;
        this.workerFactory = workerFactory;
        this.contextFactory = contextFactory;
        this.asyncGlobalTaskExecutor = taskExecutor;

        this.serviceRegister = serviceRegister;

        threadPool = Executors.newFixedThreadPool(maxUsers);
        connectedClients = new CopyOnWriteArrayList<>();

        isRunning = false;
    }

    /**
     * Constructs an AbstractServer with the specified parameters and an external server socket.
     * (Construct for debug and do unit tests)
     *
     * @param port the port number on which the server listens
     * @param maxUsers the maximum number of concurrent users
     * @param contextFactory the factory for creating Context instances
     * @param workerFactory the factory for creating Worker instances
     * @param taskExecutor the executor for global asynchronous tasks
     * @param serviceRegister the manager for handling running service
     * @param serverSocket the external server socket to be used
     */
    public AbstractServer(int port, int maxUsers, ContextFactory contextFactory, WorkerFactory workerFactory, AbstractServiceRegister serviceRegister, AbstractTaskExecutor taskExecutor, ServerSocket serverSocket) {
        logger.log(Level.DEBUG, "Initializing with external socket");

        this.port = port;
        this.maxUsers = maxUsers;
        this.workerFactory = workerFactory;
        this.contextFactory = contextFactory;
        this.asyncGlobalTaskExecutor = taskExecutor;
        this.serverSocket = serverSocket;

        this.serviceRegister = serviceRegister;

        threadPool = Executors.newFixedThreadPool(maxUsers);
        connectedClients = new CopyOnWriteArrayList<>();

        isRunning = false;
    }

    /**
     * Starts the server and begins accepting client connections.
     * This method blocks until the server is stopped.
     * <p>
     * Method override from {@link Server} interface
     */
    @Override @SuppressWarnings("unchecked")
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            logger.log(Level.INFO, "Initializing server on port: {}", port);

            isRunning = true;

            asyncGlobalTaskExecutor.start(this);

            while (isRunning) {
                if (Thread.activeCount() > maxUsers) {
                    logger.log(Level.WARN, "Max users amount reached: {}. Waiting to accept new connections", maxUsers);
                    continue;
                }

                Socket clientSocket = serverSocket.accept();
                logger.log(Level.INFO, "New connection accepted from: {}", clientSocket.getInetAddress());

                threadPool.submit(createNewClientWorker(clientSocket));
            }
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error initializing server: ", e);
        } finally {
            logger.log(Level.INFO, "Closing server...");
            stop();
        }
    }

    /**
     * Creates a new client worker for the specified client socket.
     *
     * @param clientSocket the client socket
     * @return the created Worker
     * @throws IOException if an I/O error occurs
     */
    private Worker createNewClientWorker(Socket clientSocket) throws IOException {
        Context context = contextFactory.createServerContext(this);
        Worker client = workerFactory.createWorker(clientSocket, context, serviceRegister);

        addClient(client);

        return client;
    }

    /**
     * Stops the server and closes all client connections.
     * <p>
     * Method override from {@link Server} interface
     */
    @Override
    public void stop() {
        isRunning = false;
        asyncGlobalTaskExecutor.stop();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                logger.log(Level.INFO, "Server stopped.");
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error closing server: ", e);
        }
        threadPool.shutdown();
    }

    /**
     * Adds a client worker to the list of connected clients.
     *
     * @param worker the client worker to be added
     * @return {@code true} if the client was added successfully, {@code false} otherwise
     */
    public final boolean addClient(Worker worker) {
        if (connectedClients.isEmpty()) {
            return false;
        }

        return connectedClients.add(worker);
    }

    /**
     * Removes a client worker from the list of connected clients.
     *
     * @param worker the client worker to be removed
     * @return {@code true} if the client was removed successfully, {@code false} otherwise
     */
    public final boolean removeClient(Worker worker) {
        if (connectedClients.size() >= maxUsers) {
            return false;
        }

        return connectedClients.remove(worker);
    }

    /**
     * Returns an unmodifiable copy of the list of connected clients.
     *
     * @return a list of connected clients
     */
    public final List<Worker> getConnectedClients() {
        if (connectedClients.isEmpty()) {
            return null;
        }

        return List.copyOf(connectedClients);
    }

    /**
     * Returns the global asynchronous task executor.
     *
     * @return the global asynchronous task executor
     */
    public AbstractTaskExecutor getAsyncGlobalTaskExecutor() {
        return asyncGlobalTaskExecutor;
    }

    /**
     * Returns the port number on which the server listens.
     *
     * @return the port number
     */
    public final int getPort() {
        return port;
    }

    /**
     * Returns the maximum number of concurrent users.
     *
     * @return the maximum number of concurrent users
     */
    public final int getMaxUsers() {
        return maxUsers;
    }
}
