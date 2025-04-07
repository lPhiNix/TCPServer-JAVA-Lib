package org.phinix.example.server.service.services;

import org.phinix.lib.server.service.services.AbstractPersistenceDataManager;

public class MathEquationPersistenceManager extends AbstractPersistenceDataManager {

    private static final String FILE_PATH = "equations.txt";

    public MathEquationPersistenceManager() {
        super(FILE_PATH);
    }

    @Override
    protected boolean isValidDataLine(String line) {
        return false;
    }
}
