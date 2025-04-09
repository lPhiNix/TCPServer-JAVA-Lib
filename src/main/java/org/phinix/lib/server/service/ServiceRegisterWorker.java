package org.phinix.lib.server.service;

import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.worker.AbstractWorker;

/**
 * {@code ServiceRegisterWorker} interface provides a factory method for creating instances of
 * {@link AbstractServiceRegister}.
 * <p>
 * This interface is used for create instance of a concrete {@code AbstractServiceRegister}
 * class (subclass) type.
 * <p>
 * The purpose of this interface is to abstract the creation of service registries,
 * enabling flexibility in their implementation.
 * <p>
 * This interface implements {@code Factory Design Pattern}
 *
 * @see AbstractServiceRegister
 * @see AbstractServer
 * @see AbstractWorker
 */
public interface ServiceRegisterWorker {
    /**
     * Creates and returns a new instance of {@link AbstractServiceRegister}.
     * This method defines how a service registry is initialized and returned to the caller.
     *
     * @return a new instance of {@link AbstractServiceRegister}
     */
    AbstractServiceRegister createServiceRegister();
}