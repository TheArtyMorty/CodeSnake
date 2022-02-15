package com.codingame.game;

import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

import java.util.*;

public class GraphicManager {
    @Inject private GraphicEntityModule graphicEntityModule;

    private int mapSize = 10;

    private String[] images = { "cross.png", "circle.png", "teleport.png", "reverse.png" };
    private String[] snakeBody = {"Body_UpDown.png",  "Body_LeftRight.png", "Body_UpRight.png", "Body_RightDown.png", "Body_DownLeft.png", "Body_LeftUp.png" };
    private String[] snakeHead = { "Head_Up.png", "Head_Right.png", "Head_Down.png", "Head_Left.png" };
    private String[] snakeTail = { "Tail_Up.png", "Tail_Right.png", "Tail_Down.png", "Tail_Left.png" };
    private Map<Character, Sprite> Items = new HashMap<Character, Sprite>() {};
    private List<List<Sprite>> snakeSprites = new ArrayList<>();
    private List<Text> PlayerScores = new ArrayList<Text>();
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
                .setImage("myLogo.png")
                .setX(1920 - 280)
                .setY(915)
                .setAnchor(0.5);
        graphicEntityModule.createSprite()
                .setImage("logo.png")
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

            Text score = graphicEntityModule.createText("Score : 2")
                    .setX(x)
                    .setY(y + 160)
                    .setZIndex(20)
                    .setFontSize(40)
                    .setFillColor(0xffffff)
                    .setAnchor(0.5);
            PlayerScores.add(score);

            Sprite avatar = graphicEntityModule.createSprite()
                    .setX(x)
                    .setY(y)
                    .setZIndex(20)
                    .setImage(player.getAvatarToken())
                    .setAnchor(0.5)
                    .setBaseHeight(116)
                    .setBaseWidth(116);

            player.hud = graphicEntityModule.createGroup(text, score, avatar);
        }
    }

    public void UpdateScore(int playerIndex, int score)
    {
        PlayerScores.get(playerIndex).setText(String.format("score : %d", score));
        graphicEntityModule.commitWorldState(0);
    }

    public void drawItem(int x, int y, char item)
    {
        int cellSize = (int) Math.round(720 / mapSize);
        int topLeftx = (int) Math.round(1920/2 - cellSize*mapSize/2);
        int topLefty = (int) Math.round(1080/2 - cellSize*mapSize/2);

        int color = 0xff1234;
        String image = images[1];

        switch (item)
        {
            case 'A':
            case 'B':
                color = playerColors[item - 'A'];
                image = images[1];
                break;
            case 'R':
                color = 0xff1234;
                image = images[3];
                break;
            case 'T':
            default:
                color = 0xff1234;
                image = images[2];
                break;
        }

        Items.put(item, graphicEntityModule.createSprite()
                .setX(topLeftx + x*cellSize+cellSize/2)
                .setY(topLefty + y*cellSize+cellSize/2)
                .setBaseHeight(cellSize)
                .setBaseWidth(cellSize)
                .setZIndex(20)
                .setImage(image)
                .setAnchor(0.5)
                .setTint(color));
    }

    public void HideItem(char i)
    {
        Items.get(i).setAlpha(0);
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
                if (map.grid[y][x] == '#')
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

    private String GetSpriteFromPosition(Position p)
    {
        if (p.from < 0)
        {
            return snakeTail[p.to];
        }
        else if (p.to < 0)
        {
            return snakeHead[p.from];
        }
        else
        {
            if (p.from % 2 == p.to % 2)
            {
                return snakeBody[p.from % 2]; //UpDown or LeftRight
            }
            else
            {
                if (p.from == (p.to + 3) % 4)
                {
                    return snakeBody[2 + p.from];
                }
                else
                {
                    return snakeBody[2 + p.to];
                }
            }
        }
    }

    public void AddSnakeSprite(Position p, int snakeId)
    {
        int cellSize = (int) Math.round(720 / mapSize);
        Sprite s = graphicEntityModule.createSprite()
                .setBaseHeight(cellSize)
                .setBaseWidth(cellSize)
                .setZIndex(20)
                .setImage(GetSpriteFromPosition(p))
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
                .setY(topLefty + p.y*cellSize+cellSize/2, Curve.IMMEDIATE)
                .setImage(GetSpriteFromPosition(p));
    }
}
