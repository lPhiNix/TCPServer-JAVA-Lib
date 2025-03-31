package org.phinix.lib.server.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractServiceRegister {
    private static final Logger logger = LogManager.getLogger();

    private final ConcurrentHashMap<Class<? extends Service>, Service> services;

    public AbstractServiceRegister() {
        logger.log(Level.DEBUG, "Initializing");

        services = new ConcurrentHashMap<>();

        int amountRegisteredService = initServices();
        logger.log(Level.INFO, "{} service registered in server", amountRegisteredService);
    }

    protected abstract int initServices();

    protected void registerService(Class<? extends Service> classService, Service service) {
        services.put(classService, service);
    }

    public synchronized <S extends Service> S getService(Class<S> classService) {
        S service = classService.cast(services.get(classService));

        if (service == null) {
            logger.log(Level.ERROR, "{} service is not register and is not running in server", classService.getSimpleName());
            return null;
        }

        return service;
    }

    protected int getAmountRegisterService() {
        return services.size();
    }
}
