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
import org.phinix.lib.server.service.ServiceRegisterWorker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * {@code AbstractServer} class is an abstract implementation of the {@link Server} interface.
 * This class provides basic functionality for starting, stopping,
 * and managing client connections to the server.
 * <p>
 * Use example:
 * <pre>{@code
 * public class MyServer extends AbstractServer {
 *
 *         public MyServer(int port, int maxUsers) {
 *             super(
 *                 port,
 *                 maxUsers,
 *                 server -> new MyServerContext((MyServer) server),
 *                 (socket, serverContext, serviceRegister) ->
 *                      new MyWorker(
 *                          socket, (MyServerContext) serverContext,
 *                          (AbstractServiceRegister) serviceRegister
 *                      ),
 *                 MyServiceManager::new,
 *                 new MyTaskExecutor(new TaskQueue<>())
 *             );
 *         }
 *     }
 * }
 *
 * @see Server
 * @see Manageable
 * @see Worker
 * @see WorkerFactory
 * @see ContextFactory
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
     * @param taskExecutor the executor for global asynchronous tasks
     */
    public AbstractServer(int port, int maxUsers,
                          ContextFactory contextFactory,
                          WorkerFactory workerFactory,
                          ServiceRegisterWorker serviceRegisterWorker,
                          AbstractTaskExecutor taskExecutor) {
        logger.log(Level.DEBUG, "Initializing");

        this.port = port;
        this.maxUsers = maxUsers;
        this.workerFactory = workerFactory;
        this.contextFactory = contextFactory;
        this.asyncGlobalTaskExecutor = taskExecutor;

        this.serviceRegister = serviceRegisterWorker.createServiceRegister(); // Instantiating new ServiceRegister

        threadPool = Executors.newFixedThreadPool(maxUsers); // ThreadPool with client limit
        connectedClients = new CopyOnWriteArrayList<>(); // Initialize the list of connected clients

        isRunning = false; // initializing running as false
    }

    /**
     * Constructs an AbstractServer with the specified parameters and an external server socket.
     * (Constructor for debugging and unit tests)
     *
     * @param port the port number on which the server listens
     * @param maxUsers the maximum number of concurrent users
     * @param contextFactory the factory for creating Context instances
     * @param workerFactory the factory for creating Worker instances
     * @param taskExecutor the executor for global asynchronous tasks
     * @param serverSocket the external server socket to be used
     */
    public AbstractServer(int port, int maxUsers,
                          ContextFactory contextFactory,
                          WorkerFactory workerFactory,
                          ServiceRegisterWorker serviceRegisterWorker,
                          AbstractTaskExecutor taskExecutor,
                          ServerSocket serverSocket) {
        logger.log(Level.DEBUG, "Initializing with external socket");

        this.port = port;
        this.maxUsers = maxUsers;
        this.workerFactory = workerFactory;
        this.contextFactory = contextFactory;
        this.asyncGlobalTaskExecutor = taskExecutor;
        this.serverSocket = serverSocket;

        this.serviceRegister = serviceRegisterWorker.createServiceRegister(); // Instantiating new ServiceRegister

        threadPool = Executors.newFixedThreadPool(maxUsers); // ThreadPool with client limit
        connectedClients = new CopyOnWriteArrayList<>(); // Initialize the list of connected clients

        isRunning = false; // initializing running as false
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
            serverSocket = new ServerSocket(port); // Create a new ServerSocket to listen on the specified port
            logger.log(Level.INFO, "Initializing server on port: {}", port);

            isRunning = true; // Set the server running flag to true

            asyncGlobalTaskExecutor.start(this); // Start the asynchronous global task executor

            while (isRunning) {
                // Check if the maximum number of users is reached
                if (Thread.activeCount() > maxUsers) {
                    logger.log(Level.WARN, "Max users amount reached: {}. Waiting to accept new connections", maxUsers);
                    continue; // Skip accepting new connections if the limit is reached
                }

                Socket clientSocket = serverSocket.accept(); // Accept a new client connection
                logger.log(Level.INFO, "New connection accepted from: {}", clientSocket.getInetAddress());

                threadPool.submit(createNewClientWorker(clientSocket)); // Submit the new client worker to the thread pool
            }
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error initializing server: ", e); // Log the error if the server fails to initialize
        } finally {
            logger.log(Level.INFO, "Closing server...");
            stop(); // Stop the server in the finally block to ensure it always gets executed
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
        Context context = contextFactory.createServerContext(this); // Create a new context for the server
        Worker client = workerFactory.createWorker(clientSocket, context, serviceRegister); // Create a new worker for the client

        addClient(client); // Add the new client to the connected clients list

        return client; // Return the newly created client worker
    }

    /**
     * Stops the server and closes all client connections.
     * <p>
     * Method override from {@link Server} interface
     */
    @Override
    public void stop() {
        isRunning = false; // Set the server running flag to false
        asyncGlobalTaskExecutor.stop(); // Stop the asynchronous global task executor
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Close the server socket if it's not already closed
                logger.log(Level.INFO, "Server stopped.");
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error closing server: ", e); // Log an error if there's an issue closing the server
        }
        threadPool.shutdown(); // Shutdown the thread pool to clean up resources
    }

    /**
     * Adds a client worker to the list of connected clients.
     *
     * @param worker the client worker to be added
     * @return {@code true} if the client was added successfully, {@code false} otherwise
     */
    public final boolean addClient(Worker worker) {
        if (connectedClients.isEmpty()) {
            return false; // Return false if the connected clients list is empty
        }

        return connectedClients.add(worker); // Add the client worker to the connected clients list
    }

    /**
     * Removes a client worker from the list of connected clients.
     *
     * @param worker the client worker to be removed
     * @return {@code true} if the client was removed successfully, {@code false} otherwise
     */
    public final boolean removeClient(Worker worker) {
        if (connectedClients.size() >= maxUsers) {
            return false; // Return false if the number of connected clients is at the maximum limit
        }

        return connectedClients.remove(worker); // Remove the client worker from the connected clients list
    }

    /**
     * Returns an unmodifiable copy of the list of connected clients.
     *
     * @return a list of connected clients
     */
    public final List<Worker> getConnectedClients() {
        if (connectedClients.isEmpty()) {
            return null; // Return null if no clients are connected
        }

        return List.copyOf(connectedClients); // Return an unmodifiable copy of the connected clients list
    }

    /**
     * Returns the global asynchronous task executor.
     *
     * @return the global asynchronous task executor
     */
    public AbstractTaskExecutor getAsyncGlobalTaskExecutor() {
        return asyncGlobalTaskExecutor; // Return the global asynchronous task executor
    }

    /**
     * Returns the port number on which the server listens.
     *
     * @return the port number
     */
    public final int getPort() {
        return port; // Return the port number
    }

    /**
     * Returns the maximum number of concurrent users.
     *
     * @return the maximum number of concurrent users
     */
    public final int getMaxUsers() {
        return maxUsers; // Return the maximum number of concurrent users
    }
}
