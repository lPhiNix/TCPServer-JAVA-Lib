package org.phinix.example.server.core;

import org.phinix.lib.server.context.Context;

public class MathGameServerContext extends Context {

    private final MathGameServer server;

    public MathGameServerContext(MathGameServer server) {
        super(server);
        this.server = server;
    }

    public void nose() {
        server.nose();
    }
}
