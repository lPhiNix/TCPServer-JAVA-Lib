package org.phinix.lib.server.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.context.ContextFactory;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.core.worker.WorkerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractServer implements Server {
    private static final Logger logger = LogManager.getLogger();

    protected final int port;
    protected final int maxUsers;
    protected final AbstractTaskExecutor asyncGlobalTaskExecutor;
    protected ServerSocket serverSocket;
    protected boolean isRunning;

    private final List<Worker> connectedClients;

    private final WorkerFactory workerFactory;
    private final ContextFactory contextFactory;
    private final ExecutorService threadPool;

    public AbstractServer(int port, int maxUsers, ContextFactory contextFactory, WorkerFactory workerFactory, AbstractTaskExecutor taskExecutor) {
        logger.log(Level.DEBUG, "Initializing");

        this.port = port;
        this.maxUsers = maxUsers;
        this.workerFactory = workerFactory;
        this.contextFactory = contextFactory;
        this.asyncGlobalTaskExecutor = taskExecutor;

        threadPool = Executors.newFixedThreadPool(maxUsers);
        connectedClients = new CopyOnWriteArrayList<>();

        isRunning = false;
    }

    public AbstractServer(int port, int maxUsers, ContextFactory contextFactory, WorkerFactory workerFactory, AbstractTaskExecutor taskExecutor, ServerSocket serverSocket) {
        logger.log(Level.DEBUG, "Initializing with external socket");

        this.port = port;
        this.maxUsers = maxUsers;
        this.workerFactory = workerFactory;
        this.contextFactory = contextFactory;
        this.asyncGlobalTaskExecutor = taskExecutor;
        this.serverSocket = serverSocket;

        threadPool = Executors.newFixedThreadPool(maxUsers);
        connectedClients = new CopyOnWriteArrayList<>();

        isRunning = false;
    }

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

    private Worker createNewClientWorker(Socket clientSocket) throws IOException {
        Context context = contextFactory.createServerContext(this);
        Worker client = workerFactory.createWorker(clientSocket, context);

        addClient(client);

        return client;
    }

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

    public final boolean addClient(Worker worker) {
        if (connectedClients.isEmpty()) {
            return false;
        }

        return connectedClients.add(worker);
    }

    public final boolean removeClient(Worker worker) {
        if (connectedClients.size() >= maxUsers) {
            return false;
        }

        return connectedClients.remove(worker);
    }

    public final List<Worker> getConnectedClients() {
        if (connectedClients.isEmpty()) {
            return null;
        }

        return List.copyOf(connectedClients);
    }

    public AbstractTaskExecutor getAsyncGlobalTaskExecutor() {
        return asyncGlobalTaskExecutor;
    }

    public final int getPort() {
        return port;
    }

    public final int getMaxUsers() {
        return maxUsers;
    }
}