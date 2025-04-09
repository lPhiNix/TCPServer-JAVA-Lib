package org.phinix.lib.client;

/**
 * {@code Client} interface representing a client that can connect to a server.
 * <p>
 * This interface defines the basic actions that a client can perform when interacting with a server,
 * including connecting to a server, starting communication, and disconnecting.
 */
public interface Client {

    /**
     * Connects the client to the server at the specified address and port.
     *
     * @param serverAddress the address of the server
     * @param serverPort the port of the server
     */
    void connect(String serverAddress, int serverPort);

    /**
     * Connects the client to the default server address and port.
     * <p>
     * This method uses pre-configured default values for the server's address and port
     * to establish the connection. It is a convenience method for scenarios where
     * the default server settings are used.
     */
    void connect();

    /**
     * Starts the client, handling communication with the server.
     * <p>
     * Once the client is connected, this method begins the process of handling
     * the communication with the server. This includes sending and receiving messages.
     * This method must be called after the client has successfully connected to the server.
     */
    void start();

    /**
     * Disconnects the client from the server.
     * <p>
     * This method gracefully disconnects the client from the server, ensuring
     * that any ongoing communication is properly closed before disconnecting.
     */
    void disconnect();
}
