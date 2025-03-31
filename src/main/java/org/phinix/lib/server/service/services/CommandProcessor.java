package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.server.command.AbstractCommandFactory;
import org.phinix.lib.server.command.Command;
import org.phinix.lib.server.service.Service;
import org.phinix.lib.server.core.worker.Worker;

import java.util.Arrays;

public class CommandProcessor<W extends Worker> implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final AbstractCommandFactory<W> commandFactory;

    public CommandProcessor(AbstractCommandFactory<W> commandFactory) {
        logger.log(Level.DEBUG, "Initializing");

        this.commandFactory = commandFactory;
    }

    public boolean processCommand(String line, W worker) {
        logger.log(Level.INFO, "Processing command line: {}", line);

        try {
            String[] formatLine = formatLine(line);
            if (!isValidFormatLine(formatLine)) {
                worker.getMessagesManager().sendMessageAndLog(Level.WARN, "Command line hasn't valid format: {}", line);
                return false;
            }

            String commandName = formatLine[0];
            String[] args = getCommandParameters(formatLine);

            logger.log(Level.DEBUG, "Trying to build command: {}", commandName);
            return buildAndExecuteCommand(commandName, args, worker);
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error processing command: {}. Exception: ", line, e);
            return true;
        }
    }

    private String[] formatLine(String line) {
        return line.split("\\s+");
    }

    private boolean isValidFormatLine(String[] formatLine) {
        if (formatLine.length == 0) {
            logger.log(Level.WARN, "Command line is unknown");
            return false;
        }

        return true;
    }

    private boolean buildAndExecuteCommand(String commandName, String[] commandParameters, W worker) throws Exception {
        Command<W> userCommand = commandFactory.createCommand(commandName);
        if (userCommand != null) {
            logger.log(Level.DEBUG, "Command build successfully!: {}", userCommand.getClass().getSimpleName());
            userCommand.execute(commandParameters, worker);
            return true;
        }

        return false;
    }

    private static String[] getCommandParameters(String[] array) {
        if (array.length <= 1) {
            return new String[0];
        }
        return Arrays.copyOfRange(array, 1, array.length);
    }
}