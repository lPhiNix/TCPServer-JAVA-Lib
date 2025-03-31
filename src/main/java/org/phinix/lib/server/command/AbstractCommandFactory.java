package org.phinix.lib.server.command;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.server.core.worker.Worker;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCommandFactory<W extends Worker> {
    private static final Logger logger = LogManager.getLogger();

    protected final Map<String, Class<? extends Command<W>>> commands;

    public AbstractCommandFactory() {
        logger.log(Level.DEBUG, "Initializing");

        this.commands = new HashMap<>();

        int amountRegisteredCommands = initCommands();
        logger.log(Level.INFO, "{} registered commands in server", amountRegisteredCommands);
    }

    protected abstract int initCommands();

    protected void registerCommand(String commandName, Class<? extends Command<W>> commandClass) {
        commands.put(commandName, commandClass);
    }

    public Class<? extends Command<W>> getCommandType(String commandName) {
        return commands.get(commandName);
    }

    public Command<W> createCommand(String commandName) throws Exception {
        Class<? extends Command<W>> commandClass = getCommandType(commandName);
        if (commandClass != null) {
            return commandClass.getConstructor().newInstance();
        }

        return null;
    }

    protected int getAmountRegisteredCommands() {
        return commands.size();
    }
}