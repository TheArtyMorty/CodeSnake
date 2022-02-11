package com.codingame.game;

public class Position {
	int x;
	int y;

    int from;
    int to;

	public Position(int x, int y, int f, int t) {
        this.x = x;
        this.y = y;
        from = f;
        to = t;
    }

    public Position Reverse()
    {
        return new Position(x,y,to,from);
    }

    public boolean Equals(Position other)
    {
        return x == other.x && y == other.y;
    }
}
