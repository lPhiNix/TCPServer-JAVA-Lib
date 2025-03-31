package org.phinix.lib.client;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.common.util.MessagesManager;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractClient implements Client {
    private static final Logger logger = LogManager.getLogger();

    protected String serverAddress = "localhost";
    protected int serverPort = 12345;

    protected MessagesManager messagesManager;
    protected Socket socket;
    protected boolean isConnected;

    @Override
    public void connect(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            messagesManager = new MessagesManager(socket);

            isConnected = true;

            this.serverAddress = serverAddress;
            this.serverPort = serverPort;

            logger.log(Level.DEBUG, "Connection established successfully!");
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error connecting server {}:{}. Exception: ", new Object[]{serverAddress, serverPort}, e);
        }
    }

    @Override
    public void connect() {
        try {
            socket = new Socket(serverAddress, serverPort);
            messagesManager = new MessagesManager(socket);

            isConnected = true;

            logger.log(Level.DEBUG, "Standard connection established successfully!");
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error connecting server {}:{}. Exception: ", new Object[]{serverAddress, serverPort}, e);
        }
    }

    @Override
    public void start() {
        if (socket == null) {
            return;
        }

        Thread listener = new Thread(this::handleReceivedMessage);
        listener.start();

        handleInputUser();
    }

    protected abstract void handleReceivedMessage();

    protected abstract void handleInputUser();

    @Override
    public void disconnect() {
        isConnected = false;
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error disconnecting server {}:{}. Exception: ", new Object[]{serverAddress, serverPort}, e);
        }
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
