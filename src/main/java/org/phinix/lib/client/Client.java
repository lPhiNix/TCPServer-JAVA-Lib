package org.phinix.lib.client;

public interface Client {
    void connect(String serverAddress, int serverPort);
    void connect();
    void start();
    void disconnect();
}
