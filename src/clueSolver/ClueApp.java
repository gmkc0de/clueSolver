package clueSolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import clueSolver.db.ReportApp;

public class ClueApp {
	// this app does not do any round by round reporting or recording
	public static void main(String[] args) throws Exception {
		
		L.CURRENT_LEVEL = L.ERROR;
		//random number, that when you add 3 your range will be min: 3, max 6.
		int numberOfGames = 50000;
		
		resetDatabase();
		
		LocalDateTime start = LocalDateTime.now();
		
		for (int i = 0; i < numberOfGames; i++) {
			if(i % 30 == 0) {
				System.out.print(i +" ");			
			}
			if(i > 0 && i % 1000 == 0) {
				Duration duration = Duration.between(start, LocalDateTime.now());
				System.out.println();
				System.out.println("time so far: "+duration);
				
				
			}
			int numPlayers = ((int)(Math.random()*4))+3;
			Game game = Game.createTestGame(numPlayers);
			game.dealCards();
			
			while (!game.hasWinningGuess()) {
				game.takeTurn();
			}
			game.save();
			game.cleanup();
		}
		
		ReportApp.main(args);
		

	}
	
	public static int resetDatabase() throws IOException, InterruptedException {
		String cmd = "/Users/Kloster/eclipse-workspace/clueSolver/db/recreateDb.sh";
		System.out.println(cmd);
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String result = in.readLine();
		while(result != null) {
			System.out.println(result);
			result = in.readLine();
		}
		
		return p.exitValue();

	}

}
