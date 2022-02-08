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

 		snakes[0] = new Snake(new Position(2,1), new Position(1,1), 1, graphicManager, mapSize, 0);
 		snakes[1] = new Snake(new Position(mapSize-3,mapSize-2),new Position(mapSize-2,mapSize-2),3, graphicManager, mapSize, 1);

		 graphicManager.DrawGrid(theMap);

		GenerateFruits();
    }
    
    public List<Action> getValidActionsFor(Player player) {
        List<Action> validActions = new ArrayList<>();
        if (winner == 0) {
        	for (int i = 0; i < 4; i++)
        	{
        		if (GetPlayerSnake(player).CanGoInDirection(i)) {
        			validActions.add(new Action(null, directions[i], ""));
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

    private boolean IsValidAction(Action action)
    {
    	Snake snake = GetPlayerSnake(action.player);
		for (int i = 0; i < 4; i++)
		{
			if (directions[i].equals(action.direction))
			{
				return snake.CanGoInDirection(i);
			}
		}
    	return false;
    }
    
    public void play(Action action) throws InvalidAction {
        if (!IsValidAction(action)) 
        {
            throw new InvalidAction("Invalid move!");
        }
        
        Snake snake = GetPlayerSnake(action.player);
        Position head = snake.body.getFirst();
        int nextx = head.x;
        int nexty = head.y;
		switch (action.direction)
        {
        case "UP":
        	snake.orientation = 0;
        	if (head.y > 0) {nexty = head.y - 1;}
        	else {nexty = mapSize - 1;}
        	break;
        case "RIGHT":
        	snake.orientation = 1;
        	if (head.x < mapSize - 1) {nextx = head.x + 1;}
        	else {nextx = 0;}
        	break;
        case "DOWN":
        	snake.orientation = 2;
        	if (head.y < mapSize -1) {nexty = head.y + 1;}
        	else {nexty = 0;}
        	break;
        case "LEFT":
        	snake.orientation = 3;
        	if (head.x > 0) {nextx = head.x - 1;}
        	else {nextx = mapSize - 1;}
        	break;
        }
		MoveSnakeTo(snake, new Position(nextx, nexty), action.player.getIndex());
    }
    
    private void MoveSnakeTo(Snake snake, Position position, int playerIndex)
    {
		Position tail;
		char cellContent = grid[position.y][position.x];
    	switch (cellContent)
    	{
    	case 'A':
		case 'B':
			snake.MoveSnakeTo(position, ('A' + playerIndex) != cellContent);
			if (('A' + playerIndex) != cellContent)
			{
				tail = snake.body.getLast();
				grid[tail.y][tail.x] = '.';
			}
			graphicManager.HideFruit(cellContent - 'A');
    		GenerateNewFruit(cellContent);
    		break;
		case '.':
		case '0':
		case '1':
		case 'W':
		default:
			//Move but Wait until end turn to know if really dead or both dead
			tail = snake.body.getLast();
			grid[tail.y][tail.x] = '.';
			snake.MoveSnakeTo(position, true);
			break;
    	}
    }

	public List<Integer> UpdateAfterTurn()
	{
		List<Integer> deadSnakes = new ArrayList<Integer>();
		if (snakes[0].Head() == snakes[1].Head())
		{
			deadSnakes.add(0);
			deadSnakes.add(1);
		}
		for (int i = 0; i < 2; i++)
		{
			Position position = snakes[i].Head();
			char cellContent = grid[position.y][position.x];
			switch (cellContent)
			{
				case 'A':
				case 'B':
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
					distance(new Position(x,y), snakes[0].Head()) >= 4 &&
					distance(new Position(x,y), snakes[1].Head()) >= 4)
			{
				grid[y][x] = 'A';
				graphicManager.drawFruit(x,y, 'A');
				grid[mapSize -1 - y][mapSize -1 - x] = 'B';
				graphicManager.drawFruit(mapSize -1 -x,mapSize -1 - y, 'B');
				return;
			}
		}
	}

    private void GenerateNewFruit(char fruit)
    {
    	boolean fruitWasAdded = false;
    	Random random = new Random(mapSeed);
    	while (!fruitWasAdded)
    	{
    		int x = random.nextInt(mapSize);
    		int y = random.nextInt(mapSize);
    		if (grid[y][x] == '.' &&
				distance(new Position(x,y), snakes[0].Head()) >= 4 &&
				distance(new Position(x,y), snakes[1].Head()) >= 4)
    		{
    			grid[y][x] = fruit;
				graphicManager.drawFruit(x,y, fruit);
    			return;
    		}
    	}
    }
}