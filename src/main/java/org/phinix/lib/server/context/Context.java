package org.phinix.lib.server.context;

import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.worker.Worker;

import java.util.List;

public class Context {
    protected final AbstractServer server;

    public Context(AbstractServer server) {
        this.server = server;
    }

    public List<Worker> getConnectedClients() {
        return server.getConnectedClients();
    }
}
