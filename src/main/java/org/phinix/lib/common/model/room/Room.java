package org.phinix.lib.common.model.room;

import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.session.Session;

/**
 * {@code Room} interface representing a room that can contain clients and manage a session.
 * <p>
 * This interface defines the basic operations needed to manage clients within a room and track the session associated with the room.
 * It allows adding/removing clients, checking if the room is empty, and retrieving information about the room's name, client count, and session.
 *
 * @see Worker
 * @see Session
 */
public interface Room {

    /**
     * Adds a client to the room.
     * <p>
     * This method adds a {@link Worker} client to the room, allowing the client to participate in the session associated with the room.
     *
     * @param client the client to be added
     */
    void addClient(Worker client);

    /**
     * Removes a client from the room.
     * <p>
     * This method removes a {@link Worker} client from the room, and it also accepts a flag to indicate whether the session has ended.
     * If the session is over, the client will be removed accordingly.
     *
     * @param client the client to be removed
     * @param sessionIsEnded flag indicating whether the session has ended
     */
    void removeClient(Worker client, boolean sessionIsEnded);

    /**
     * Checks if the room is empty.
     * <p>
     * This method checks whether the room contains any clients. It returns {@code true} if the room is empty, and {@code false} otherwise.
     *
     * @return {@code true} if the room is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Returns the name of the room.
     * <p>
     * This method retrieves the name of the room, which is typically used to identify the room in a user interface or backend system.
     *
     * @return the name of the room
     */
    String getRoomName();

    /**
     * Returns the number of clients in the room.
     * <p>
     * This method returns the count of clients currently in the room. This can be used to monitor room occupancy.
     *
     * @return the number of clients in the room
     */
    String getClientsAmount();

    /**
     * Returns the session associated with the room.
     * <p>
     * This method retrieves the session object that is associated with this room. The session typically tracks the state and activities of the room.
     *
     * @return the session associated with the room
     */
    Session getSession();
}
