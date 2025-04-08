package org.phinix.example.server.service.services;

import org.phinix.example.common.model.Equation;
import org.phinix.lib.server.service.services.AbstractPersistenceDataManager;

import java.util.Random;

public class MathEquationPersistenceManager extends AbstractPersistenceDataManager {

    private static final String FILE_PATH = "equations.txt";

    public MathEquationPersistenceManager() {
        super(FILE_PATH);
    }

    @Override
    protected boolean isValidDataLine(String line) {
        return true;
    }

    public Equation getRandomEquation() {
        Random random = new Random();
        System.out.println(getEquationAmount());
        int ranNum = random.nextInt(1, getEquationAmount() - 1);

        return new Equation(super.getDataLine(ranNum));
    }
}
