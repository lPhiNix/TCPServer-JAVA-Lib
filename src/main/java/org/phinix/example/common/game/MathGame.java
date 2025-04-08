package org.phinix.example.common.game;

import org.phinix.example.common.model.Equation;
import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.example.server.service.ServiceManager;
import org.phinix.example.server.service.services.MathEquationPersistenceManager;
import org.phinix.example.server.service.services.PlayerManager;
import org.phinix.lib.common.socket.MessagesManager;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.service.services.RoomManager;
import org.phinix.lib.server.session.game.Game;

import java.util.List;
import java.util.Queue;

public class MathGame implements Game {
    private final List<ClientHandler> players;
    private final RoomManager roomManager;
    private final MathEquationPersistenceManager equationProposer;
    private final ScoreManager[] scoreManagers;
    private Equation currentEquation;
    private int currentTurnIndex = 0;

    public MathGame(List<ClientHandler> players, ServiceManager serviceManager) {
        this.players = players;
        this.equationProposer = serviceManager.getService(MathEquationPersistenceManager.class);
        this.roomManager = serviceManager.getService(RoomManager.class);
        this.scoreManagers = new ScoreManager[players.size()];

        PlayerManager playerManager = serviceManager.getService(PlayerManager.class);

        initScoreManagers(playerManager);
    }

    private void initScoreManagers(PlayerManager manager) {
        for (int i = 0; i < players.size(); i++) {
            ClientHandler client = players.get(i);
            this.scoreManagers[i] = new ScoreManager(manager, client.getCurrentUser(), client.getMessagesManager());
        }
    }

    @Override
    public void start() {
        MessagesManager.broadcast(players, "Initializing new game!");
        announceTurn();
    }

    private void announceTurn() {
        ClientHandler client = isTurn();
        MessagesManager.broadcastLess(players, client, "Turn of " + client.getClientAddress());
        client.getMessagesManager().sendMessage("It's your turn!");
        proposeEquation(client);
    }

    private void nextTurn() {
        currentTurnIndex = (currentTurnIndex + 1) % players.size();
        announceTurn();
    }

    @Override
    public ClientHandler isTurn() {
        return players.get(currentTurnIndex);
    }

    private void proposeEquation(ClientHandler client) {
        currentEquation = equationProposer.getRandomEquation();
        MessagesManager.broadcastLess(players, client, "Equation that " + client.getClientAddress() + " have to resolve" );
        MessagesManager.broadcastLess(players, client, "Equation: " + currentEquation.getMathExpression());
        client.getMessagesManager().sendMessage("RESOLVE THIS EQUATION: " + currentEquation.getMathExpression());

    }

    public void tryGuessRoot(String mathExpression, ClientHandler player) {
        ClientHandler turn = players.get(currentTurnIndex);

        if (!turn.equals(player)) {
            player.getMessagesManager().sendMessage("it is not your turn!");
            return;
        }

        if (currentEquation != null) {
            boolean success = currentEquation.tryGuessRoot(mathExpression);

            if (success) {
                player.getMessagesManager().sendMessage("CORRECT: " + currentEquation.getMathExpression() + " = 0; x = " + mathExpression + "!");
                scoreManagers[currentTurnIndex].success();
            } else {
                player.getMessagesManager().sendMessage("INCORRECT: " + currentEquation.getMathExpression() + " != 0; x = " + mathExpression + "!");
            }

            nextTurn();
            checkGameOver();
        }
    }

    @Override
    public void checkGameOver() {

    }

    @Override
    public <W extends Worker> void handleDisconnect(W client) {
        players.remove((ClientHandler) client);

        if (players.size() < 2) {
            roomManager.leaveRoom(players.getFirst(), true);
            // game0ver = true
        }

        if (!isTurn().equals(client)) {
            announceTurn();
            return;
        }

        nextTurn();
    }

    @Override
    public boolean isEnd() {
        return false;
    }

    @Override
    public void setIsEnd(boolean isEnd) {

    }

    @Override
    public void end() {

    }
}
