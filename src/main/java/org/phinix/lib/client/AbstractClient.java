package org.phinix.lib.client;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.common.util.MessagesManager;

import java.io.IOException;
import java.net.Socket;

/**
 * Abstract implementation of the {@link Client} interface.
 * This class provides basic functionality for connecting, starting, and disconnecting from a server.
 *
 * @see Client
 */
public abstract class AbstractClient implements Client {
    private static final Logger logger = LogManager.getLogger();

    protected String serverAddress = "localhost"; // Default server address
    protected int serverPort = 12345; // Default server port

    protected MessagesManager messagesManager; // Manages communication with the server
    protected Socket socket; // Client socket
    protected boolean isConnected; // Flag indicating whether the client is connected

    /**
     * Connects the client to the server at the specified address and port.
     *
     * @param serverAddress the address of the server
     * @param serverPort the port of the server
     */
    @Override
    public void connect(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort); // Establishes the socket connection
            messagesManager = new MessagesManager(socket); // Initializes the messages manager

            isConnected = true; // Sets the connection flag

            this.serverAddress = serverAddress; // Updates the server address
            this.serverPort = serverPort; // Updates the server port

            logger.log(Level.DEBUG, "Connection established successfully!");
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error connecting server {}:{}. Exception: ", new Object[]{serverAddress, serverPort}, e);
        }
    }

    /**
     * Connects the client to the default server address and port.
     */
    @Override
    public void connect() {
        try {
            socket = new Socket(serverAddress, serverPort); // Establishes the socket connection
            messagesManager = new MessagesManager(socket); // Initializes the messages manager

            isConnected = true; // Sets the connection flag

            logger.log(Level.DEBUG, "Standard connection established successfully!");
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error connecting server {}:{}. Exception: ", new Object[]{serverAddress, serverPort}, e);
        }
    }

    /**
     * Starts the client, handling communication with the server.
     * This method starts a thread to listen for messages from the server and handles user input.
     */
    @Override
    public void start() {
        if (socket == null) {
            return;
        }

        Thread listener = new Thread(this::handleReceivedMessage); // Creates a thread to listen for messages
        listener.start(); // Starts the listener thread

        handleInputUser(); // Handles user input
    }

    /**
     * Handles received messages from the server.
     * This method should be implemented by subclasses to process server messages.
     */
    protected abstract void handleReceivedMessage();

    /**
     * Handles user input.
     * This method should be implemented by subclasses to process user input.
     */
    protected abstract void handleInputUser();

    /**
     * Disconnects the client from the server.
     */
    @Override
    public void disconnect() {
        isConnected = false; // Sets the connection flag to false
        try {
            if (socket != null) {
                socket.close(); // Closes the socket connection
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error disconnecting server {}:{}. Exception: ", new Object[]{serverAddress, serverPort}, e);
        }
    }

    /**
     * Returns the server address.
     *
     * @return the server address
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Returns the server port.
     *
     * @return the server port
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Sets the server address.
     *
     * @param serverAddress the server address
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * Sets the server port.
     *
     * @param serverPort the server port
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}