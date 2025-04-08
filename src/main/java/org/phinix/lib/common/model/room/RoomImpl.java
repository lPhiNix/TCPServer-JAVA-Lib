package org.phinix.lib.common.model.room;

import org.phinix.lib.common.socket.MessagesManager;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.session.Session;
import org.phinix.lib.server.session.game.Game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of the {@link Room} interface.
 * This class manages clients and sessions within a room.
 *
 * @see Room
 */
public class RoomImpl implements Room {
    protected int maxUsers; // Maximum number of users in the room

    protected String roomName; // Name of the room
    protected List<Worker> clients; // List of clients in the room
    protected Session session; // Session associated with the room

    /**
     * Constructs a new RoomImpl with the specified parameters.
     *
     * @param roomName the name of the room
     * @param owner the owner of the room
     * @param maxUsers the maximum number of users in the room
     */
    public RoomImpl(String roomName, Worker owner, int maxUsers) {
        this.roomName = roomName;
        this.maxUsers = maxUsers;
        this.clients = new CopyOnWriteArrayList<>();
        this.clients.add(owner);
        owner.setCurrentRoom(this);
    }

    /**
     * Adds a client to the room.
     *
     * @param client the client to be added
     */
    public synchronized void addClient(Worker client) {
        if (clients.size() >= maxUsers) {
            client.getMessagesManager().sendMessage("Room " + roomName + " is full! " + getClientsAmount());
            return;
        }

        client.getMessagesManager().sendMessage("You enter in the room " + roomName + " successfully!");
        clients.add(client);
        client.setCurrentRoom(this);

        MessagesManager.broadcast(clients, client.getClientAddress() + " has joined this room");

        if (clients.size() == maxUsers) {
            startSession();
        }
    }

    /**
     * Removes a client from the room.
     *
     * @param client the client to be removed
     * @param isSessionEnd flag indicating whether the session has ended
     */
    public synchronized void removeClient(Worker client, boolean isSessionEnd) {
        clients.remove(client);
        MessagesManager.broadcast(clients, client.getClientAddress() + " has left the room");

        client.setCurrentRoom(null);

        if (isSessionEnd) {
            return;
        }

        if (session != null) {
            if (session instanceof Game) {
                ((Game) session).handleDisconnect(client);
            }

            if (clients.isEmpty()) {
                session.setIsEnd(false);
            }
        }
    }

    /**
     * Starts the session in the room.
     */
    public void startSession() {
        // Implementation for starting a session
    }

    /**
     * Checks if the room is empty.
     *
     * @return {@code true} if the room is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return clients.isEmpty();
    }

    /**
     * Returns the number of clients in the room.
     *
     * @return the number of clients in the room
     */
    public synchronized String getClientsAmount() {
        return "(" + clients.size() + "/" + maxUsers + ")";
    }

    /**
     * Returns the name of the room.
     *
     * @return the name of the room
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Returns the session associated with the room.
     *
     * @return the session associated with the room
     */
    public Session getSession() {
        return session;
    }
}