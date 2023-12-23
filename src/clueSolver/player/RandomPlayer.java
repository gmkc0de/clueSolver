package clueSolver.player;

import java.util.ArrayList;
import java.util.List;

import clueSolver.Card;
import clueSolver.Game;
import clueSolver.Guess;

public class RandomPlayer extends Player {
	
	public String RANDOM_TYPE = "random";
	
	public RandomPlayer() {
		super();
	}
	public RandomPlayer(String name, Game g) {
		this.name = name;
		
		hand = new ArrayList<Card>();
	
		currentGame = g;
		isComputer = true;
		
	}
	
	public Guess makeGuess() {
		Guess g = null;
		
		List<Card> suspects = currentGame.findUnknownSuspects(this);
		List<Card> weapons = currentGame.findUnknownWeapons(this);
		List<Card> rooms = currentGame.findUnknownRooms(this);
		List<Card> guessCards = new ArrayList<Card>();
		
		//ArrayList<Player> possibleDisprovers = allPlayersButThis(guesser);
		// calc all random cards
		Card s = null;
		Card w = null;
		Card r = null;
		try {
			s = suspects.get((int) (Math.random() * suspects.size()));
			guessCards.add(s);
			w = weapons.get((int) (Math.random() * weapons.size()));
			guessCards.add(w);
			r = rooms.get((int) (Math.random() * rooms.size()));
			guessCards.add(r);
			
			g = new Guess(this, s, r, w, null, null);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			throw e;
		}
		
		return g ;
	}
	
	public String getType() {

		return RANDOM_TYPE;
	}
}
