package org.phinix.lib.client;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.common.socket.MessagesManager;

import java.io.IOException;
import java.net.Socket;

/**
 * {@code AbstractClient} abstract class is a abstract implementation of the {@link Client} interface.
 * <p>
 * This class provides basic functionality for connecting, starting, and disconnecting from a server.
 * It handles establishing a connection to the server, sending and receiving messages, and managing the socket connection.
 * The actual logic for handling received messages and user input is left to be implemented by subclasses.
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
     * <p>
     * This method establishes a socket connection to the server and initializes the MessagesManager
     * for handling communication. If the connection is successful, the {@code isConnected} flag is set to true.
     *
     * @param serverAddress the address of the server
     * @param serverPort the port of the server
     */
    @Override
    public void connect(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort); // Establishes the socket connection
            messagesManager = new MessagesManager(socket); // Initializes the messages manager

            isConnected = true; // Sets the connection flag to true

            this.serverAddress = serverAddress; // Updates the server address
            this.serverPort = serverPort; // Updates the server port

            logger.log(Level.DEBUG, "Connection established successfully to server {}:{}.", serverAddress, serverPort);
        } catch (IOException e) {
            // Logs error if the connection fails
            logger.log(Level.ERROR, "Error connecting to server {}:{} - Exception: {}", serverAddress, serverPort, e.getMessage());
        }
    }

    /**
     * Connects the client to the default server address and port.
     * <p>
     * This method uses the default server settings defined by {@code serverAddress} and {@code serverPort}.
     * It establishes a socket connection and initializes the MessagesManager.
     */
    @Override
    public void connect() {
        try {
            socket = new Socket(serverAddress, serverPort); // Establishes the socket connection
            messagesManager = new MessagesManager(socket); // Initializes the messages manager

            isConnected = true; // Sets the connection flag to true

            logger.log(Level.DEBUG, "Standard connection established successfully to default server {}:{}.", serverAddress, serverPort);
        } catch (IOException e) {
            // Logs error if the connection fails
            logger.log(Level.ERROR, "Error connecting to default server {}:{} - Exception: {}", serverAddress, serverPort, e.getMessage());
        }
    }

    /**
     * Starts the client, handling communication with the server.
     * <p>
     * This method starts a new thread to listen for messages from the server and also handles user input.
     * It must be called after the client has successfully connected.
     */
    @Override
    public void start() {
        if (socket == null || !isConnected) {
            // Checks if the socket is initialized and if the client is connected before starting
            logger.log(Level.DEBUG, "Cannot start client. No active connection to server.");
            return;
        }

        Thread listener = new Thread(this::handleReceivedMessage); // Creates a thread to listen for messages from the server
        listener.start(); // Starts the listener thread

        handleInputUser(); // Handles user input, implemented by subclasses
    }

    /**
     * Handles received messages from the server.
     * <p>
     * This method should be implemented by subclasses to process and act on the messages received from the server.
     */
    protected abstract void handleReceivedMessage();

    /**
     * Handles user input.
     * <p>
     * This method should be implemented by subclasses to process input provided by the user.
     */
    protected abstract void handleInputUser();

    /**
     * Disconnects the client from the server.
     * <p>
     * This method closes the socket connection to the server and sets the connection flag to false.
     * It should be called when the client no longer needs to communicate with the server.
     */
    @Override
    public void disconnect() {
        isConnected = false; // Sets the connection flag to false, indicating the client is disconnected
        try {
            if (socket != null) {
                socket.close(); // Closes the socket connection to the server
                logger.log(Level.DEBUG, "Connection to server {}:{} closed successfully.", serverAddress, serverPort);
            }
        } catch (IOException e) {
            // Logs error if the disconnection fails
            logger.log(Level.ERROR, "Error disconnecting from server {}:{} - Exception: {}", serverAddress, serverPort, e.getMessage());
        }
    }

    /**
     * Returns the server address.
     *
     * @return the server address
     */
    public String getServerAddress() {
        return serverAddress; // Returns the current server address
    }

    /**
     * Returns the server port.
     *
     * @return the server port
     */
    public int getServerPort() {
        return serverPort; // Returns the current server port
    }

    /**
     * Sets the server address.
     *
     * @param serverAddress the server address
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress; // Updates the server address
    }

    /**
     * Sets the server port.
     *
     * @param serverPort the server port
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort; // Updates the server port
    }
}
