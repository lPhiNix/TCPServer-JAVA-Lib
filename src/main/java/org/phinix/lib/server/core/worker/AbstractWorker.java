package org.phinix.lib.server.core.worker;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.common.model.room.RoomImpl;
import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;
import org.phinix.lib.server.service.AbstractServiceRegister;
import org.phinix.lib.common.socket.MessagesManager;

import java.io.IOException;
import java.net.Socket;

/**
 * {@code AbstractWorker} class is an abstract implementation of the {@link Worker} interface.
 * This class provides basic functionality for handling client communication.
 *
 * <p>
 * Use example:
 * <pre>{@code
 * public class ClientHandler extends AbstractWorker {
 *
 *     public ClientHandler(Socket socket, ServiceManager serviceManager) throws IOException {
 *         super(socket, serviceManager);
 *         this.serviceRegister = serviceManager;
 *     }
 *
 *     \@Override
 *     public void listen(String message) {
 *         CommandProcessor commandProcessor = getServiceRegister().getService(CommandProcessor.class);
 *         if (!commandProcessor.processCommand(message, this)) {
 *             getMessagesManager().sendMessage(getClientAddress() + ": " + message);
 *         }
 *     }
 * }
 * </pre>}
 *
 * @see Worker
 * @see WorkerFactory
 * @see MessagesManager
 */
public abstract class AbstractWorker implements Worker {
    private static final Logger logger = LogManager.getLogger();

    protected final Socket socket; // Client socket
    protected final MessagesManager messagesManager; // Messages manager for client communication
    protected final AbstractServiceRegister serviceRegister; // Service register
    protected final Context serverContext; // Server context
    protected AbstractTaskExecutor asyncClientTaskExecutor; // Executor for asynchronous client tasks (Raw param: <>)
    protected RoomImpl currentRoomImpl; // Current room the worker is in
    protected boolean isRunning; // Flag indicating whether the worker is running

    /**
     * Constructs an AbstractWorker with the specified parameters.
     *
     * @param socket          the client socket
     * @param serverContext   the server context
     * @param serviceRegister the service register
     * @param taskExecutor    the executor for asynchronous client tasks
     * @throws IOException if an I/O error occurs
     */
    public AbstractWorker(Socket socket,
                          Context serverContext,
                          AbstractServiceRegister serviceRegister,
                          AbstractTaskExecutor taskExecutor) throws IOException {
        logger.log(Level.DEBUG, "Initializing");

        this.socket = socket;
        this.messagesManager = new MessagesManager(socket);
        this.serviceRegister = serviceRegister;
        this.serverContext = serverContext;
        this.asyncClientTaskExecutor = taskExecutor;

        isRunning = true; // initializing running in true
    }

    /**
     * Method {@code run()} from {@link Runnable} that will be executed when
     * this thread start its live cycle.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            logger.log(Level.INFO, "Listening client {}", socket.getInetAddress());

            // Start Async Worker Task
            asyncClientTaskExecutor.start(this);

            while (isRunning) {
                if (!listenLoop()) { // Listening always while worker is running
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error connecting to client: ", e);
        } catch (Exception e) {
            logger.log(Level.FATAL, "Error managing client input: ", e);
        } finally {
            logger.log(Level.INFO, "Stopping worker {}...", socket.getInetAddress());
            closeConnection(); // Closing connection in end of thread live cycle
        }
    }

    /**
     * Listens for messages from the client in a loop.
     *
     * @return {@code true} if the loop should continue, {@code false} otherwise
     * @throws IOException if an I/O error occurs
     */
    protected boolean listenLoop() throws IOException {
        String line;
        if ((line = messagesManager.receiveMessage()) != null) {
            listen(line); // Listening
            return true;
        }

        return false; // If message is null, the connection with clients has ended.
    }

    /**
     * Listens for a message from the client.
     * This method should be implemented by subclasses to handle client messages.
     * <p>
     * In subclass will use this method to handle client message that iy receive
     *
     * @param line the message received from the client
     * @throws IOException if an I/O error occurs
     */
    @Override
    public abstract void listen(String line) throws IOException;

    /**
     * Returns the messages manager for client communication.
     *
     * @return the messages manager
     */
    @Override
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    /**
     * Returns the service register.
     *
     * @return the service register
     */
    @Override
    public AbstractServiceRegister getServiceRegister() {
        return serviceRegister;
    }

    /**
     * Returns the server context.
     *
     * @return the server context
     */
    @Override
    public Context getServerContext() {
        return serverContext;
    }

    /**
     * Returns the current room the worker is in.
     *
     * @return the current room
     */
    @Override
    public RoomImpl getCurrentRoom() {
        return currentRoomImpl;
    }

    /**
     * Sets the current room the worker is in.
     *
     * @param roomImpl the current room
     */
    @Override
    public void setCurrentRoom(RoomImpl roomImpl) {
        this.currentRoomImpl = roomImpl;
    }

    /**
     * Returns the client's address.
     *
     * @return the client's address
     */
    @Override
    public String getClientAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    /**
     * Returns the client socket.
     *
     * @return the client socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Closes the connection to the client.
     */
    @Override
    public void closeConnection() {
        isRunning = false;
        asyncClientTaskExecutor.stop();
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logger.log(Level.INFO, "Worker stopped.");
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error closing worker: ", e);
        }
    }
}