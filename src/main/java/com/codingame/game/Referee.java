package com.codingame.game;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.google.inject.Inject;

import java.util.List;

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

        gameManager.setFrameDuration(200);
        gameManager.setMaxTurns(200);
    }

    private void SendPlayerInputs(Player player)
    {
		masterGrid.SendToPlayer(player);
		
		// Possible Actions
		List<Action> actions = masterGrid.getValidActionsFor(player);
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

    @Override
    public void onEnd() {
        endScreenModule.setScores(gameManager.getPlayers().stream().mapToInt(p -> p.getScore()).toArray());
    }
}
