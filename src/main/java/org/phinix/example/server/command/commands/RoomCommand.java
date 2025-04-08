package org.phinix.example.server.command.commands;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.example.common.game.MathGameRoom;
import org.phinix.example.server.command.CommandFactory;
import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.server.command.Command;
import org.phinix.lib.server.service.services.RoomManager;

public class RoomCommand implements Command<ClientHandler> {
    private static final Logger logger = LogManager.getLogger();
    private static final String COMMAND_NAME = "room";

    @Override
    public void execute(String[] args, ClientHandler client) {
        if (args.length < 1 ||
                (args.length == 1 && !(args[0].equals("leave") || args[0].equals("list"))) ||
                (args.length == 2 && !(args[0].equals("join"))) ||
                (args.length == 3 && !args[0].equals("create"))) {

            client.getMessagesManager().sendMessage("Help: " + CommandFactory.getCommandSymbol() +
                    COMMAND_NAME + " <create|join|leave|list> [roomName] [maxPlayers > 1] [rounds > 1]");
            return;
        }

        if (args[0].equals("create")) {
            int maxUsers = Integer.parseInt(args[2]);
            int rounds = Integer.parseInt(args[3]);
            if (maxUsers < 2 || rounds < 2) {
                client.getMessagesManager().sendMessage("Help: " + CommandFactory.getCommandSymbol() +
                        COMMAND_NAME + " <create|join|leave|list> [roomName] [maxPlayers > 1] [rounds > 1]");

                return;
            }
        }

        if (client.getCurrentUser() == null) {
            client.getMessagesManager().sendMessage("Must be login before playing");
            return;
        }

        if (args[0].equals("join") && client.getCurrentRoom() != null) {
            if (client.getCurrentRoom().getRoomName().equals(args[1])) {
                client.getMessagesManager().sendMessage("You are already in " + client.getCurrentRoom().getRoomName() + " room");
                return;
            }
        }

        logger.log(Level.DEBUG, "Executing command {} by {}", new Object[]{COMMAND_NAME, client.getClientAddress()});

        RoomManager<MathGameRoom, ClientHandler> roomManager = client.getServiceRegister().getService(RoomManager.class);
        String action = args[0];

        switch (action) {
            case "create" -> roomManager.createRoom(args[1], client, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            case "join" -> roomManager.joinRoom(args[1], client);
            case "leave" -> roomManager.leaveRoom(client, false);
            case "list" -> roomManager.printAllActiveRooms(client);
            default -> client.getMessagesManager().sendMessage("Help: " + CommandFactory.getCommandSymbol() +
                    COMMAND_NAME + " <create|join|leave|list> [roomName] [maxPlayers > 1] [rounds > 1]");
        }
    }

    public static String getCommandName() {
        return COMMAND_NAME;
    }
}
