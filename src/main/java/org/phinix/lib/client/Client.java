package org.phinix.lib.client;

/**
 * Interface representing a client that can connect to a server.
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
     */
    void connect();

    /**
     * Starts the client, handling communication with the server.
     */
    void start();

    /**
     * Disconnects the client from the server.
     */
    void disconnect();
}