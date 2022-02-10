package com.codingame.game;

import java.util.*;

public class Snake {
	private GraphicManager graphicManager;
	private int mapSize;
	private int color;

	public int orientation = 0;
	public String bonuses = "";
	public Deque<Position> body = new LinkedList<Position>();
	public Deque<Integer> orientations = new LinkedList<Integer>();;
	
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
		orientations.addLast(o);
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
			reversedBody.addLast(body.removeLast());
		}
		body = reversedBody;
		//reverse directions
		Deque<Integer> reversedOrientations = new LinkedList<Integer>();
		while (!orientations.isEmpty())
		{
			reversedOrientations.addLast((orientations.removeLast()+2) % 4);
		}
		orientations = reversedOrientations;
		orientation = orientations.getFirst();
	}

	private int GetTailOrientation()
	{
		return (orientations.getLast()+2)%4;
	}

	public void MoveTo(Position p, boolean removeTail)
	{
		body.addFirst(p);
		orientations.addFirst(orientation);
		if (removeTail)
		{
			body.removeLast();
			orientations.removeLast();
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

	public boolean CanReverseToDirection(int d)
	{
		return ((GetTailOrientation() + 2) % 4) != d;
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
