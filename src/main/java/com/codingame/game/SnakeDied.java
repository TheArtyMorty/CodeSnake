package com.codingame.game;

public class SnakeDied extends Exception {
    private static final long serialVersionUID = -8185589153224401564L;

    public SnakeDied(String message) {
        super(message);
    }

}

