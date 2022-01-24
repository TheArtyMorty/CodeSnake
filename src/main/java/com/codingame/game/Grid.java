package com.codingame.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Line;
import com.codingame.gameengine.module.entities.Sprite;
import com.google.inject.Inject;

public class Grid {
    @Inject private GraphicEntityModule graphicEntityModule;

    private String[] images = { "cross.png", "circle.png" };
    private String[] directions = { "UP", "RIGHT", "DOWN", "LEFT" };

    private Group entity;

    private int mapSize = 10;
    private char[][] grid;
    private long mapSeed;
    protected int winner = 0;
    
    private Snake[] snakes = new Snake[2];
    
    public Grid(int size, long seed)
    {
    	mapSize = size;
    	mapSeed = seed;
    	InitGridAndSnakes(size);
    }

    private void InitGridAndSnakes(int size)
    {
    	 grid = new char[size][size];
    	 
    	 for (int i = 0; i < size; i++)
    	 {
    		 char[] row = new char[size];
    		 Arrays.fill(row,'.');
    		 grid[i] = row;
    	 }
    		    
    	 grid[1][1]='0';
    	 grid[1][2]='0';
    	 grid[5][4]='A';
    	 grid[size-2][size-2]='1';
    	 grid[size-2][size-3]='1';
    	 
 		snakes[0] = new Snake(2,1,1,1,1);
 		snakes[1] = new Snake(size-3,size-2,size-2,size-2,3);
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
    	switch (action.direction)
        {
        case "UP":
        	if (snake.orientation == 2) return false;
        	break;
        case "RIGHT":
        	if (snake.orientation == 3) return false;
        	break;
        case "DOWN":
        	if (snake.orientation == 0) return false;
        	break;
        case "LEFT":
        	if (snake.orientation == 1) return false;
        	break;
        }
    	return true;
    }
    
    public void play(Action action) throws InvalidAction, SnakeDied {
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
		if (!MoveSnakeTo(snake, new Position(nextx, nexty), action.player.getIndex()))
		{
			throw new SnakeDied("Snake died !");
		}

        drawPlay(action);
    }
    
    private boolean MoveSnakeTo(Snake snake, Position position, int playerIndex) 
    {
    	switch (grid[position.y][position.x])
    	{
    	case '.':
    		snake.body.addFirst(position);
    		grid[position.y][position.x] = (char)('0' + playerIndex);
    		Position tail = snake.body.removeLast();
    		grid[tail.y][tail.x] = '.';
    		break;
    	case 'A':
    		snake.body.addFirst(position);
    		grid[position.y][position.x] = (char)('0' + playerIndex);
    		GenerateNewApple();
    		break;
    	default:
    		//Kill the snake
    		return false;
    	}
    	return true;
    }
    
    private void GenerateNewApple()
    {
    	boolean appleWasAdded = false;
    	Random random = new Random(mapSeed);
    	while (!appleWasAdded)
    	{
    		int x = random.nextInt(mapSize);
    		int y = random.nextInt(mapSize);
    		if (grid[y][x] == '.')
    		{
    			grid[y][x] = 'A';
    			return;
    		}
    	}
    }

    public void draw() {
        
    }

    public void drawPlay(Action action) {

    }

    public void hide() {
        this.entity.setAlpha(0);
        this.entity.setVisible(false);
    }

    public void activate() {
        this.entity.setAlpha(1, Curve.NONE);
        graphicEntityModule.commitEntityState(1, entity);
    }

    public void deactivate() {
        if (winner == 0) {
            this.entity.setAlpha(0.5, Curve.NONE);
            graphicEntityModule.commitEntityState(1, entity);
        }
    }
}