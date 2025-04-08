package org.phinix.example.server.command.commands;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.example.common.game.MathGameRoom;
import org.phinix.example.server.command.CommandFactory;
import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.server.command.Command;

public class ResolveCommand implements Command<ClientHandler> {
    private static final Logger logger = LogManager.getLogger();
    private static final String COMMAND_NAME = "resolve";
    private static final int parametersAmount = 1;

    @Override
    public void execute(String[] args, ClientHandler client) {
        if (args.length != parametersAmount) {
            client.getMessagesManager().sendMessage("Help: " + CommandFactory.getCommandSymbol() +
                    COMMAND_NAME + " <mathExpression>");
            return;
        }

        logger.log(Level.DEBUG, "Executing command {} by {}", new Object[]{COMMAND_NAME, client.getClientAddress()});

        MathGameRoom room = client.getCurrentRoom();

        if (room == null) {
            client.getMessagesManager().sendMessage("You are not in a room right now");
            return;
        }

        room.tryGuessRoot(args[0], client);
    }

    public static String getCommandName() {
        return COMMAND_NAME;
    }
}
