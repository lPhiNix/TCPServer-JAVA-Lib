package org.phinix.lib.server.session;

import org.phinix.lib.server.core.Manageable;

/**
 * Interface representing a session that can be managed.
 *
 * @see Manageable
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