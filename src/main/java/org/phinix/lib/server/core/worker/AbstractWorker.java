package org.phinix.lib.server.core.worker;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.example.server.service.task.TaskExecutor;
import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.service.AbstractServiceRegister;
import org.phinix.lib.common.util.MessagesManager;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractWorker implements Worker {
    private static final Logger logger = LogManager.getLogger();

    protected final Socket socket;
    protected final MessagesManager messagesManager;
    protected final AbstractServiceRegister serviceRegister;
    protected final TaskExecutor asyncTasks;
    protected final Context serverContext;
    protected boolean isRunning;

    public AbstractWorker(Socket socket, Context serverContext, AbstractServiceRegister serviceRegister) throws IOException {
        logger.log(Level.DEBUG, "Initializing");

        this.socket = socket;
        this.messagesManager = new MessagesManager(socket);
        this.serviceRegister = serviceRegister;
        this.serverContext = serverContext;

        asyncTasks = new TaskExecutor();

        isRunning = true;
    }

    @Override
    public void run() {
        try {
            logger.log(Level.INFO, "Listening client {}", socket.getInetAddress());

            asyncTasks.start();

            while (isRunning) {
                if (!listenLoop()) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error connecting to client: ", e);
        } catch (Exception e) {
            logger.log(Level.FATAL, "Error managing client input: ", e);
        } finally {
            logger.log(Level.INFO, "Stopping worker {}...", socket.getInetAddress());
            closeConnection();
        }
    }

    protected boolean listenLoop() throws IOException {
        String line;
        if ((line = messagesManager.receiveMessage()) != null) {
            listen(line);
            return true;
        }

        return false;
    }

    @Override
    public abstract void listen(String line) throws IOException;

    @Override
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    @Override
    public AbstractServiceRegister getServiceRegister() {
        return serviceRegister;
    }

    @Override
    public void closeConnection() {
        isRunning = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logger.log(Level.INFO, "Worker stopped.");
            }
            asyncTasks.stop();
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error closing worker: ", e);
        }
    }
}