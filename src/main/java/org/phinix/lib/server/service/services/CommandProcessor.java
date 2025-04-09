package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.server.command.AbstractCommandFactory;
import org.phinix.lib.server.command.Command;
import org.phinix.lib.server.service.Service;
import org.phinix.lib.server.core.worker.Worker;

import java.util.Arrays;

/**
 * {@code CommandProcessor} is a service that processes commands sent by clients.
 * It uses an {@link AbstractCommandFactory} to create and execute commands based on
 * the input provided by clients.
 * <p>
 * This class validates the command format, creates appropriate command instances,
 * and executes them with the provided parameters.
 *
 * @param <W> the type of worker associated with the commands
 * @see AbstractCommandFactory
 * @see Command
 * @see Worker
 */
public class CommandProcessor<W extends Worker> implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final AbstractCommandFactory<W> commandFactory;

    /**
     * Constructs a {@code CommandProcessor} with the specified command factory.
     *
     * @param commandFactory the factory used to create command instances
     */
    public CommandProcessor(AbstractCommandFactory<W> commandFactory) {
        logger.log(Level.DEBUG, "Initializing CommandProcessor");

        this.commandFactory = commandFactory;
    }

    /**
     * Processes a command line received from a client.
     * Validates the command format, builds the command, and executes it.
     *
     * @param line the command line received from the client
     * @param worker the worker executing the command
     * @return {@code true} if the command was successfully executed, {@code false} otherwise
     */
    public boolean processCommand(String line, W worker) {
        logger.log(Level.DEBUG, "Processing command line: {}", line);

        try {
            // Split the command line into individual components (command name and arguments).
            String[] formatLine = formatLine(line);
            if (!isValidFormatLine(formatLine)) {
                // Log and notify if the command format is invalid
                worker.getMessagesManager().sendMessageAndLog(Level.WARN,
                        "Command line has invalid format: {}", line);
                return false;
            }

            // Extract the command name and its parameters.
            String commandName = formatLine[0];
            String[] args = getCommandParameters(formatLine);

            logger.log(Level.DEBUG, "Attempting to build command: {}", commandName);
            return buildAndExecuteCommand(commandName, args, worker);
        } catch (Exception e) {
            // Handle any exceptions that occur during command processing.
            logger.log(Level.ERROR, "Error processing command: {}. Exception: ", line, e);
            return false;
        }
    }

    /**
     * Splits a command line into its components using whitespace as a delimiter.
     *
     * @param line the command line to split
     * @return an array of command components
     */
    private String[] formatLine(String line) {
        return line.split("\\s+");
    }

    /**
     * Validates the format of the command line.
     *
     * @param formatLine the components of the command line
     * @return {@code true} if the format is valid, {@code false} otherwise
     */
    private boolean isValidFormatLine(String[] formatLine) {
        if (formatLine.length == 0) {
            // Log and notify if the command line is empty or unknown
            logger.log(Level.WARN, "Command line is unknown or empty");
            return false;
        }
        return true;
    }

    /**
     * Builds a command instance using the factory and executes it.
     *
     * @param commandName the name of the command
     * @param commandParameters the parameters for the command
     * @param worker the worker executing the command
     * @return {@code true} if the command was executed successfully, {@code false} otherwise
     * @throws Exception if an error occurs during command creation or execution
     */
    private boolean buildAndExecuteCommand(String commandName, String[] commandParameters, W worker)
            throws Exception {
        Command<W> userCommand = commandFactory.createCommand(commandName);
        if (userCommand != null) {
            // Log successful execution of the command
            logger.log(Level.INFO, "Command executed successfully: {}",
                    userCommand.getClass().getSimpleName());
            userCommand.execute(commandParameters, worker);
            return true;
        }

        // Log if the command could not be built or was invalid
        logger.log(Level.DEBUG, "Failed to build or execute command: {}", commandName);
        return false;
    }

    /**
     * Extracts command parameters from the command line components.
     *
     * @param array the command line components
     * @return an array of command parameters
     */
    private static String[] getCommandParameters(String[] array) {
        if (array.length <= 1) {
            // Return an empty array if no parameters are provided
            return new String[0];
        }
        return Arrays.copyOfRange(array, 1, array.length);
    }
}
