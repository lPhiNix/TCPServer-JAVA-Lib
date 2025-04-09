package org.phinix.lib.common.model.room;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.common.socket.MessagesManager;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.session.Session;
import org.phinix.lib.server.session.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of the {@link Room} interface.
 * This class manages clients and sessions within a room.
 * <p>
 * The room has a maximum number of users, and it manages the clients within it, sending messages and handling the session logic.
 * When the room reaches the maximum number of clients, a session can start automatically.
 *
 * @see Room
 */
public class RoomImpl implements Room {

    private static final Logger logger = LogManager.getLogger();

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
        this.clients = new CopyOnWriteArrayList<>(); // Initialize the client list as a thread-safe collection
        this.clients.add(owner); // Add the owner to the room
        owner.setCurrentRoom(this); // Set the current room for the owner
        logger.log(Level.DEBUG, "Room '{}' created with owner: {}", roomName, owner.getClientAddress());
    }

    /**
     * Adds a client to the room.
     *
     * @param client the client to be added
     */
    public synchronized void addClient(Worker client) {
        if (clients.size() >= maxUsers) { // Check if the room is full
            // Notify the client that the room is full and include the current client count
            client.getMessagesManager().sendMessage("Room " + roomName + " is full! " + getClientsAmount());
            logger.log(Level.DEBUG, "Room '{}' is full, client '{}' cannot join.", roomName, client.getClientAddress());
            return;
        }

        // Notify the client that they have successfully entered the room
        client.getMessagesManager().sendMessage("You entered the room " + roomName + " successfully!");
        clients.add(client); // Add the client to the room's client list
        client.setCurrentRoom(this); // Set the current room for the new client

        // Broadcast the new client's arrival to all other clients in the room
        MessagesManager.broadcast(clients, client.getClientAddress() + " has joined this room");

        // If the room is full, start the session
        if (clients.size() == maxUsers) {
            startSession();
            logger.log(Level.DEBUG, "Room '{}' is full. Session is starting.", roomName);
        }
    }

    /**
     * Removes a client from the room.
     *
     * @param client the client to be removed
     * @param isSessionEnd flag indicating whether the session has ended
     */
    public synchronized void removeClient(Worker client, boolean isSessionEnd) {
        clients.remove(client); // Remove the client from the room's list
        MessagesManager.broadcast(clients, client.getClientAddress() + " has left the room");

        client.setCurrentRoom(null); // Set the current room for the client to null

        if (isSessionEnd) { // If the session has ended, return early
            logger.log(Level.DEBUG, "Session has ended, client '{}' removed.", client.getClientAddress());
            return;
        }

        if (session != null) {
            // If the session is a Game instance, handle the client's disconnect
            if (session instanceof Game) {
                ((Game) session).handleDisconnect(client);
                logger.log(Level.DEBUG, "Client '{}' disconnected from the game session.", client.getClientAddress());
            }

            // If no clients remain, mark the session as not ended
            if (clients.isEmpty()) {
                session.setIsEnd(false);
                logger.log(Level.DEBUG, "No clients left in room '{}', session status updated.", roomName);
            }
        }
    }

    /**
     * Casts the list of clients to a specific worker type.
     *
     * @param clients the list of clients
     * @param workerType the target type to cast the clients to
     * @param <W> the type of worker to cast to
     * @return a list of clients casted to the specified worker type
     */
    protected <W extends Worker> List<W> castClientsList(List<Worker> clients, Class<W> workerType) {
        List<W> castedClients = new ArrayList<>();
        for (Worker worker : clients) {
            if (workerType.isInstance(worker)) { // Check if the client is an instance of the desired worker type
                castedClients.add(workerType.cast(worker)); // Add the casted client to the list
            }
        }
        logger.log(Level.DEBUG, "Cast {} clients to type '{}'.", castedClients.size(), workerType.getName());
        return castedClients;
    }

    /**
     * Starts the session in the room.
     * <p>
     * This method initiates the session logic for the room. It is triggered when the room reaches the maximum number of clients.
     */
    public void startSession() {
        // Implementation for starting a session goes here
        logger.log(Level.DEBUG, "Session started in room '{}'.", roomName);
    }

    /**
     * Checks if the room is empty.
     *
     * @return {@code true} if the room is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return clients.isEmpty(); // Return whether the client list is empty
    }

    /**
     * Returns the number of clients in the room.
     *
     * @return the number of clients in the room as a string in the format (current/maximum)
     */
    public synchronized String getClientsAmount() {
        return "(" + clients.size() + "/" + maxUsers + ")"; // Return the number of clients in the room
    }

    /**
     * Returns the name of the room.
     *
     * @return the name of the room
     */
    public String getRoomName() {
        return roomName; // Return the room's name
    }

    /**
     * Returns the session associated with the room.
     *
     * @return the session associated with the room
     */
    public Session getSession() {
        return session; // Return the session associated with this room
    }
}