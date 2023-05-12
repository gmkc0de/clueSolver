package clueSolver.player;

import java.util.ArrayList;

import clueSolver.Card;
import clueSolver.Game;
import clueSolver.Guess;

public class SmartPlayer extends Player {

	public static String SMART_TYPE = "smart";
	public ArrayList<Card> notDisproven = new ArrayList<Card>();

	public SmartPlayer(String name, Game g) {
		this.name = name;

		hand = new ArrayList<Card>();

		currentGame = g;
		isComputer = true;

	}

	public Guess OldMakeGuess() {
		Guess g = null;

		ArrayList<Card> suspects = currentGame.findUnknownSuspects(this);
		ArrayList<Card> weapons = currentGame.findUnknownWeapons(this);
		ArrayList<Card> rooms = currentGame.findUnknownRooms(this);
		ArrayList<Card> guessCards = new ArrayList<Card>();

		// ArrayList<Player> possibleDisprovers = allPlayersButThis(guesser);
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

		return g;
	}

	@Override
	public Guess makeGuess() {
		// TODO write a smart player make guess
		/*
		 * / this player should always guess two cards from their own hand and one card
		 * they havent guessed yet when a guess cannot be disproved this player should
		 * note the card that that could not be disproven and avoid guessing it again
		 * when they have three cards that could not be disproven they guess those three
		 * cards
		 */
		Guess smart = new Guess();
		smart.setMadeBy(this);
		
		String[] types = {"suspect","room","weapon"};

		ArrayList<Card> cardsForGuess = new ArrayList<Card>();
		// choose any card in hand
		Card first = getRandomCardFromHand();
		
		//replace it with a proven card if there is one of this type
		if (currentGame.findProvenCardOfType(this, first.getType()) != null) {
			first = currentGame.findProvenCardOfType(this, first.getType());
		}
		cardsForGuess.add(first);
		
		String scndType = chooseDifferentType(first.getType());
		Card scnd = getRandomCardFromHandWithType(scndType);

		//if player has all cards of same type, second card will be null
		// so, if we have an already proven card use that instead
		if (scnd == null && currentGame.findProvenCardOfType(this, scndType) != null) {
			//error here?
			scnd = currentGame.findProvenCardOfType(this, scndType);
		}
		if(scnd == null) {
			//or, make a random guess from the deck instead
			int counter = 0;
			scnd = currentGame.getRandomCardOfATypeFromDeck(scndType);
			while(hasSeenCard(scnd)) {
				scnd = currentGame.getRandomCardOfATypeFromDeck(scndType);
				++counter;
				if(counter > 21) {
					System.out.println("break!");
				}
			}
			
			
			//TODO: make sure we have not already seen this card 
			//(i.e. it's already disproven)
			
		}
		cardsForGuess.add(scnd);

		String type = findDifferentType(first.getType(), scnd.getType());
		ArrayList<Card> unknownCards = currentGame.findUnknownCards(this);
		// select the third card in the guess from the only remaining options
		Card third = Game.getRandomCardFromListWithType(type, unknownCards);
		if (third == null && currentGame.findProvenCardOfType(this, type) != null) {
			third = currentGame.findProvenCardOfType(this, type);
		}else if (third == null) {
			third = currentGame.getRandomCardOfATypeFromDeck(type);
		}
		cardsForGuess.add(third);
		// fill in the guess
		
		smart.addCardToGuess(cardsForGuess.get(0));
		smart.addCardToGuess(cardsForGuess.get(1));
		smart.addCardToGuess(cardsForGuess.get(2));
		
		
	
		return smart;
	}

	

	public String getType() {

		return SMART_TYPE;
	}

}
