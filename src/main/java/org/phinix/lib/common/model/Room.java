package org.phinix.lib.common.model;

import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.session.Session;

public interface Room {
    void addClient(Worker client);
    void removeClient(Worker client, boolean sessionIsEnded);
    boolean isEmpty();
    String getRoomName();
    String getClientsAmount();
    Session getSession();
}
