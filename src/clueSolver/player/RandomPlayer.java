package clueSolver.player;

import java.util.ArrayList;

import clueSolver.Card;
import clueSolver.Game;
import clueSolver.Guess;

public class RandomPlayer extends Player {
	
	public RandomPlayer(String name, Game g) {
		this.name = name;
		guesses = new ArrayList<Guess>();
		hand = new ArrayList<Card>();
		notePad = new ArrayList<Guess>();
		currentGame = g;
		isComputer = true;
		
	}
	
	public Guess makeGuess() {
		Guess g = null;
		
		ArrayList<Card> suspects = currentGame.findUnknownSuspects(this);
		ArrayList<Card> weapons = currentGame.findUnknownWeapons(this);
		ArrayList<Card> rooms = currentGame.findUnknownRooms(this);
		ArrayList<Card> guessCards = new ArrayList<Card>();
		
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
}
