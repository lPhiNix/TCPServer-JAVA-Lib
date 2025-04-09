package org.phinix.lib.common.socket;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.core.worker.AbstractWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;

/**
 * {@code MessageManager} class manages messages sent to and received from a client socket.
 * <p>
 * This class encapsulates the logic for handling socket-based message transfers, making it easier to read and maintain.
 * It allows for sending and receiving messages to/from clients through a socket.
 *
 * @see Worker
 * @see AbstractWorker
 */
public class MessagesManager {
    private static final Logger logger = LogManager.getLogger();

    private final Socket socket; // Client socket for communication
    private final BufferedReader input; // Input stream for receiving messages
    private final PrintWriter output; // Output stream for sending messages

    /**
     * Constructs a new MessagesManager with the specified client socket.
     * <p>
     * Initializes the input and output streams used for communication with the client.
     *
     * @param socket the client socket
     * @throws IOException if an I/O error occurs while creating the streams
     */
    public MessagesManager(Socket socket) throws IOException {
        logger.log(Level.DEBUG, "Initializing MessagesManager for socket: {}", socket);

        this.socket = socket;

        this.input = createSocketInput(); // Creates input stream for receiving messages
        this.output = createSocketOutput(); // Creates output stream for sending messages
    }

    /**
     * Sends a message to the client.
     * <p>
     * This method writes the specified message to the output stream.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        output.println(message); // Sends the message to the client
        logger.log(Level.DEBUG, "Sent message to client: {}", message); // Logs the sent message
    }

    /**
     * Sends a message to the client and logs it.
     * <p>
     * This method logs the message at the specified log level and then sends it to the client.
     *
     * @param level the logging level
     * @param message the message to send
     * @param args the arguments for the message
     */
    public void sendMessageAndLog(Level level, String message, Object... args) {
        logger.log(level, message, args); // Logs the message with the provided log level and arguments
        output.println(message); // Sends the message to the client
    }

    /**
     * Receives a message from the client.
     * <p>
     * This method reads a line of text from the input stream and returns it.
     * If an error occurs during the reception, it logs the error and returns {@code null}.
     *
     * @return the received message, or {@code null} if an error occurs
     * @throws IOException if an I/O error occurs while reading the message
     */
    public String receiveMessage() throws IOException {
        try {
            String message = input.readLine(); // Reads a line of text from the input stream
            logger.log(Level.DEBUG, "Received message: {}", message); // Logs the received message
            return message;
        } catch (SocketException e) {
            logger.log(Level.ERROR, "Unable to receive client message due to socket exception: ", e); // Logs socket exceptions
            return null;
        }
    }

    /**
     * Creates the input stream for receiving messages from the client socket.
     * <p>
     * This method creates and returns a BufferedReader for reading messages from the client socket.
     *
     * @return the input stream
     * @throws IOException if an I/O error occurs while creating the input stream
     */
    public BufferedReader createSocketInput() throws IOException {
        if (socket == null) {
            logger.log(Level.DEBUG, "Socket is null, returning null for input stream.");
            return null;
        }

        return new BufferedReader(new InputStreamReader(socket.getInputStream())); // Creates and returns a BufferedReader for the socket input
    }

    /**
     * Creates the output stream for sending messages to the client socket.
     * <p>
     * This method creates and returns a PrintWriter for sending messages to the client socket.
     *
     * @return the output stream
     * @throws IOException if an I/O error occurs while creating the output stream
     */
    public PrintWriter createSocketOutput() throws IOException {
        if (socket == null) {
            logger.log(Level.DEBUG, "Socket is null, returning null for output stream.");
            return null;
        }

        return new PrintWriter(socket.getOutputStream(), true); // Creates and returns a PrintWriter for the socket output
    }

    /**
     * Broadcasts a message to all clients in the collection.
     * <p>
     * This method iterates over all clients and sends the specified message to each client.
     *
     * @param clients the collection of clients to broadcast the message to
     * @param message the message to broadcast
     * @param <W> the type of worker representing clients
     */
    public static <W extends Worker> void broadcast(Collection<W> clients, String message) {
        for (Worker client : clients) {
            client.getMessagesManager().sendMessage(message); // Sends the message to each client
        }
        logger.log(Level.DEBUG, "Broadcasted message to {} clients", clients.size()); // Logs the broadcast action
    }

    /**
     * Broadcasts a message to all clients in the collection except one.
     * <p>
     * This method iterates over all clients, excluding the specified client, and sends the message to the others.
     *
     * @param clients the collection of clients to broadcast the message to
     * @param less the client to exclude from the broadcast
     * @param message the message to broadcast
     * @param <W> the type of worker representing clients
     */
    public static <W extends Worker> void broadcastLess(Collection<W> clients, W less, String message) {
        for (Worker client : clients) {
            if (!client.equals(less)) {
                client.getMessagesManager().sendMessage(message); // Sends the message to each client except the excluded one
            }
        }
        logger.log(Level.DEBUG, "Broadcasted message to {} clients, excluding one.", clients.size() - 1); // Logs the broadcast action excluding one client
    }
}
