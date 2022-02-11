package com.codingame.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;

public class Grid {
	private GraphicManager graphicManager;

    private String[] directions = { "UP", "RIGHT", "DOWN", "LEFT" };

    private Group entity;

    private int mapSize = 10;
    private char[][] grid;
	private String mapName;

    private long mapSeed;
    protected int winner = 0;
    
    private Snake[] snakes = new Snake[2];

	public Grid(long seed, GraphicManager graphicModule, List<Player> players)
    {
		graphicManager = graphicModule;
    	mapSeed = seed;
    	InitGridAndSnakes(players);
    }

    private void InitGridAndSnakes(List<Player> players)
    {
		 CMap theMap = com.codingame.game.CMap.MapGenerator.GenerateRandomMapForLeague(mapSeed,0);

    	 grid = theMap.grid;
    	 mapSize = theMap.mapSize;
		 mapName = theMap.mapName;

		 graphicManager.Init(mapSize, players.get(0).getColorToken(), players.get(1).getColorToken());

 		snakes[0] = new Snake(new Position(2,1,3, -1), new Position(1,1,-1, 1), 1, graphicManager, mapSize, 0);
 		snakes[1] = new Snake(new Position(mapSize-3,mapSize-2,1,-1),new Position(mapSize-2,mapSize-2,-1,3),3, graphicManager, mapSize, 1);

		 graphicManager.DrawGrid(theMap);

		GenerateFruits();
		GenerateNewItem('T');
		GenerateNewItem('R');
    }
    
    public List<Action> getValidActionsFor(Player player) {
		Snake snake = GetPlayerSnake(player);
        List<Action> validActions = new ArrayList<>();
		//Standard actions
		for (int i = 0; i < 4; i++)
		{
			if (snake.CanGoInDirection(i)) {
				validActions.add(new Action(null, directions[i], ""));
			}
		}
		//Using Teleport
		if (snake.CanUseTeleport())
		{
			for (int i = 0; i < 4; i++)
			{
				if (snake.CanGoInDirection(i)) {
					validActions.add(new Action(null, directions[i], "TELEPORT"));
				}
			}
		}
		//Using Reverse
		if (snake.CanUseReverse())
		{
			for (int i = 0; i < 4; i++)
			{
				if (snake.CanReverseToDirection(i)) {
					validActions.add(new Action(null, directions[i], "REVERSE"));
				}
			}
		}
        return validActions;
    }
    
    private Snake GetPlayerSnake(Player player)
    {
    	int snakeIndex = player.getIndex();
    	return snakes[snakeIndex];
    }
    
    public int GetPlayerSnakeSize(Player player)
    {
    	return GetPlayerSnake(player).body.size();
    }
    
    public void SendToPlayer(Player player)
    {
    	player.sendInputLine(Integer.toString(mapSize));
    	int snakeIndex = player.getIndex();
    	for (char[] row : grid)
    	{
    		String formattedRow = "";
    		for (char c : row)
    		{
    			if (snakeIndex != 0 && (c == '0' || c == '1'))
    			{
    				formattedRow += c == '0' ? '1' : '0';
    			}
    			else if (snakeIndex != 0 && (c == 'A' || c == 'B'))
				{
					formattedRow += c == 'A' ? 'B' : 'A';
				}
				else
    			{
    				formattedRow += c;
    			}
    		}
    		player.sendInputLine(formattedRow);
    	}
    	GetPlayerSnake(player).SendToPlayer(player);
    	snakes[1-snakeIndex].SendToPlayer(player);
    }

	private int DirectionAsInt(String direction)
	{
		for (int i = 0; i < 4; i++)
		{
			if (directions[i].equals(direction))
			{
				return i;
			}
		}
		return 0;
	}

    private boolean IsValidAction(Action action)
    {
    	Snake snake = GetPlayerSnake(action.player);
		int direction = DirectionAsInt(action.direction);
		if (action.bonus.equals("REVERSE"))
		{
			return snake.CanUseReverse() && snake.CanReverseToDirection(direction);
		}
		else if (action.bonus.equals("TELEPORT"))
		{
			return snake.CanUseTeleport() && snake.CanGoInDirection(direction);
		}
		else
		{
			return snake.CanGoInDirection(direction);
		}
    }
    
    public void play(Action action) throws InvalidAction {
        if (!IsValidAction(action)) 
        {
            throw new InvalidAction("Invalid move!");
        }
        
        Snake snake = GetPlayerSnake(action.player);
		if (action.bonus.equals("REVERSE"))
		{
			snake.Reverse();
		}
		int increment = action.bonus.equals("TELEPORT") ? 2 : 1;
		if (action.bonus.equals("TELEPORT"))
		{
			snake.UseBonus('T');
		}

        Position head = snake.body.getFirst();
        int nextx = head.x;
        int nexty = head.y;
		switch (action.direction)
        {
        case "UP":
        	snake.orientation = 0;
        	if (head.y > increment-1) {nexty = head.y - increment;}
        	else {nexty = mapSize - increment + head.y;}
        	break;
        case "RIGHT":
        	snake.orientation = 1;
			if (head.x < mapSize - increment) {nextx = head.x + increment;}
			else {nextx = 0 + increment - (mapSize- head.x);}
        	break;
        case "DOWN":
        	snake.orientation = 2;
			if (head.y < mapSize - increment) {nexty = head.y + increment;}
			else {nexty = 0 + increment - (mapSize- head.y);}
        	break;
        case "LEFT":
        	snake.orientation = 3;
			if (head.x > increment-1) {nextx = head.x - increment;}
			else {nextx = mapSize - increment + head.x;}
        	break;
        }
		int from = Utils.ReverseDirection(snake.orientation);
		MoveSnakeTo(snake, new Position(nextx, nexty,from,-1), action.player.getIndex());
    }
    
    private void MoveSnakeTo(Snake snake, Position position, int playerIndex)
    {
		Position tail;
		char cellContent = grid[position.y][position.x];
		boolean removeTail = cellContent != 'A' + playerIndex;
		//Move but Wait until end turn to know if really dead or both dead
		if (removeTail)
		{
			tail = snake.body.getLast();
			grid[tail.y][tail.x] = '.';
		}
		snake.MoveTo(position, removeTail);
    }

	public List<Integer> UpdateAfterTurn()
	{
		List<Integer> deadSnakes = new ArrayList<Integer>();
		if (snakes[0].Head().Equals(snakes[1].Head()))
		{
			deadSnakes.add(0);
			deadSnakes.add(1);
			return deadSnakes;
		}
		for (int i = 0; i < 2; i++)
		{
			Position position = snakes[i].Head();
			char cellContent = grid[position.y][position.x];
			switch (cellContent)
			{
				case 'T':
				case 'R':
					snakes[i].AddBonus(cellContent);
				case 'A':
				case 'B':
					graphicManager.HideItem(cellContent );
					GenerateNewItem(cellContent);
				case '.':
					grid[position.y][position.x] = (char)('0' + i);
					break;
				case 'W':
				case '0':
				case '1':
					deadSnakes.add(i);
					break;
			}
		}
		return deadSnakes;
	}

	private int distance(Position p1, Position p2)
	{
		return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
	}

	private void GenerateFruits()
	{
		boolean fruitWasAdded = false;
		Random random = new Random(mapSeed);
		while (!fruitWasAdded)
		{
			int x = random.nextInt(mapSize);
			int y = random.nextInt(mapSize);
			if (grid[y][x] == '.' &&
					distance(new Position(x,y,-1,-1), snakes[0].Head()) >= 4 &&
					distance(new Position(x,y,-1,-1), snakes[1].Head()) >= 4)
			{
				grid[y][x] = 'A';
				graphicManager.drawItem(x,y, 'A');
				grid[mapSize -1 - y][mapSize -1 - x] = 'B';
				graphicManager.drawItem(mapSize -1 -x,mapSize -1 - y, 'B');
				return;
			}
		}
	}

	private void GenerateNewItem(char item)
	{
		boolean itemWasAdded = false;
		Random random = new Random(mapSeed);
		while (!itemWasAdded)
		{
			int x = random.nextInt(mapSize);
			int y = random.nextInt(mapSize);
			if (grid[y][x] == '.' &&
					distance(new Position(x,y,-1,-1), snakes[0].Head()) >= 4 &&
					distance(new Position(x,y,-1,-1), snakes[1].Head()) >= 4)
			{
				grid[y][x] = item;
				graphicManager.drawItem(x,y, item);
				return;
			}
		}
	}
}