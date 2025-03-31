package org.phinix.example.server.command;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.example.server.command.commands.HelpCommand;
import org.phinix.example.server.thread.ClientHandler;
import org.phinix.lib.server.command.AbstractCommandFactory;
import org.phinix.lib.server.command.Command;

public class CommandFactory extends AbstractCommandFactory<ClientHandler> {
    private static final Logger logger = LogManager.getLogger();

    private static final String COMMAND_SYMBOL = "/";

    @Override
    protected int initCommands() {
        registerCommand(HelpCommand.getCommandName(), HelpCommand.class);

        return getAmountRegisteredCommands();
    }

    @Override
    public Command<ClientHandler> createCommand(String commandName) throws Exception {
        if (commandName.startsWith(COMMAND_SYMBOL)) {
            commandName = commandName.substring(COMMAND_SYMBOL.length());
        } else {
            logger.log(Level.WARN, "Name provided isn't a command name: {}", commandName);
            return null;
        }

        return super.createCommand(commandName);
    }

    public static String getCommandSymbol() {
        return COMMAND_SYMBOL;
    }
}