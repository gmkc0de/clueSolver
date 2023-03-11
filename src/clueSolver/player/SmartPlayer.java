package clueSolver.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clueSolver.Card;
import clueSolver.Game;
import clueSolver.Guess;

public class SmartPlayer extends Player {
	
	public SmartPlayer(String name, Game g) {
		this.name = name;
		
		hand = new ArrayList<Card>();
		
		currentGame = g;
		isComputer = true;
		
	}
	
	@Override
	public Guess makeGuess() {
		// TODO write a smart player make guess
		/*/
		 * this player should always guess two cards from their own hand and one card they havent guessed yet
		 * when a guess cannot be disproved this player should note the card that that could not be disproven and avoid guessing it again
		 * when they have three cards that could not be disproven they guess those three cards 
		 */
		
		Guess smart = null;
		//choose any card in hand
		Card  rand = hand.get((int)(Math.random() * hand.size()));
		
		// choose a second a card of a different type
		Card  scnd = getRandomCardFromHandWithoutType(rand.getType());
		//
	
		String type  = findDifferentType(rand.getType(),scnd.getType());


		
		//
		ArrayList<Card> unknown = currentGame.findUnknownCards(this);
		//unknown.getRandomCardFromHandWithType(type);
		
		return null;
	}

	

	
	
	
}
