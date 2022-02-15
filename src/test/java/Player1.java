import java.util.*;

class Player1 {
    private static class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class PositionWithOrigin extends Position
    {
        public PositionWithOrigin(int x, int y, int f)
        {
            super(x,y);
            from = f;
        }

        int from;
    }

    public static class CMap
    {
        public CMap(int s)
        {
            size = s;
            cases = new int[size][size];
        }
        public void AddWall(int x, int y)
        {
            cases[y][x] = 99;
        }

        private List<PositionWithOrigin> GetAdjacents(Position p)
        {
            List<PositionWithOrigin> result = new ArrayList<PositionWithOrigin>();
            if (p.x > 0)
            {
                result.add(new PositionWithOrigin(p.x-1, p.y,1));
            }
            else
            {
                result.add(new PositionWithOrigin(size-1, p.y,1));
            }
            if (p.y > 0)
            {
                result.add(new PositionWithOrigin(p.x, p.y-1, 2));
            }
            else
            {
                result.add(new PositionWithOrigin(p.x, size-1,2));
            }
            if (p.x < size-1)
            {
                result.add(new PositionWithOrigin(p.x+1, p.y, 3));
            }
            else
            {
                result.add(new PositionWithOrigin(0, p.y,3));
            }
            if (p.y < size-1)
            {
                result.add(new PositionWithOrigin(p.x, p.y+1, 0));
            }
            else
            {
                result.add(new PositionWithOrigin(p.x, 0, 0));
            }
            return result;
        }

        private void FillAllCasesFrom(Position p)
        {
            Queue<PositionWithOrigin> toDo = new LinkedList<>();
            cases[p.y][p.x] = 1;
            toDo.add(new PositionWithOrigin(p.x,p.y,0));
            while (!toDo.isEmpty())
            {
                PositionWithOrigin current = toDo.remove();
                //System.err.println(String.format("evaluating %d %d (remaining = %d)",current.x,current.y, toDo.size()));
                int value = cases[current.y][current.x];
                for (PositionWithOrigin adj : GetAdjacents(current))
                {
                    if (cases[adj.y][adj.x] == 0)
                    {
                        cases[adj.y][adj.x] = value+1;
                        toDo.add(adj);
                    }
                }
            }
        }

        public int GetNextMoveGoingFromTo(Position f, Position to)
        {
            FillAllCasesFrom(f);
            //If I can't reach destination, find first correct move
            //System.err.println(cases[to.y][to.x]);
            if (cases[to.y][to.x] < 1)
            {
                System.err.println(String.format("recovery"));
                for (PositionWithOrigin p : GetAdjacents(f))
                {
                    System.err.println(cases[p.y][p.x]);
                    if (cases[p.y][p.x] != 99)
                    {
                        System.err.println(String.format("default solution"));
                        return (p.from + 2) % 4;
                    }
                }
            }
            //ReverseFind
            Position next = to;
            int direction = 0;
            while (cases[next.y][next.x] > 1)
            {
                int value = cases[next.y][next.x];
                for(PositionWithOrigin adj : GetAdjacents(next))
                {
                    if (cases[adj.y][adj.x] < value)
                    {
                        direction = adj.from;
                        next = adj;
                        //System.err.println(String.format("%d %d : value = %d : direction = %d", next.y, next.x, value, direction));
                        break;
                    }
                }
            }
            //System.err.println(String.format("found a way"));
            return direction;
        }

        int size;
        int[][] cases;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        while (true) {
            int mapSize = in.nextInt();
            in.nextLine();
            System.err.println(mapSize);
            CMap theMap = new CMap(mapSize);
            Position apple = new Position(0,0);
            for (int i = 0; i < mapSize; i++)
            {
                String line = in.nextLine();
                System.err.println(line);
                for (int j = 0; j < mapSize; j++)
                {
                    switch (line.toCharArray()[j])
                    {
                        case '#':
                        case '0':
                        case '1':
                            theMap.AddWall(j,i);
                            break;
                        case 'A':
                            apple = new Position(j, i);
                        case '.':
                        default:
                            break;
                    }
                }
            }

            String myDirection = in.nextLine();
            System.err.println(myDirection);
            String myBonuses = in.next();
            System.err.println(myBonuses);
            int myLength = in.nextInt();
            System.err.println(myLength);
            Position[] myBody = new Position[myLength];
            for (int i = 0; i < myLength; i++)
            {
                int x = in.nextInt();
                int y = in.nextInt();
                System.err.println(String.format("%d %d",x,y));
                myBody[i] = new Position(x,y);
            }

            in.nextLine();
            String hisDirection = in.nextLine();
            String hisBonuses = in.next();
            int hisLength = in.nextInt();
            System.err.println(hisDirection);
            System.err.println(hisBonuses);
            System.err.println(hisLength);
            for (int i = 0; i < hisLength; i++)
            {
                int x = in.nextInt();
                int y = in.nextInt();
                System.err.println(String.format("%d %d",x,y));
            }

            int validActionCount = in.nextInt();
            String[] validActions = new String[validActionCount];
            System.err.println(validActionCount);
            in.nextLine();
            for (int i = 0; i < validActionCount; i++) {
                String action = in.nextLine();
                System.err.println(action);
                validActions[i] = action;
            }

            //Output
            String[] directions = { "UP", "RIGHT", "DOWN", "LEFT" };

            Position myPos = myBody[0];
            System.err.println(String.format("%d %d",myPos.x,myPos.y));
            System.err.println(String.format("%d %d",apple.x,apple.y));
            int d = theMap.GetNextMoveGoingFromTo(myPos, apple);
            String action = directions[d];

            System.out.println(action);
        }
    }
}
