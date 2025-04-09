package org.phinix.lib.server.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code AbstractServiceRegister} is an abstract base class that manages the registration and retrieval
 * of services within the server. Services are stored in a thread-safe {@link ConcurrentHashMap} to
 * ensure concurrent access is handled properly.
 * <p>
 * This class provides a framework for registering services of type {@link Service} and retrieving
 * them by their class type. It also includes a mechanism to initialize services through the
 * {@link #initServices()} method, which must be implemented by subclasses.
 *
 * @see Service
 */
public abstract class AbstractServiceRegister {
    private static final Logger logger = LogManager.getLogger();

    private final ConcurrentHashMap<Class<? extends Service>, Service> services;

    /**
     * Constructs an {@code AbstractServiceRegister} and initializes the service registry.
     * The constructor calls the {@link #initServices()} method to allow subclasses to
     * register their specific services.
     */
    public AbstractServiceRegister() {
        logger.log(Level.DEBUG, "Initializing");

        // Initialize an empty service registry.
        services = new ConcurrentHashMap<>();

        // Call the subclass-specific service initialization method.
        int amountRegisteredService = initServices();
        logger.log(Level.INFO, "{} service(s) registered in server", amountRegisteredService);
    }

    /**
     * Initializes the services to be registered in the service registry.
     * Subclasses must implement this method to define their own service registration logic.
     *
     * @return the number of services registered
     */
    protected abstract int initServices();

    /**
     * Registers a service in the registry with its corresponding class type.
     *
     * @param classService the class type of the service
     * @param service the service instance to register
     */
    protected void registerService(Class<? extends Service> classService, Service service) {
        services.put(classService, service);
    }

    /**
     * Retrieves a service from the registry by its class type.
     * If the service is not found, an error message is logged and {@code null} is returned.
     *
     * @param classService the class type of the service to retrieve
     * @param <S> the specific type of the service
     * @return the service instance, or {@code null} if not found
     */
    public synchronized <S extends Service> S getService(Class<S> classService) {
        S service = classService.cast(services.get(classService));

        if (service == null) {
            logger.log(Level.ERROR, "{} service is not registered and is not running in the server",
                    classService.getSimpleName());
            return null;
        }

        return service;
    }

    /**
     * Returns the total number of registered services in the registry.
     *
     * @return the number of registered services
     */
    protected int getAmountRegisterService() {
        return services.size();
    }
}