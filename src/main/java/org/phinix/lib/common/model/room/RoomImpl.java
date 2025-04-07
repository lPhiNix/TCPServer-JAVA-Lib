package org.phinix.lib.common.model.room;

import org.phinix.lib.common.util.MessagesManager;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.session.Session;
import org.phinix.lib.server.session.game.Game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomImpl implements Room {
    protected int maxUsers;

    protected String roomName;
    protected List<Worker> clients;
    protected Session session;

    public RoomImpl(String roomName, Worker owner, int maxUsers) {
        this.roomName = roomName;
        this.maxUsers = maxUsers;
        this.clients = new CopyOnWriteArrayList<>();
        this.clients.add(owner);
        owner.setCurrentRoom(this);
    }

    public synchronized void addClient(Worker client) {
        if (clients.size() >= maxUsers) {
            client.getMessagesManager().sendMessage("Room " + roomName + " is full! " + getClientsAmount());
            return;
        }

        client.getMessagesManager().sendMessage("You enter in the room " + roomName + " successfully!");
        clients.add(client);
        client.setCurrentRoom(this);

        MessagesManager.broadcast(clients, client.getClientAddress() + "has join to this room");

        if (clients.size() == maxUsers) {
            startSession();
        }
    }

    public synchronized void removeClient(Worker client, boolean isSessionEnd) {
        clients.remove(client);
        MessagesManager.broadcast(clients, client.getClientAddress() + "has leaved from room");

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

    public void startSession() {}

    public boolean isEmpty() {
        return clients.isEmpty();
    }

    public synchronized String getClientsAmount() {
        return "(" + clients.size() + "/" + maxUsers + ")";
    }

    public String getRoomName() {
        return roomName;
    }

    public Session getSession() {
        return session;
    }
}
