package org.phinix.lib.common.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
        return input.readLine();
    }

    public BufferedReader createSocketInput() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public PrintWriter createSocketOutput() throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }
}
