package org.phinix.example.main;

import org.phinix.example.client.TCPClient;

public class ClientMain {
    public static void main(String[] args) {
        TCPClient client = new TCPClient();
        client.connect("localhost", 12345);
        client.start();
    }
}