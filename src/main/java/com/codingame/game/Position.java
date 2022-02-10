package com.codingame.game;

public class Position {
	int x;
	int y;
	
	public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean Equals(Position other)
    {
        return x == other.x && y == other.y;
    }
}
