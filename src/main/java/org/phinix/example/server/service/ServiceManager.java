package org.phinix.example.server.service;

import org.phinix.example.server.command.CommandFactory;
import org.phinix.example.server.service.services.UserManager;
import org.phinix.example.server.thread.ClientHandler;
import org.phinix.lib.server.service.AbstractServiceRegister;
import org.phinix.lib.server.service.services.CommandProcessor;

public class ServiceManager extends AbstractServiceRegister {
    @Override
    protected int initServices() {
        registerService(UserManager.class, new UserManager());
        registerService(CommandProcessor.class, new CommandProcessor<ClientHandler>(new CommandFactory()));

        return getAmountRegisterService();
    }
}
