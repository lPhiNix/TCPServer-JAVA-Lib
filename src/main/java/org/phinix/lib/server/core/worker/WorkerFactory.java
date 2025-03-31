package org.phinix.lib.server.core.worker;

import org.phinix.lib.server.context.Context;

import java.io.IOException;
import java.net.Socket;

public interface WorkerFactory {
    Worker createWorker(Socket socket, Context serverContext) throws IOException;
}
