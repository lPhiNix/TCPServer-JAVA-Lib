package org.phinix.example.server.service;

import org.phinix.example.common.game.MathGameRoom;
import org.phinix.example.server.command.CommandFactory;
import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.example.server.service.services.MathEquationPersistenceManager;
import org.phinix.example.server.service.services.PlayerManager;
import org.phinix.lib.server.service.AbstractServiceRegister;
import org.phinix.lib.server.service.services.CommandProcessor;
import org.phinix.lib.server.service.services.RoomManager;

public class ServiceManager extends AbstractServiceRegister {
    @Override
    protected int initServices() {
        registerService(PlayerManager.class, new PlayerManager());
        registerService(CommandProcessor.class, new CommandProcessor<>(new CommandFactory()));
        registerService(RoomManager.class, new RoomManager<>(MathGameRoom.class, ClientHandler.class));
        registerService(MathEquationPersistenceManager.class, new MathEquationPersistenceManager());

        return getAmountRegisterService();
    }
}
