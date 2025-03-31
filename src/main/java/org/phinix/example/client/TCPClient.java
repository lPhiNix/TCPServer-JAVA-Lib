package org.phinix.example.client;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.client.AbstractClient;

import java.io.IOException;
import java.util.Scanner;

public class TCPClient extends AbstractClient {
    private static final Logger logger = LogManager.getLogger();

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    @Override
    public void start() {
        if (socket == null) {
            return;
        }

        printWelcomeMessage();
        super.start();
    }

    @Override
    protected void handleReceivedMessage() {
        try {
            String receivedMessage;
            while (isConnected && (receivedMessage = messagesManager.receiveMessage()) != null) {
                printServerMessage(receivedMessage);
            }
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error handling received messages from server {}:{}. Exception: ", new Object[]{serverAddress, serverPort}, e);
        }
    }

    @Override
    protected void handleInputUser() {
        Scanner scanner = new Scanner(System.in);
        String commandLine;

        try {
            while (isConnected) {
                Thread.sleep(10);
                System.out.print(CYAN + "📝 > " + RESET);
                commandLine = scanner.nextLine().trim();

                messagesManager.sendMessage(commandLine);
            }
        } catch (InterruptedException e) {
            logger.log(Level.FATAL, "Error handling client input messages to server {}:{}. Exception: ", new Object[]{serverAddress, serverPort}, e);
        }
    }

    private void printWelcomeMessage() {
        System.out.println(GREEN + "🎰 Welcome!." + RESET);
        System.out.println(CYAN + "📝 Use /help to get more information." + RESET);
    }

    private void printServerMessage(String message) {
        System.out.println("\n" + YELLOW + "📢 > " + message + RESET);
    }
}
