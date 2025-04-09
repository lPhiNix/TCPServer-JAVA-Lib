package org.phinix.lib.server.service;

import org.phinix.lib.server.service.services.*;
import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.worker.AbstractWorker;

/**
 * {@code Service} interface is a marker interface that represents a service in the server.
 * <p>
 * Classes implementing this interface are intended to encapsulate specific
 * functionalities or business logic, making them modular and reusable.
 * <p>
 * This interface has been implemented for handling cleaner server dependencies.
 *
 * @see AbstractServer
 * @see AbstractWorker
 * @see AbstractPersistenceDataManager
 * @see AbstractUserManager
 * @see CommandProcessor
 * @see RoomManager
 */
public interface Service {}