package org.phinix.example.server.command.commands;

import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.server.command.Command;

public class HelpCommand implements Command<ClientHandler> {
    private static final String COMMAND_NAME = "help";

    @Override
    public void execute(String[] args, ClientHandler worker) {
        worker.getMessagesManager().sendMessage("no.");
    }

    public static String getCommandName() {
        return COMMAND_NAME;
    }
}