package org.phinix.lib.server.core;

import org.phinix.lib.server.core.worker.Worker;

import java.util.List;

public interface Server extends Manageable {
    void start();
    void stop();
}
