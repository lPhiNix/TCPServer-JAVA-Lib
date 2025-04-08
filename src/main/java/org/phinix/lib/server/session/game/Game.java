package org.phinix.lib.server.session.game;

import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.server.session.Session;
import org.phinix.lib.server.core.worker.Worker;

/**
 * Interface representing a game session.
 *
 * @see Session
 */
public interface Game extends Session {

    /**
     * Returns the worker whose turn it is.
     *
     * @return the worker whose turn it is
     */
    Worker isTurn();

    /**
     * Checks if the game is over.
     */
    void checkGameOver();

    /**
     * Handles the disconnection of a client.
     *
     * @param client the client that disconnected
     */
    <W extends Worker> void handleDisconnect(W client);
}