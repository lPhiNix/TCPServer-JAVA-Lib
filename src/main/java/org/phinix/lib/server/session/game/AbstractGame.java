package org.phinix.lib.server.session.game;

import org.phinix.lib.server.core.worker.Worker;

public abstract class AbstractGame implements Game {
    @Override
    public void start() {

    }

    @Override
    public void end() {

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
}
