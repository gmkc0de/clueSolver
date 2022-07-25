package clueSolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class App {
//TODO: >long term<All get input methods should deal with incorrect input and allow player to try again instead of requiring the code to restart
	public static void main(String[] args) throws Exception {
		int longest = Integer.MIN_VALUE;
		int shortest = Integer.MAX_VALUE;
		double average = 0;
		double averagePerPlayer = 0;
		// create and play 100 games
		int numberOfGames = 5000;
		for (int i = 0; i < numberOfGames; i++) {
			System.out.println("Game Number: "+i);
			Game game = Game.createTestGame(2);
			game.dealCards();
			// game.printAllCards();
			// game.addPlayers();
			game.printPlayers();
			int count = 0;
			System.out.println(i);
			while (!game.hasWinningGuess()) {
				System.out.println(count); 
				count++;
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
			average += count;
			if(count > longest) {
				longest = count;
			}
			if(count< shortest) {
				shortest = count;
			}
			System.out.println(">>we have  a winner! " + game.findWinningGuess() + " after " + count + " turns<<");

		}
		//TODO: add each players personal average  guesses
		System.out.println("longest game: " +longest+ " turns, shortest game: " +shortest+" turns, avdrage number of turns: "+ average/numberOfGames);
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
