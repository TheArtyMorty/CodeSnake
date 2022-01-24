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
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class Referee extends AbstractReferee {
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    private Grid masterGrid;

    @Override
    public void init()
    {        
        masterGrid = new Grid(12, gameManager.getSeed());
        drawBackground();
        drawHud();
        drawGrid();

        gameManager.setFrameDuration(200);
        gameManager.setMaxTurns(100);
    }

    private void drawBackground() {
        graphicEntityModule.createSprite()
                .setImage("Background.jpg")
                .setAnchor(0);
        graphicEntityModule.createSprite()
                .setImage("logo.png")
                .setX(1920 - 280)
                .setY(915)
                .setAnchor(0.5);
        graphicEntityModule.createSprite()
                .setImage("myLogo.png")
                .setX(280)
                .setY(915)
                .setAnchor(0.5);
    }

    private void drawGrid() {
        masterGrid.draw();

        graphicEntityModule
            .createSprite()
            .setImage("board_border.png")
            .setX(1920 / 2)
            .setY(1080 / 2)
            .setAnchor(0.5);
    }
    
    private void drawHud() {
        for (Player player : gameManager.getPlayers()) {
            int x = player.getIndex() == 0 ? 280 : 1920 - 280;
            int y = 220;

            graphicEntityModule
                    .createRectangle()
                    .setWidth(140)
                    .setHeight(140)
                    .setX(x - 70)
                    .setY(y - 70)
                    .setLineWidth(0)
                    .setFillColor(player.getColorToken());

            graphicEntityModule
                    .createRectangle()
                    .setWidth(120)
                    .setHeight(120)
                    .setX(x - 60)
                    .setY(y - 60)
                    .setLineWidth(0)
                    .setFillColor(0xffffff);

            Text text = graphicEntityModule.createText(player.getNicknameToken())
                    .setX(x)
                    .setY(y + 120)
                    .setZIndex(20)
                    .setFontSize(40)
                    .setFillColor(0xffffff)
                    .setAnchor(0.5);

            Sprite avatar = graphicEntityModule.createSprite()
                    .setX(x)
                    .setY(y)
                    .setZIndex(20)
                    .setImage(player.getAvatarToken())
                    .setAnchor(0.5)
                    .setBaseHeight(116)
                    .setBaseWidth(116);

            player.hud = graphicEntityModule.createGroup(text, avatar);
        }
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
			} catch (SnakeDied e) {
				player.deactivate(String.format("$%d snake died from collision!", player.getIndex()));
			    player.setScore(-1);
			} catch (InvalidAction e) {
				player.deactivate(String.format("$%d invalid action!", player.getIndex()));
			    player.setScore(-1);
			} 
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
    }
}
