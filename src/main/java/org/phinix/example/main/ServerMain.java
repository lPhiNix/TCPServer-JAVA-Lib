package org.phinix.example.main;

import org.phinix.example.server.core.TCPServer;

public class ServerMain {
    public static void main(String[] args) {
        TCPServer server = new TCPServer(12345, 20);
        server.start();
    }
}