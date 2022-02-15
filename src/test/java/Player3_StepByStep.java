import java.util.*;

class Player3_StepByStep {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int turn = 0;

        // game loop
        while (true) {
            int mapSize = in.nextInt(); // The size of the map
            if (in.hasNextLine()) {
                in.nextLine();
            }
            for (int i = 0; i < mapSize; i++) {
                String row = in.nextLine();
            }
            String myDirection = in.next();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            String myBonuses = in.nextLine();
            int mySize = in.nextInt();
            for (int i = 0; i < mySize; i++) {
                int bodyX = in.nextInt();
                int bodyY = in.nextInt();
            }
            String opponentDirection = in.next();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            String opponentBonuses = in.nextLine();
            int opponentSize = in.nextInt();
            for (int i = 0; i < opponentSize; i++) {
                int bodyX = in.nextInt();
                int bodyY = in.nextInt();
            }
            int validActionCount = in.nextInt();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            for (int i = 0; i < validActionCount; i++) {
                String validAction = in.nextLine();
                System.err.println(validAction);
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            List<String> Actions = new ArrayList<String>(Arrays.asList(
                    "LEFT",
                    "UP",
                    "UP",
                    "UP",
                    "DOWN REVERSE",
                    "DOWN",
                    "DOWN"));

            System.out.println(Actions.get(turn++ % Actions.size()));
        }
    }
}