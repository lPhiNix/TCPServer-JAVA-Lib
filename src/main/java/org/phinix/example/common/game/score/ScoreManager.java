package org.phinix.example.common.game.score;

import org.phinix.example.common.model.Player;
import org.phinix.example.server.service.services.PlayerManager;
import org.phinix.lib.common.socket.MessagesManager;

public class ScoreManager {
    private final PlayerManager playerManager;
    private final Player thisPlayer;
    private final MessagesManager messagesManager;
    private int tries;

    public ScoreManager(PlayerManager playerManager, Player currentPlayer, MessagesManager messagesManager) {
        this.playerManager = playerManager;
        this.thisPlayer = currentPlayer;
        this.messagesManager = messagesManager;
        this.tries = 0;
    }

    public void incrementTries() {
        tries++;
    }


}
