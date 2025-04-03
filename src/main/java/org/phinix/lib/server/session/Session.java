package org.phinix.lib.server.session;

import org.phinix.lib.server.core.Manageable;

public interface Session extends Manageable {
    void start();
    boolean isEnd();
    void setIsEnd(boolean isEnd);
    void end();
}
