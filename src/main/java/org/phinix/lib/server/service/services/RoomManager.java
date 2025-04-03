package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.common.model.AbstractRoom;
import org.phinix.lib.common.model.Room;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.service.Service;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager<R extends Room> implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final Map<String, R> rooms;

    public RoomManager() {
        rooms = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public synchronized void createRoom(String roomName, Worker owner) {
        if (rooms.containsKey(roomName)) {
            owner.getMessagesManager().sendMessage("This room already exists!");
            return;
        }

        try {
            Constructor<R> constructor = (Constructor<R>) AbstractRoom.class.getConstructor(String.class, Worker.class);
            R room = constructor.newInstance(roomName, owner);

            rooms.put(roomName, room);

            owner.getMessagesManager().sendMessage("Room " + roomName + " created successfully!");
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error creating room: {}", roomName, e);
            owner.getMessagesManager().sendMessage("Error creating room: " + roomName);
        }
    }

    public synchronized void joinRoom(String roomName, Worker client) {
        R room = rooms.get(roomName);
        if (room == null) {
            client.getMessagesManager().sendMessage("Room " + roomName + " does not exist!");
            return;
        }
        room.addClient(client);
    }

    @SuppressWarnings("unchecked")
    public synchronized void leaveRoom(Worker client, boolean isSessionEnd) {
        R room = (R) client.getCurrentRoom();

        if (room == null) {
            return;
        }

        logger.log(Level.INFO, "{} has leaved from room {}", new Object[]{client.getClientAddress(), room.getRoomName()});
        client.getMessagesManager().sendMessage("You has leaved from room {}" + room.getRoomName());

        if (!isSessionEnd) {
            room.removeClient(client, false);

            if (room.isEmpty()) {
                rooms.remove(room.getRoomName());
            }
            return;
        }

        room.removeClient(client, true);
        rooms.remove(room.getRoomName());
    }


    public synchronized void printAllActiveRooms(Worker client) {
        if (rooms.isEmpty()) {
            client.getMessagesManager().sendMessage("There are not active rooms.");
            return;
        }

        client.getMessagesManager().sendMessage("Active Rooms: ");
        for (String roomName : rooms.keySet()) {
            client.getMessagesManager().sendMessage("Name: " + roomName + ", Users: " + rooms.get(roomName).getClientsAmount());
        }
    }
}
