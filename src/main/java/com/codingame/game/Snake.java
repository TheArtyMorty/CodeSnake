package com.codingame.game;
import java.util.Deque;
import java.util.LinkedList;

public class Snake {
	public int orientation = 0;
	public String bonuses = "";
	public Deque<Position> body = new LinkedList<Position>();
	
	private String[] directions = { "UP", "RIGHT", "DOWN", "LEFT" };

	public Snake(int headx, int heady, int tailx, int taily, int o) {
		body.clear();
		body.add(new Position(headx,heady));
		body.add(new Position(tailx,taily));
        orientation = o;
        bonuses = "";
    }
	
	public boolean CanGoInDirection(int d)
	{
		return ((orientation + 2) % 4) != d;
	}
	
	public void SendToPlayer(Player player)
    {
		player.sendInputLine(directions[orientation]);
		if (bonuses.isEmpty()) {player.sendInputLine("NONE");}
		else {player.sendInputLine(bonuses);}
    	player.sendInputLine(Integer.toString(body.size()));
    	for (Position p : body)
    	{
    		player.sendInputLine(Integer.toString(p.x) + ' ' + Integer.toString(p.y));
    	}
    }
}
