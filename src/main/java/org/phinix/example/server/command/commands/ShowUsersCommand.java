package org.phinix.example.server.command.commands;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.example.server.command.CommandFactory;
import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.example.server.service.services.PlayerManager;
import org.phinix.lib.server.command.Command;

public class ShowUsersCommand implements Command<ClientHandler> {
    private static final Logger logger = LogManager.getLogger();
    private static final String COMMAND_NAME = "users";
    private static final String SHOW_USERS_IN_THIS_RUN_MODIFIER = "-a";
    private static final int maxParameters = 1;

    @Override
    public void execute(String[] args, ClientHandler client) {
        if (args.length > maxParameters) {
            client.getMessagesManager().sendMessage("Help: " + CommandFactory.getCommandSymbol() +
                    COMMAND_NAME + "[-a]");
            return;
        }

        logger.log(Level.DEBUG, "Executing command {} by {}", new Object[]{COMMAND_NAME, client.getClientAddress()});

        PlayerManager manager = client.getServiceRegister().getService(PlayerManager.class);

        client.getMessagesManager().sendMessage("Users: ");
        for (String username : manager.getRunningUsers()) {
            client.getMessagesManager().sendMessage(username);
        }
    }

    public static String getCommandName() {
        return COMMAND_NAME;
    }
}
