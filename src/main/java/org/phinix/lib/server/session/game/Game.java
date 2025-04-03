package org.phinix.lib.server.session.game;

import org.phinix.lib.server.session.Session;
import org.phinix.lib.server.core.worker.Worker;

public interface Game extends Session {
    Worker isTurn();
    void checkGameOver();
    void handleDisconnect(Worker client);
}
