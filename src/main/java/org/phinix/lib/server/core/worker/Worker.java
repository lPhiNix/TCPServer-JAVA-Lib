package org.phinix.lib.server.core.worker;

import org.phinix.lib.common.model.room.RoomImpl;
import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.service.AbstractServiceRegister;
import org.phinix.lib.common.util.MessagesManager;

import java.io.IOException;

/**
 * Represents a worker that handles client communication.
 * This interface extends {@link Runnable} and {@link Manageable}.
 *
 * @see Runnable
 * @see Manageable
 */
public interface Worker extends Runnable, Manageable {

    /**
     * Listens for a message from the client.
     *
     * @param line the message received from the client
     * @throws IOException if an I/O error occurs
     */
    void listen(String line) throws IOException;

    /**
     * Closes the connection to the client.
     */
    void closeConnection();

    /**
     * Returns the messages manager for client communication.
     *
     * @return the messages manager
     */
    MessagesManager getMessagesManager();

    /**
     * Returns the service register.
     *
     * @return the service register
     */
    AbstractServiceRegister getServiceRegister();

    /**
     * Returns the server context.
     *
     * @return the server context
     */
    Context getServerContext();

    /**
     * Returns the current room the worker is in.
     *
     * @return the current room
     */
    RoomImpl getCurrentRoom();

    /**
     * Sets the current room the worker is in.
     *
     * @param roomImpl the current room
     */
    void setCurrentRoom(RoomImpl roomImpl);

    /**
     * Returns the client's address.
     *
     * @return the client's address
     */
    String getClientAddress();
}