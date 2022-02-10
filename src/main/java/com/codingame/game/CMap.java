package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CMap {
    public String mapName;
    public int mapSize;
    public char[][] grid;

    public CMap(String name, int size, String[] g)
    {
        mapName = name;
        mapSize = size;
        grid = new char[size][size];

        for (int i = 0; i < size; i++)
        {
            grid[i] = g[i].toCharArray();
        }
    }

    static class MapGenerator
    {
        public static CMap GenerateRandomMapForLeague(long seed, int league)
        {
            List<CMap> maps = new ArrayList<CMap>();
            Random random = new Random(seed);

            //Empty map
            maps.add(new CMap("Empty_15", 15, new String[]{
                    "...............",
                    ".00............",
                    "...............",
                    "...............",
                    "...............",
                    "...............",
                    "...............",
                    "...............",
                    "...............",
                    "...............",
                    "...............",
                    "...............",
                    "...............",
                    "............11.",
                    "..............."}));

            //Labyrinth
            maps.add(new CMap("Labyrinth_15", 15, new String[]{
                    ".......W.......",
                    ".00....W...W.W.",
                    "WWWWWW.W.WWW.WW",
                    "..W....W...W.W.",
                    "..W.WWWWW..W...",
                    "..W....W...WW..",
                    "WWW..W.W.W..WWW",
                    ".....W...W.....",
                    "WWW..W.W.W..WWW",
                    "..WW...W....W..",
                    "...W..WWWWW.W..",
                    ".W.W...W....W..",
                    "WW.WWW.W.WWWWWW",
                    ".W.W...W....11.",
                    ".......W......."}));

            //Square map
            maps.add(new CMap("Square_15", 15, new String[]{
                    "...............",
                    ".00............",
                    "...............",
                    "...WWWW.WWWW...",
                    "...W.......W...",
                    "...W.WWWWW.W...",
                    "...W.W...W.W...",
                    "...W...W...W...",
                    "...W.W...W.W...",
                    "...W.WWWWW.W...",
                    "...W.......W...",
                    "...WWWW.WWWW...",
                    "...............",
                    "............11.",
                    "..............."}));

            //NoTeleport
            maps.add(new CMap("No_Teleport_15", 15, new String[]{
                    "WWWWWWWWWWWWWWW",
                    "W00...........W",
                    "W.............W",
                    "W.............W",
                    "W.............W",
                    "W.............W",
                    "W.............W",
                    "W.............W",
                    "W.............W",
                    "W.............W",
                    "W.............W",
                    "W.............W",
                    "W.............W",
                    "W...........11W",
                    "WWWWWWWWWWWWWWW"}));

            return maps.get(random.nextInt(maps.size()));
        }
    }
}

