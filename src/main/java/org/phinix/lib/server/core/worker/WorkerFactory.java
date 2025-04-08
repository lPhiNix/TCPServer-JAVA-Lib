package org.phinix.lib.server.core.worker;

import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.service.AbstractServiceRegister;

import java.io.IOException;
import java.net.Socket;

/**
 * {@code WorkerFactory} interface is factory interface for creating {@link Worker} instances.
 * This interface is used for create instance of a concrete Worker class type.
 * <p>
 * This interface implements {@code Factory Design Pattern}
 *
 * @see Worker
 * @see AbstractWorker
 */
public interface WorkerFactory {
    /**
     * Creates a new {@link Worker} instance.
     *
     * @param socket the client socket
     * @param serverContext the server context
     * @param serviceRegister the service register
     * @return the created Worker
     * @throws IOException if an I/O error occurs
     */
    Worker createWorker(Socket socket,
                        Context serverContext,
                        AbstractServiceRegister serviceRegister)
            throws IOException;
}