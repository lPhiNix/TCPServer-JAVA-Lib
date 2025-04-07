package org.phinix.lib.server.command;

import org.phinix.lib.server.core.worker.Worker;

/**
 * Interface representing a command that can be executed by a worker.
 *
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