package com.codingame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class Referee extends AbstractReferee {
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private EndScreenModule endScreenModule;
    @Inject private GraphicManager graphicManager;

    private Grid masterGrid;

    @Override
    public void init()
    {
        long seed = gameManager.getSeed();
        masterGrid = new Grid(seed, graphicManager, gameManager.getPlayers());

        graphicManager.drawBackground();
        graphicManager.drawHud(gameManager.getPlayers());

        gameManager.setFrameDuration(150);
        gameManager.setMaxTurns(100);
    }

    private List<Action> getValidActionsFor(Player player) 
    {
        List<Action> validActions;
        validActions = masterGrid.getValidActionsFor(player);
        //Collections.shuffle(validActions, random);
        return validActions;
    }

    private void SendPlayerInputs(Player player)
    {
		masterGrid.SendToPlayer(player);
		
		// Possible Actions
		List<Action> actions = getValidActionsFor(player);
        player.sendInputLine(Integer.toString(actions.size()));
        for (Action action : actions)
        {
        	player.sendInputLine(action.toString());
        }
    }
    
    @Override
    public void gameTurn(int turn) {
        for (Player player : gameManager.getActivePlayers())
        {
            SendPlayerInputs(player);
            player.execute();
        }

        for (Player player : gameManager.getActivePlayers())
        {
            try {
                Action playerAction = player.getAction();
                masterGrid.play(playerAction);
            } catch (NumberFormatException e) {
                player.deactivate(String.format("$%d invalid action!", player.getIndex()));
                player.setScore(-1);
            } catch (TimeoutException e) {
                player.deactivate(String.format("$%d timeout!", player.getIndex()));
                player.setScore(-1);
            } catch (InvalidAction e) {
                player.deactivate(String.format("$%d invalid action!", player.getIndex()));
                player.setScore(-1);
            }
        }

        for (int i : masterGrid.UpdateAfterTurn())
        {
            Player player = gameManager.getPlayer(i);
            player.deactivate(String.format("$%d snake died from collision!", player.getIndex()));
            player.setScore(-1);
        }

    	CheckForGameEnd();
    }

    private void CheckForGameEnd()
    {
    	boolean gameWillEnd = false;
    	List<Player> players = gameManager.getActivePlayers();
    	
    	if ( players.size() < 2)
    	{
    		gameWillEnd = true;
    	}
    	
    	//Update scores
    	for (Player player : players)
		{
    		int snakeSize = masterGrid.GetPlayerSnakeSize(player);
    		player.setScore(snakeSize);
            graphicManager.UpdateScore(player.getIndex(), snakeSize);
			if (snakeSize >= 12)
			{
				gameManager.addTooltip(player,String.format("$%d snake reached 12 length !", player.getIndex()));
				gameWillEnd = true;
			}
		}
    	
    	if (gameWillEnd)
    	{
    		endGame();
    	}
    }
    
    private void endGame() {
        gameManager.endGame();

        Player p0 = gameManager.getPlayers().get(0);
        Player p1 = gameManager.getPlayers().get(1);
        if (p0.getScore() > p1.getScore()) {
            p1.hud.setAlpha(0.3);
        }
        if (p0.getScore() < p1.getScore()) {
            p0.hud.setAlpha(0.3);
        }

        endScreenModule.setScores(gameManager.getPlayers().stream().mapToInt(p -> p.getScore()).toArray());
    }
}
