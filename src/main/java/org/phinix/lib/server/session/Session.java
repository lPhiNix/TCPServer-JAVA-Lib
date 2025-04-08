package org.phinix.lib.server.session;

import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;
import org.phinix.lib.server.core.task.Task;
import org.phinix.lib.server.session.game.Game;
import org.phinix.lib.common.model.room.Room;
import org.phinix.lib.server.core.Server;
import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.common.model.room.RoomImpl;

/**
 * {@code Session} interface represent a session that can be managed.
 * This session is part of a {@link Room} in a {@link Server}.
 * A session in a server room is the purpose of that room. It can be start and
 * finish. When this session end, the session and the room end its live cycle.
 * <p>
 * {@code Manageable} interface permits this server run global async {@link Task}
 * using {@link AbstractTaskExecutor} instance.
 *
 * @see Manageable
 * @see Game
 * @see Server
 * @see AbstractServer
 * @see Room
 * @see RoomImpl
 */
public interface Session extends Manageable {
    /**
     * Starts the session.
     */
    void start();
    /**
     * Checks if the session has ended.
     *
     * @return {@code true} if the session has ended, {@code false} otherwise
     */
    boolean isEnd();
    /**
     * Sets the session end state.
     *
     * @param isEnd the session end state
     */
    void setIsEnd(boolean isEnd);
    /**
     * Ends the session.
     */
    void end();
}