package com.codingame.game;

public class Action {
    public final String direction;
    public final String bonus;
    public Player player;
    
    public Action(Player p, String d, String b) {
        this.player = p;
        this.direction = d;
        this.bonus = b;
    }
    
    @Override
    public String toString() {
        return direction + (bonus.isEmpty() ? "" : " " + bonus);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Action) {
            Action other = (Action) obj;
            return direction == other.direction && bonus == other.bonus;
        } else {
            return false;
        }
    }
}