package org.phinix.lib.common.util;

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

public class MessagesManager {
    private static final Logger logger = LogManager.getLogger();

    private final Socket socket;
    private final BufferedReader input;
    private final PrintWriter output;

    public MessagesManager(Socket socket) throws IOException {
        logger.log(Level.DEBUG, "Initializing");

        this.socket = socket;

        this.input = createSocketInput();
        this.output = createSocketOutput();

        if (input == null || output == null) {
            logger.log(Level.FATAL, "Error connecting client-server I/O");
            throw new IOException();
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public void sendMessageAndLog(Level level, String message, Object... args) {
        logger.log(level, message, args);
        output.println(message);
    }

    public String receiveMessage() throws IOException {
        try {
            return input.readLine();
        } catch (SocketException e) {
            logger.log(Level.ERROR, "Don't be able to receive client message: ", e);
            return null;
        }
    }

    public BufferedReader createSocketInput() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public PrintWriter createSocketOutput() throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    public static void broadcast(Collection<Worker> clients, String message) {
        for (Worker client : clients) {
            client.getMessagesManager().sendMessage(message);
        }
    }
}
