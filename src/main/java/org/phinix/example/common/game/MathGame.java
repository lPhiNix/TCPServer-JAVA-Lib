package org.phinix.example.common.game;

import org.phinix.example.common.game.score.ScoreManager;
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

    }

    @Override
    public ClientHandler isTurn() {
        return players.get(currentTurnIndex);
    }

    private void proposeEquation() {
        currentEquation = equationProposer.getRandomEquation();

    }

    @Override
    public void checkGameOver() {

    }

    @Override
    public void handleDisconnect(Worker client) {

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
