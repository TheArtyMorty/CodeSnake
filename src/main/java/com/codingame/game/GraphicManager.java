package com.codingame.game;

import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphicManager {
    @Inject private GraphicEntityModule graphicEntityModule;

    private int mapSize = 10;

    private String[] images = { "cross.png", "circle.png" };
    private Sprite[] fruits = new Sprite[2];
    private List<List<Sprite>> snakeSprites = new ArrayList<>();
    private int[] playerColors = new int[2];

    public void Init(int size, int c1, int c2)
    {
        mapSize = size;
        playerColors[0] = c1;
        playerColors[1] = c2;
        snakeSprites.add(new LinkedList<>());
        snakeSprites.add(new LinkedList<>());
    }

    public void drawBackground() {
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

    public void drawHud(List<Player> players) {
        for (Player player : players) {
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

    public void drawFruit(int x, int y, char fruit)
    {
        int cellSize = (int) Math.round(720 / mapSize);
        int topLeftx = (int) Math.round(1920/2 - cellSize*mapSize/2);
        int topLefty = (int) Math.round(1080/2 - cellSize*mapSize/2);
        fruits[fruit - 'A'] = graphicEntityModule.createSprite()
                .setX(topLeftx + x*cellSize+cellSize/2)
                .setY(topLefty + y*cellSize+cellSize/2)
                .setBaseHeight(cellSize)
                .setBaseWidth(cellSize)
                .setZIndex(20)
                .setImage(images[1])
                .setAnchor(0.5)
                .setTint(playerColors[fruit - 'A']);
    }

    public void HideFruit(int i)
    {
        fruits[i].setAlpha(0);
    }
    public void drawWall(int x, int y)
    {
        int cellSize = (int) Math.round(720 / mapSize);
        int topLeftx = (int) Math.round(1920/2 - cellSize*mapSize/2);
        int topLefty = (int) Math.round(1080/2 - cellSize*mapSize/2);
        graphicEntityModule.createRectangle()
                .setX(topLeftx + x*cellSize)
                .setY(topLefty + y*cellSize)
                .setWidth(cellSize)
                .setHeight(cellSize)
                .setZIndex(10)
                .setFillColor(0x000000);
    }

    public void drawEmptyCell(int x, int y)
    {
        int cellSize = (int) Math.round(720 / mapSize);
        int topLeftx = (int) Math.round(1920/2 - cellSize*mapSize/2);
        int topLefty = (int) Math.round(1080/2 - cellSize*mapSize/2);
        graphicEntityModule.createRectangle()
                .setX(topLeftx + x*cellSize)
                .setY(topLefty + y*cellSize)
                .setWidth(cellSize)
                .setHeight(cellSize)
                .setZIndex(10)
                .setFillColor(0xffffff);
    }

    public void DrawGrid(CMap map) {
        graphicEntityModule.createText(map.mapName)
                .setX(1920 / 2)
                .setY(120)
                .setZIndex(20)
                .setFontSize(40)
                .setFillColor(0xffffff)
                .setAnchor(0.5);

        for(int y = 0; y < mapSize; y++)
        {
            for (int x = 0; x < mapSize; x++)
            {
                if (map.grid[y][x] == 'W')
                {
                    drawWall(x,y);
                }
                else
                {
                    drawEmptyCell(x,y);
                }
            }
        }

        graphicEntityModule
                .createSprite()
                .setImage("board_border.png")
                .setX(1920 / 2)
                .setY(1080 / 2)
                .setAnchor(0.5)
                .setZIndex(25);
    }


    public void AddSnakeSprite(Position p, int snakeId)
    {
        int cellSize = (int) Math.round(720 / mapSize);
        Sprite s = graphicEntityModule.createSprite()
                .setBaseHeight(cellSize)
                .setBaseWidth(cellSize)
                .setZIndex(20)
                .setImage("cross.png")
                .setAnchor(0.5)
                .setTint(playerColors[snakeId]);
        snakeSprites.get(snakeId).add(s);
        MoveSnakeSpriteTo(snakeId,snakeSprites.get(snakeId).size()-1,p);
    }

    public void MoveSnakeSpriteTo(int snakeId, int spriteIndex, Position p)
    {
        int cellSize = (int) Math.round(720 / mapSize);
        int topLeftx = (int) Math.round(1920/2 - cellSize*mapSize/2);
        int topLefty = (int) Math.round(1080/2 - cellSize*mapSize/2);
        snakeSprites.get(snakeId).get(spriteIndex)
                .setX(topLeftx + p.x*cellSize+cellSize/2, Curve.IMMEDIATE)
                .setY(topLefty + p.y*cellSize+cellSize/2, Curve.IMMEDIATE);

    }
}
