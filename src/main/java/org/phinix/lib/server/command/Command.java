package org.phinix.lib.server.command;

import org.phinix.lib.server.core.worker.Worker;

/**
 * {@code Command} interface representing a command that can be executed by a {@link Worker}.
 * <p>
 * Use example:
 * <pre>{@code
 * public class ExampleCommand implements Command<MyWorker> {
 *     private static final String COMMAND_NAME = "example";
 *     private static final int parametersAmount = 0;
 *
 *     @Override
 *     public void execute(String[] args, MyWorker client) {
 *         if (args.length != parametersAmount) {
 *             client.getMessagesManager().sendMessage(
 *                  "Help: " + COMMAND_NAME
 *             );
 *             return;
 *         }
 *
 *         client.example();
 *     }
 *
 *     public static String getCommandName() {
 *         return COMMAND_NAME;
 *     }
 * }
 * }
 * @param <W> the type of worker that executes the command
 * @see Worker
 */
public interface Command<W extends Worker> {

    /**
     * Executes the command with the specified arguments and worker.
     *
     * @param args the command arguments
     * @param worker the worker executing the command
     */
    void execute(String[] args, W worker);
}