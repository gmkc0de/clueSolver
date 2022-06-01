package clueSolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class App {
//TODO: All get input methods should deal with incorrect input and allow player to try agian instead of requiroing the code to restart
	public static void main(String[] args) throws Exception {
		
		//Game game = new Game();
		Game game = Game.createTestGame();
		//game.printAllCards();
		//game.addPlayers();
		game.printPlayers(); 
		//and the clueSolver will know the card type (saves user typing card type)
		//hi dad
		//game.askUserForHand();
		int count = 0;
		while(count < 5) {
			
			//Guess g = game.getGuessFromUser();
			//game.addGuess(g);
			//System.out.println(g.toString());
			game.autoGuess(1);
			System.out.println(game.getGuessList().get(game.getGuessList().size() -1));
			
			game.findPLayerGuesses(game.getPlayers().get(0));
			count ++;
			if(count > 0) {
				System.out.println("my clues:");
				ArrayList<Card>myClues = game.findMyClues();
				for(Card c: myClues) {
					System.out.println(c.getName());
				}
				System.out.println(">>----------<<");
			}
			System.out.println("unknow sus: ");
			ArrayList<Card> test = game.findUnknownSuspects();
			for(Card c: test) {
				System.out.println(c.getName());
			}
			System.out.println(">>----------<<");
			
		}
		
		//1)
		//Ask user the size of their hand
		
		
	
		//2) Add to the "guess" class, the player and card that were provided to "disprove" the guess. "disprovePlayer", "disproveCard"
		//2a) Add to getGuessFromUser() the ability to know who disproved the guess. IF it is our guess, we also will know the card shown
						
		//3) 
		//After the player has their hand, create a loop that plays the game
		//Each loop will ask the next player for a guess, and record the guess 
		
//		// get guess and print it
//		Guess test = getGuessFromUser(players);
//		players.get(0).getGuessList().add(test);
//		Guess g = players.get(0).getGuessList().get(0);
//		System.out.println(g.getSuspect() + ", " + g.getWeapon() + ", " + g.getRoom());

		//deal all Cards and print each player hand
	
//		dealCards(players, cardsList, secretCards);
//		for(Player p: players) {
//			System.out.println(p.getName());
//			for (Card dealC: p.getHandList()) {
//				System.out.println(dealC.getName());
//			}
//			
//		}
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
	

	public void dealCards(ArrayList<Player> players, List<Card> c, Card[] s) {
		// deal the three secret cards 
		String whatType = "suspect";
		String nextType = "weapon";
		ArrayList<Card> cards = new ArrayList<Card>(c);
		for(int i = 0; i < 3; i++) {
			if(cards.size()!= 0){
				int num = (int)Math.random() * cards.size();
				if(cards.get(num).getType() == whatType) {
					s[i] = cards.get(num);
					cards.remove(num);
					whatType = nextType;
					nextType = "room";
				}
			}
		}
		// deal to players
		while(cards.size() != 0) {
			for(Player p: players) {
				if(cards.size()!= 0){
				int num = (int)Math.random() * cards.size();
				p.getHandList().add(cards.get(num));
				cards.remove(num);
				}
			}
		}	
	}
}
