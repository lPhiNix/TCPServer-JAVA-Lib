package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.common.model.room.Room;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.service.Service;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code RoomManager} provides functionality to manage rooms in the server.
 * Rooms are logical spaces that facilitate interactions between workers (clients),
 * such as chat rooms or multiplayer game lobbies.
 * <p>
 * This class enables the creation, joining, leaving, and listing of rooms. It supports
 * a generic type {@code R} for rooms and {@code W} for workers, ensuring flexibility
 * for different implementations.
 *
 * @param <R> the type of the room
 * @param <W> the type of the worker
 * @see Room
 * @see Worker
 */
public class RoomManager<R extends Room, W extends Worker> implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final Map<String, R> rooms; // Stores rooms with their names as keys
    private final Class<R> roomType; // The type of room to manage
    private final Class<W> workerType; // The type of worker associated with rooms

    /**
     * Constructs a {@code RoomManager} for the specified room and worker types.
     *
     * @param roomType   the class type of the room
     * @param workerType the class type of the worker
     */
    public RoomManager(Class<R> roomType, Class<W> workerType) {
        this.roomType = roomType;
        this.workerType = workerType;
        rooms = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new room with the specified parameters.
     * If a room with the given name already exists, the room creation is aborted.
     *
     * @param roomName the name of the room
     * @param owner    the worker who owns the room
     * @param maxUsers the maximum number of users allowed in the room
     * @param rounds   the number of rounds (used in games)
     */
    public synchronized void createRoom(String roomName, Worker owner, int maxUsers, int rounds) {
        if (rooms.containsKey(roomName)) {
            // Notify the owner if the room already exists
            owner.getMessagesManager().sendMessage("This room already exists!");
            return;
        }

        try {
            logger.log(Level.DEBUG, "Creating room with type: {}", roomType.getName());
            // Use reflection to construct a new room instance
            Constructor<R> constructor = roomType.getConstructor(String.class, workerType, int.class, int.class);
            R room = constructor.newInstance(roomName, owner, maxUsers, rounds);

            // Add the room to the collection
            rooms.put(roomName, room);

            // Notify the owner of successful room creation
            owner.getMessagesManager().sendMessage("Room " + roomName + " created successfully!");
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error creating room: {}", roomName, e);
            owner.getMessagesManager().sendMessage("Error creating room: " + roomName);
        }
    }

    /**
     * Allows a worker to join an existing room.
     * If the room does not exist, the worker is notified.
     *
     * @param roomName the name of the room
     * @param client   the worker attempting to join the room
     */
    public synchronized void joinRoom(String roomName, Worker client) {
        R room = rooms.get(roomName);
        if (room == null) {
            // Notify the client if the room does not exist
            client.getMessagesManager().sendMessage("Room " + roomName + " does not exist!");
            return;
        }
        // Add the client to the room
        room.addClient(client);
    }

    /**
     * Allows a worker to leave a room.
     * If the session has ended, the room is removed; otherwise, the worker is simply removed.
     *
     * @param client       the worker leaving the room
     * @param isSessionEnd {@code true} if the session has ended, {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    public synchronized void leaveRoom(Worker client, boolean isSessionEnd) {
        R room = (R) client.getCurrentRoom();

        if (room == null) {
            return; // The worker is not in any room
        }

        logger.log(Level.INFO, "{} has left the room {}", client.getClientAddress(), room.getRoomName());
        client.getMessagesManager().sendMessage("You have left the room " + room.getRoomName());

        if (!isSessionEnd) {
            // Remove the client from the room without ending the session
            room.removeClient(client, false);

            if (room.isEmpty()) {
                // Remove the room if it is empty
                rooms.remove(room.getRoomName());
            }
            return;
        }

        // Remove the client and terminate the session
        room.removeClient(client, true);
        rooms.remove(room.getRoomName());
    }

    /**
     * Sends a list of all active rooms to the specified worker.
     * If no rooms are active, the worker is notified.
     *
     * @param client the worker requesting the list of active rooms
     */
    public synchronized void printAllActiveRooms(Worker client) {
        if (rooms.isEmpty()) {
            // Notify the client if no rooms are active
            client.getMessagesManager().sendMessage("There are no active rooms.");
            return;
        }

        // Send the list of active rooms to the client
        client.getMessagesManager().sendMessage("Active Rooms: ");
        for (String roomName : rooms.keySet()) {
            client.getMessagesManager().sendMessage(
                    "Name: " + roomName + ", Users: " + rooms.get(roomName).getClientsAmount()
            );
        }
    }
}