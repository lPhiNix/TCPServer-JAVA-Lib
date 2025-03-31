package org.phinix.example.server;

import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.context.Context;

public class TCPServerContext extends Context {
    public TCPServerContext(AbstractServer server) {
        super(server);
    }
}
