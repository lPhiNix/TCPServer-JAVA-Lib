package org.phinix.lib.server.command;

import org.phinix.lib.server.core.worker.Worker;

public interface Command<W extends Worker>  {
    void execute(String[] args, W worker);
}
