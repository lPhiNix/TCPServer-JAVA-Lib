package org.phinix.example.server.command.commands;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.example.common.model.Player;
import org.phinix.example.common.util.StringFormat;
import org.phinix.example.server.command.CommandFactory;
import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.example.server.service.services.PlayerManager;
import org.phinix.lib.server.command.Command;

public class LoginCommand implements Command<ClientHandler> {
    private static final Logger logger = LogManager.getLogger();
    private static final String COMMAND_NAME = "login";
    private static final int parametersAmount = 2;

    @Override
    public void execute(String[] args, ClientHandler client) {
        if (args.length != parametersAmount) {
            client.getMessagesManager().sendMessage("Help: " + CommandFactory.getCommandSymbol() +
                    COMMAND_NAME + " <username> <password>");
            return;
        }

        logger.log(Level.DEBUG, "Executing command {} by {}", new Object[]{COMMAND_NAME, client.getClientAddress()});

        String username = args[0];
        String password = args[1];

        PlayerManager manager = client.getServiceRegister().getService(PlayerManager.class);
        Player user = manager.authenticate(username, password);

        if (user != null) {
            client.setCurrentUser(user);
            client.getMessagesManager().sendMessage("User login in successfully as " + username + ":" + StringFormat.hidePassword(password));
            logger.log(Level.INFO, "Client: {} has logged in as {}", new Object[]{client.getSocket().getInetAddress().getHostAddress(), client.getCurrentUser().getUsername()});
        } else {
            client.getMessagesManager().sendMessage("The input user does not exist!");
            logger.log(Level.WARN, "Client: {}'s try to login as {} has failed!", new Object[]{client.getSocket().getInetAddress().getHostAddress(), client.getCurrentUser().getUsername()});
        }
    }

    public static String getCommandName() {
        return COMMAND_NAME;
    }
}