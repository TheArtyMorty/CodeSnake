package com.codingame.game;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import javafx.geometry.Pos;

import java.util.*;

public class Snake {
	private GraphicManager graphicManager;
	private int mapSize;
	private int color;

	public int orientation = 0;
	public String bonuses = "";
	public Deque<Position> body = new LinkedList<Position>();
	
	private String[] directions = { "UP", "RIGHT", "DOWN", "LEFT" };

	public Snake(Position head, Position tail, int o, GraphicManager graphicModule, int size, int c) {
		graphicManager = graphicModule;
		mapSize = size;
		color = c;
		body.clear();
		body.addFirst(head);
		graphicManager.AddSnakeSprite(head, c);
		body.addLast(tail);
		graphicManager.AddSnakeSprite(tail, c);
        orientation = o;
        bonuses = "";
    }



	public void MoveSnakeTo(Position p, boolean removeTail)
	{
		body.addFirst(p);
		if (removeTail)
		{
			body.removeLast();
		}
		else
		{
			graphicManager.AddSnakeSprite(p,color);
		}
		for (int i = 0; i < body.size(); i++)
		{
			graphicManager.MoveSnakeSpriteTo(color,i, (Position)body.toArray()[i]);
		}
	}

	public Position Head()
	{
		return body.getFirst();
	}

	public Position RemoveTail()
	{
		return body.removeLast();
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
