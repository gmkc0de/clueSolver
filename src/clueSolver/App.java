package clueSolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class App {
//TODO: >long term<All get input methods should deal with incorrect input and allow player to try again instead of requiring the code to restart
	public static void main(String[] args) throws Exception {

		// Game game = new Game();
		Game game = Game.createTestGame();
		game.dealCards();
		// game.printAllCards();
		// game.addPlayers();
		game.printPlayers();
		int count = 0;
		while (!game.hasWinningGuess()) {
			count ++;
			game.takeTurn();

			System.out.println(game.getGuessList().get(game.getGuessList().size() - 1));

			game.findPLayerGuesses(game.getPlayers().get(0));

			System.out.println("my clues:");
			ArrayList<Card> myClues = game.findPlayerClues(game.getMyPlayer());
			for (Card c : myClues) {
				System.out.println(c.getName());
			}
			System.out.println(">>----------<<");

			System.out.println("unknow sus: ");
			ArrayList<Card> test = game.findUnknownSuspects(game.getMyPlayer());
			for (Card c : test) {
				System.out.println(c.getName());
			}
			System.out.println(">>----------<<");
			
		}
		System.out.println(">>we have  a winner! " + game.findWinningGuess()+" after "+count +" turns<<");
		

	}

	// METHOD LAND

	public static int getIntFromUser(String question) throws Exception {

		BufferedReader in2 = new BufferedReader(new InputStreamReader(System.in));
		System.out.println(question);
		String num = in2.readLine();
		int myNum = Integer.valueOf(num);
		return myNum;

	}

	public static String getStringInputFromUser(String question) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println(question);
		String name = in.readLine();
		return name;

	}

}
