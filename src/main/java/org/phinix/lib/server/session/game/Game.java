package org.phinix.lib.server.session.game;

import org.phinix.lib.server.session.Session;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.common.model.room.Room;

/**
 * {@code Game} interface representing a game session.
 * This session type is for more specific implementation of a session
 * more oriented to implements game in a {@link Room}
 *
 * @see Session
 * @see Manageable
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