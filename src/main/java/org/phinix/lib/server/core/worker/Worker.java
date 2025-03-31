package org.phinix.lib.server.core.worker;

import org.phinix.lib.server.service.AbstractServiceRegister;
import org.phinix.lib.common.util.MessagesManager;

import java.io.IOException;

public interface Worker extends Runnable {
    void listen(String line) throws IOException;
    void closeConnection();
    MessagesManager getMessagesManager();
    AbstractServiceRegister getServiceRegister();
}
