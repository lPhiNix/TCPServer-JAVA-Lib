package org.phinix.example.server.core;

import org.phinix.lib.server.context.Context;

public class TCPServerContext extends Context {

    private final TCPServer server;

    public TCPServerContext(TCPServer server) {
        super(server);
        this.server = server;
    }

    public void nose() {
        server.nose();
    }
}
