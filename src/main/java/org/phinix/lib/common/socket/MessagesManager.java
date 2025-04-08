package org.phinix.lib.common.socket;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.worker.Worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;

/**
 * Manages messages sent to and received from a client socket.
 */
public class MessagesManager {
    private static final Logger logger = LogManager.getLogger();

    private final Socket socket; // Client socket
    private final BufferedReader input; // Input stream for receiving messages
    private final PrintWriter output; // Output stream for sending messages

    /**
     * Constructs a new MessagesManager with the specified client socket.
     *
     * @param socket the client socket
     * @throws IOException if an I/O error occurs
     */
    public MessagesManager(Socket socket) throws IOException {
        logger.log(Level.DEBUG, "Initializing");

        this.socket = socket;

        this.input = createSocketInput();
        this.output = createSocketOutput();
    }

    /**
     * Sends a message to the client.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        output.println(message);
    }

    /**
     * Sends a message to the client and logs it.
     *
     * @param level the logging level
     * @param message the message to send
     * @param args the arguments for the message
     */
    public void sendMessageAndLog(Level level, String message, Object... args) {
        logger.log(level, message, args);
        output.println(message);
    }

    /**
     * Receives a message from the client.
     *
     * @return the received message, or {@code null} if an error occurs
     * @throws IOException if an I/O error occurs
     */
    public String receiveMessage() throws IOException {
        try {
            return input.readLine();
        } catch (SocketException e) {
            logger.log(Level.ERROR, "Unable to receive client message: ", e);
            return null;
        }
    }

    /**
     * Creates the input stream for receiving messages from the client socket.
     *
     * @return the input stream
     * @throws IOException if an I/O error occurs
     */
    public BufferedReader createSocketInput() throws IOException {
        if (socket == null) {
            return null;
        }

        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Creates the output stream for sending messages to the client socket.
     *
     * @return the output stream
     * @throws IOException if an I/O error occurs
     */
    public PrintWriter createSocketOutput() throws IOException {
        if (socket == null) {
            return null;
        }

        return new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Broadcasts a message to all clients in the collection.
     *
     * @param clients the collection of clients
     * @param message the message to broadcast
     * @param <W> the type of worker
     */
    public static <W extends Worker> void broadcast(Collection<W> clients, String message) {
        for (Worker client : clients) {
            client.getMessagesManager().sendMessage(message);
        }
    }

    /**
     * Broadcasts a message to all clients in the collection except one.
     *
     * @param clients the collection of clients
     * @param less the client to exclude from the broadcast
     * @param message the message to broadcast
     * @param <W> the type of worker
     */
    public static <W extends Worker> void broadcastLess(Collection<W> clients, W less, String message) {
        for (Worker client : clients) {
            if (!client.equals(less)) {
                client.getMessagesManager().sendMessage(message);
            }
        }
    }
}