package org.phinix.lib.server.core.worker;

import org.phinix.lib.common.model.AbstractRoom;
import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.service.AbstractServiceRegister;
import org.phinix.lib.common.util.MessagesManager;

import java.io.IOException;

public interface Worker extends Runnable, Manageable {
    void listen(String line) throws IOException;
    void closeConnection();
    MessagesManager getMessagesManager();
    AbstractServiceRegister getServiceRegister();
    Context getServerContext();
    AbstractRoom getCurrentRoom();
    void setCurrentRoom(AbstractRoom abstractRoom);
    String getClientAddress();
}
