import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
	public static void main(String[] args) {

		MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
		gameRunner.addAgent(Player1.class);
		gameRunner.addAgent(Player3_StepByStep.class);

		gameRunner.setLeagueLevel(1);
		gameRunner.start();
	}
}
