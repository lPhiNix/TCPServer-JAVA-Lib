package org.phinix.example.common.game;

import org.phinix.example.common.model.Player;
import org.phinix.example.server.service.services.PlayerManager;
import org.phinix.lib.common.socket.MessagesManager;

public class ScoreManager {
    private final PlayerManager playerManager;
    private final Player thisPlayer;
    private final MessagesManager messagesManager;
    private int successes;

    public ScoreManager(PlayerManager playerManager, Player currentPlayer, MessagesManager messagesManager) {
        this.playerManager = playerManager;
        this.thisPlayer = currentPlayer;
        this.messagesManager = messagesManager;
        this.successes = 0;
    }

    public void success() {
        successes++;
    }


}
