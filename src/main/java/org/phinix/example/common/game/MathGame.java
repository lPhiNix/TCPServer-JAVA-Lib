package org.phinix.example.common.game;

import org.phinix.example.common.game.score.ScoreManager;
import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.example.server.service.ServiceManager;
import org.phinix.lib.common.util.MessagesManager;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.service.services.RoomManager;
import org.phinix.lib.server.session.game.Game;

import java.util.List;

public class MathGame implements Game {

    private final List<Worker> players;
    private final RoomManager roomManager;
    private final ScoreManager[] scoreManagers;
    private int currentTurnIndex = 0;

    public MathGame(List<Worker> players, ServiceManager serviceManager) {
        this.players = players;
        this.roomManager = serviceManager.getService(RoomManager.class);
        this.scoreManagers = new ScoreManager[players.size()];
    }

    @Override
    public void start() {
        MessagesManager.broadcast(players, "Initializing new game!");
    }

    @Override
    public Worker isTurn() {
        return null;
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
