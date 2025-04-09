package org.phinix.lib.server.service;

/**
 * {@code ServiceRegisterWorker} interface provides a factory method for creating instances of
 * {@link AbstractServiceRegister}.
 * <p>
 * It acts as a contract for classes that need to manage
 * the lifecycle of service registries in the server.
 * <p>
 * The purpose of this interface is to abstract the creation of service registries,
 * enabling flexibility in their implementation.
 *
 * @see AbstractServiceRegister
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