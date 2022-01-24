import java.util.Scanner;

class Player {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        while (true) {
            int mapSize = in.nextInt();
            in.nextLine();
            System.err.println(mapSize);
            for (int i = 0; i < mapSize; i++)
            {
            	String line = in.nextLine();
                System.err.println(line);
            }
            String myDirection = in.nextLine();
            System.err.println(myDirection);
            String myBonuses = in.next();
            System.err.println(myBonuses);
            int myLength = in.nextInt();
            System.err.println(myLength);
            for (int i = 0; i < myLength; i++)
            {
            	int x = in.nextInt();
            	int y = in.nextInt();
                System.err.println(String.format("%d %d",x,y));
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
            System.err.println(validActionCount);
            for (int i = 0; i < validActionCount; i++) {
                String action = in.next();
                System.err.println(action);
            }

            System.out.println("UP");
        }
    }
}
