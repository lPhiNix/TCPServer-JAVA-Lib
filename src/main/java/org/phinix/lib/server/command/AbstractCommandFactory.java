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
     * Logs the initialization of the factory and the number of registered commands.
     */
    public AbstractCommandFactory() {
        logger.log(Level.DEBUG, "Initializing AbstractCommandFactory");

        this.commands = new HashMap<>();

        int amountRegisteredCommands = initCommands(); // Calls the abstract method to initialize the commands
        logger.log(Level.INFO, "{} registered commands in server", amountRegisteredCommands); // Logs the total number of registered commands
    }

    /**
     * Initializes the commands.
     * This method must be implemented by subclasses to register their specific commands.
     *
     * @return the number of registered commands
     */
    protected abstract int initCommands();

    /**
     * Registers a command with the specified name and class.
     * Adds the command name and its associated class to the commands map.
     *
     * @param commandName the name of the command
     * @param commandClass the class of the command
     */
    protected void registerCommand(String commandName, Class<? extends Command<W>> commandClass) {
        commands.put(commandName, commandClass); // Adds the command to the map
        logger.log(Level.DEBUG, "Command registered: {}", commandName); // Logs the registration of the command
    }

    /**
     * Returns the class of the command with the specified name.
     * This method is used to retrieve the command's class type from the map.
     *
     * @param commandName the name of the command
     * @return the class of the command, or {@code null} if not found
     */
    public Class<? extends Command<W>> getCommandType(String commandName) {
        return commands.get(commandName); // Retrieves the command class from the map
    }

    /**
     * Creates a new instance of the command with the specified name.
     * This method uses reflection to instantiate the command class.
     *
     * @param commandName the name of the command
     * @return the created command, or {@code null} if not found
     * @throws Exception if an error occurs while creating the command
     */
    public Command<W> createCommand(String commandName) throws Exception {
        logger.log(Level.DEBUG, "Building command: {}", commandName); // Logs the creation attempt
        Class<? extends Command<W>> commandClass = getCommandType(commandName); // Fetches the command class

        if (commandClass != null) {
            return commandClass.getConstructor().newInstance(); // Creates and returns a new instance of the command
        }

        return null; // Returns null if the command class was not found
    }

    /**
     * Returns the number of registered commands.
     * This method provides the total count of commands that have been registered.
     *
     * @return the number of registered commands
     */
    protected int getAmountRegisteredCommands() {
        return commands.size(); // Returns the size of the map, i.e., the number of registered commands
    }
}
