package org.phinix.lib.server.command;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.server.core.worker.Worker;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code AbstractCommandFactory} abstract class for managing the registration and creation of commands.
 * <p>
 * The main functionality includes registering commands with {@link #registerCommand(String, Class)} and creating command instances
 * using {@link #createCommand(String)}. Commands are stored in a map where the key is the command name (a {@code String})
 * and the value is the class type of the command.
 * <p>
 * Example of a generic implementation:
 * <pre>{@code
 * public class MyCommandFactory extends AbstractCommandFactory<MyWorker> {
 *     @Override
 *     protected int initCommands() {
 *         // Create and register a sample command
 *         registerCommand("Command1.getCommandName()", Command1.class);
 *         registerCommand("Command2.getCommandName()", Command2.class);
 *         registerCommand("Command3.getCommandName()", Command3.class);
 *
 *         // Return the number of registered commands
 *         return getAmountRegisteredCommands();
 *     }
 * }
 * }
 *
 * @param <W> the type of worker that executes the commands
 * @see Command
 * @see Worker
 */

public abstract class AbstractCommandFactory<W extends Worker> {
    private static final Logger logger = LogManager.getLogger();

    protected final Map<String, Class<? extends Command<W>>> commands; // Map of command names to command classes

    /**
     * Constructs an AbstractCommandFactory and initializes the commands.
     */
    public AbstractCommandFactory() {
        logger.log(Level.DEBUG, "Initializing");

        this.commands = new HashMap<>();

        int amountRegisteredCommands = initCommands();
        logger.log(Level.INFO, "{} registered commands in server", amountRegisteredCommands);
    }

    /**
     * Initializes the commands.
     *
     * @return the number of registered commands
     */
    protected abstract int initCommands();

    /**
     * Registers a command with the specified name and class.
     *
     * @param commandName the name of the command
     * @param commandClass the class of the command
     */
    protected void registerCommand(String commandName, Class<? extends Command<W>> commandClass) {
        commands.put(commandName, commandClass);
        logger.log(Level.DEBUG, "Command registered: {}", commandName);
    }

    /**
     * Returns the class of the command with the specified name.
     *
     * @param commandName the name of the command
     * @return the class of the command, or {@code null} if not found
     */
    public Class<? extends Command<W>> getCommandType(String commandName) {
        return commands.get(commandName);
    }

    /**
     * Creates a new instance of the command with the specified name.
     *
     * @param commandName the name of the command
     * @return the created command, or {@code null} if not found
     * @throws Exception if an error occurs while creating the command
     */
    public Command<W> createCommand(String commandName) throws Exception {
        logger.log(Level.DEBUG, "Building {}...", commandName);
        Class<? extends Command<W>> commandClass = getCommandType(commandName);
        if (commandClass != null) {
            return commandClass.getConstructor().newInstance();
        }

        return null;
    }

    /**
     * Returns the number of registered commands.
     *
     * @return the number of registered commands
     */
    protected int getAmountRegisteredCommands() {
        return commands.size();
    }
}