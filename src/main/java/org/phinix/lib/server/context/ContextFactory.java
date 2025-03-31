package org.phinix.lib.server.context;

import org.phinix.lib.server.core.AbstractServer;

public interface ContextFactory {
    Context createServerContext(AbstractServer server);
}
