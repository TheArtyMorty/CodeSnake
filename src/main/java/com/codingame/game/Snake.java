package com.codingame.game;

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

	public boolean CanUseReverse()
	{
		return bonuses.contains("R");
	}

	public boolean CanUseTeleport()
	{
		return bonuses.contains("T");
	}

	public void AddBonus(char b)
	{
		if (bonuses.chars().filter(ch -> ch == b).count() < 3)
		{
			bonuses += b;
		}
	}

	public void UseBonus(char b)
	{
		bonuses = bonuses.replaceFirst(Character.toString(b),"");
	}

	public void Reverse()
	{
		UseBonus('R');
		//reverse Body
		Deque<Position> reversedBody = new LinkedList<Position>();
		while (!body.isEmpty())
		{
			reversedBody.addLast(body.removeLast().Reverse());
		}
		body = reversedBody;
	}

	private int GetTailOrientation()
	{
		return body.getLast().from;
	}

	public void MoveTo(Position p, boolean removeTail)
	{
		body.getFirst().to = Utils.ReverseDirection(p.from);
		body.addFirst(p);
		if (removeTail)
		{
			body.removeLast();
			Position newLast = body.removeLast();
			newLast.to = Utils.ReverseDirection(body.getLast().from);
			newLast.from = -1;
			body.addLast(newLast);
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

	public boolean CanGoInDirection(int d)
	{
		return (Utils.ReverseDirection(orientation)) != d;
	}

	public boolean CanReverseToDirection(int d)
	{
		return Utils.ReverseDirection(GetTailOrientation()) != d;
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
