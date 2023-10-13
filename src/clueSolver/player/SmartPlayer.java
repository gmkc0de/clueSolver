package clueSolver.player;

import java.util.ArrayList;
import java.util.List;

import clueSolver.Card;
import clueSolver.Game;
import clueSolver.Guess;

public class SmartPlayer extends Player {

	public static String SMART_TYPE = "smart";
	public ArrayList<Card> notDisproven = new ArrayList<Card>();
	boolean isFirstRandom = true;
	boolean isScndRandom = false;

	public SmartPlayer(String name, Game g) {
		this.name = name;

		hand = new ArrayList<Card>();

		currentGame = g;
		isComputer = true;

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
		ArrayList<Card> cardsForGuess = new ArrayList<Card>();
		Guess smart = new Guess();
		smart.setMadeBy(this);

		List<Card> proven = currentGame.findProvenCards(this);
		smart.addAllCards(proven);

		List<String> unprovenTypes = smart.getMissingTypes();
		int numProven = 3 - unprovenTypes.size();

		switch (numProven) {
		case 0: {
			Card pick = pickFirstCard();
			smart.addCardToGuess(pick);
		}
		case 1: {
			Card first = smart.getNotNullCard();
			// smart either has no cards or getCards order is the problem
			Card pick = pickSecondCard(chooseDifferentType(first.getType()));
			smart.addCardToGuess(pick);
		}
		case 2: {
			String missingType = smart.getMissingTypes().get(0);
			Card pick = pickThirdCard(missingType);
			smart.addCardToGuess(pick);
		}
		}
		return smart;
	}

	// only used for situation where we have no proven cards yet!
	private Card pickFirstCard() {

		
		Card first = getRandomCardFromHand();
		String[] types = { "suspect", "room", "weapon" };
		
		// if card type is missing from our hand, get random card of that type
		for (String s : types) {
			if (!hasTypeInHand(s)) {
				first = currentGame.getRandomCardOfATypeFromDeck(s);
				break;
			}

		}

		// replace first with a proven card if there is one of this type
		if (currentGame.findProvenCardOfType(this, first.getType()) != null) {
			first = currentGame.findProvenCardOfType(this, first.getType());
			isFirstRandom = false;
		}

		for (int i = 0; i < 3; i++) {
			// replace the random card with a card of a type that has not been proven
			if (!isProvenType(types[i])) {
				first = currentGame.getRandomCardOfATypeFromDeck(types[i]);
				if (first != null) {
					// once we have an unproven type further looping is unnecessary
					isScndRandom = false;
					break;
				}
			}
		}

		return first;
	}

	private Card pickSecondCard(String scndType) {
		Card scnd;
		if (isFirstRandom) {
			// if first is random scnd should be a a card from hand with scndType
			scnd = getRandomCardFromHandWithType(scndType);
			// if hand does not have a card of scndType first try to find a proven card of
			// scndType
			if (scnd == null && currentGame.findProvenCardOfType(this, scndType) != null) {
				scnd = currentGame.findProvenCardOfType(this, scndType);
				return scnd;
			}
			// or, make a random guess from the deck instead
			else if (scnd == null) {
//				int counter = 0;
				isScndRandom = true;
				scnd = currentGame.getRandomCardOfATypeFromDeck(scndType);
				while (hasSeenCard(scnd)) {
					// continue picking a random card until we have one we have never seen
					scnd = currentGame.getRandomCardOfATypeFromDeck(scndType);
//					++counter;
//					if(counter > 21) {
//						System.out.println("break!");
//					}
				}

			}
		} else {
			// if first is not random second card should be instead
			// first check there is no proven card of type
			scnd = currentGame.getRandomCardOfATypeFromDeck(scndType);
			if (scnd == null) {
				scnd = currentGame.getRandomCardOfATypeFromDeck(scndType);
				while (hasSeenCard(scnd)) {
					// pick random cards till you find one we have never been shown
					scnd = currentGame.getRandomCardOfATypeFromDeck(scndType);
				}

			}

		}
		return scnd;
	}

	private Card pickThirdCard(String thirdType) {
		ArrayList<Card> unknownCards = currentGame.findUnknownCards(this);

		// if the second card was not random
		if (!isScndRandom) {
			// first check for a proven card
			Card third = currentGame.findProvenCardOfType(this, thirdType);
			if (third == null) {
				// if proven card is null us a random unknown card
				third = Game.getRandomCardFromListWithType(thirdType, unknownCards);
			}
			if (third == null) {
				// if third is still null there has been an error.
				// get a random card form deck to prevent a crash and note the error
				System.out.println(" ERROR no proven card no unknown cards");
				third = currentGame.getRandomCardOfATypeFromDeck(thirdType);
			}
			return third;
		} else {
			// if the second card was random the third card should come from the hand
			Card third = getRandomCardFromHandWithType(thirdType);

			// first check if there is a proven card of thirdType
			if (third == null && currentGame.findProvenCardOfType(this, thirdType) != null) {
				third = currentGame.findProvenCardOfType(this, thirdType);
			} else if (getRandomCardFromHandWithType(thirdType) != null) {
				// if there is no proven card get a card of thirdType form hand
				third = getRandomCardFromHandWithType(thirdType);

			} else {
				// if there is no card oftype in hand third will be a random card of type from
				// the deck
				third = currentGame.getRandomCardOfATypeFromDeck(thirdType);
			}
			return third;
		}

	}

	public String getType() {

		return SMART_TYPE;
	}

}
