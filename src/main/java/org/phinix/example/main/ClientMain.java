package org.phinix.example.main;

import org.phinix.example.client.MathGameClient;

public class ClientMain {
    public static void main(String[] args) {
        MathGameClient client = new MathGameClient();
        client.connect("localhost", 12345);
        client.start();
    }
}