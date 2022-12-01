package clueSolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class App {
//TODO: >long term<All get input methods should deal with incorrect input and allow player to try again instead of requiring the code to restart
	public static void main(String[] args) throws Exception {
		int longest = Integer.MIN_VALUE;
		int shortest = Integer.MAX_VALUE;
		double average = 0;
		int winnerGuessAverage = 0;
		double mostToWin = Integer.MIN_VALUE;
		int numPlayers = 5;
		

		ArrayList<Player> allWinners = new ArrayList<Player>();
		Map<Player, Integer> winners = new HashMap<Player, Integer>();
		// create and play 5000 games
		int numberOfGames = 5000;
		for (int i = 0; i < numberOfGames; i++) {
			System.out.println("Game Number: " + i);
			Game game = Game.createTestGame(numPlayers);
			game.dealCards();
			// game.printAllCards();
			// game.addPlayers();
			game.printPlayers();
			int round = 0;
			System.out.println(i);
			while (!game.hasWinningGuess()) {
				System.out.println(round);
				round++;
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
			// adding to variable
			if (mostToWin < ((double) round) / numPlayers) {
				mostToWin = ((double) round) / numPlayers;
			}
			Player winner = game.findWinningGuess().getGuesser();
			allWinners.add(winner);
			Integer wins = winners.get(winner);
			if (wins == null) {
				winners.put(winner, 1);
			} else {
				winners.put(winner, ++wins);
			}

			// List (Linked List, ArrayList)
			// Array ^^
			// Map or Dictionary or HashMap
			// key,value pair
			// orange -> a fruit that grows in florida
			// Set -> a collection of things (unordered, without duplicates...usually)
			// Tree

			average += round;
			if (round > longest) {
				longest = round;
			}
			if (round < shortest) {
				shortest = round;
			}
			winnerGuessAverage += game.findPLayerGuesses(game.findWinningGuess().getGuesser()).size();
			System.out.println(">>we have  a winner! " + game.findWinningGuess() + " after " + round + " rounds<<");

			//before we start the next game, close out this game
			game.cleanup();
			
		}

		ArrayList<Player> winnersArray = new ArrayList<Player>(winners.keySet());
		// TODO: sort winners here
		Collections.sort(winnersArray);
		// winnersArray.sort();
		for (Player p : winnersArray) {
			NumberFormat formatter = new DecimalFormat("#0.00");

			double w = winners.get(p);

			double percent = ((double) (w / numberOfGames)) * 100;
		
			String percentString = formatter.format(percent);
			System.out.println(p.getName() + " won " + w + " times. which is " + percentString + "% of the time");

		}
		NumberFormat formatter = new DecimalFormat("#0.00");
		double firstsPlayerAdvantage = (winners.get(winnersArray.get(0))/(double)numPlayers)- (winners.get(winnersArray.get(1))/(double)numPlayers);
		String advString = formatter.format(firstsPlayerAdvantage);
		System.out.println(">>the first player has an advantage over the others by " + advString);
		// TODO: add each players personal average guesses
		System.out.println(">>longest game: " + longest + " turns, shortest game: " + shortest
				+ " turns, average number of turns: " + average / numberOfGames);
		System.out.println(">>average num guesses for winner " + winnerGuessAverage / numberOfGames);
		System.out.println(">>the longest it took to win was " + mostToWin + " turns");
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
