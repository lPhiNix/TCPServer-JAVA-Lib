package org.phinix.example.main;

import org.phinix.example.server.core.MathGameServer;

public class ServerMain {
    public static void main(String[] args) {
        MathGameServer server = new MathGameServer(12345, 20);
        server.start();
    }
}