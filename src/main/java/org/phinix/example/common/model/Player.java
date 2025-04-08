package org.phinix.example.common.model;

import org.phinix.lib.common.model.User;

public class Player extends User {
    private int victories;
    private int resolvedEquations;

    public Player(String username, String password, int victories, int resolvedEquations) {
        super(username, password);
        this.victories = victories;
        this.resolvedEquations = resolvedEquations;
    }

    public Player(int victories, int resolvedEquations) {
        this.victories = victories;
        this.resolvedEquations = resolvedEquations;
    }

    public Player(String username, String password) {
        super(username, password);
    }

    public Player() {}

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
    }

    public int getVictories() {
        return victories;
    }

    public int getResolvedEquations() {
        return resolvedEquations;
    }

    public void setVictories(int victories) {
        this.victories = victories;
    }

    public void setResolvedEquations(int resolvedEquations) {
        this.resolvedEquations = resolvedEquations;
    }
}
