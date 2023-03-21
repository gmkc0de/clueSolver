package clueSolver.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clueSolver.Card;
import clueSolver.Game;
import clueSolver.Guess;

public class SmartPlayer extends Player {
	
	public static String SMART_TYPE = "smart";
	
	
	public SmartPlayer(String name, Game g) {
		this.name = name;
		
		hand = new ArrayList<Card>();
		
		currentGame = g;
		isComputer = true;
		
	}
	
	@Override
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
	public Guess inDevmakeGuess() {
		// TODO write a smart player make guess
		/*/
		 * this player should always guess two cards from their own hand and one card they havent guessed yet
		 * when a guess cannot be disproved this player should note the card that that could not be disproven and avoid guessing it again
		 * when they have three cards that could not be disproven they guess those three cards 
		 */
		
		Guess smart = null;
		//choose any card in hand
		Card  first = getRandomCardFromHand();
		
		// choose a second a card of a different type
		Card  scnd = getRandomCardFromHandWithoutType(first.getType());
		//
		if(scnd == null) {
			
			
		}
		String type  = findDifferentType(first.getType(),scnd.getType());

		ArrayList<Card> unknown = currentGame.findUnknownCards(this);
		//select the third card in the guess from the only remaining options
		Card third = getRandomCardFromListWithType(type, unknown);
		if(third == null) {
			
			
		}
		
		return null;
	}
	
	private Card getRandomCardFromListWithType(String type, ArrayList<Card> cards) {
		Card c  = cards.get(((int) (Math.random() * cards.size())));
		
		return c;
		
	}

	public String getType() {

		return SMART_TYPE;
	}

	
	
}
