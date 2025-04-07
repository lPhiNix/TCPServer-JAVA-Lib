package org.phinix.lib.common.model.room;

import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.session.Session;

/**
 * Interface representing a room that can contain clients and manage a session.
 *
 * @see Worker
 * @see Session
 */
public interface Room {

    /**
     * Adds a client to the room.
     *
     * @param client the client to be added
     */
    void addClient(Worker client);

    /**
     * Removes a client from the room.
     *
     * @param client the client to be removed
     * @param sessionIsEnded flag indicating whether the session has ended
     */
    void removeClient(Worker client, boolean sessionIsEnded);

    /**
     * Checks if the room is empty.
     *
     * @return {@code true} if the room is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Returns the name of the room.
     *
     * @return the name of the room
     */
    String getRoomName();

    /**
     * Returns the number of clients in the room.
     *
     * @return the number of clients in the room
     */
    String getClientsAmount();

    /**
     * Returns the session associated with the room.
     *
     * @return the session associated with the room
     */
    Session getSession();
}