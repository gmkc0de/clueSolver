package clueSolver.player;

import java.util.ArrayList;
import java.util.List;

import clueSolver.Card;
import clueSolver.Game;
import clueSolver.Guess;

public class SmartPlayer extends Player {

	public static String SMART_TYPE = "smart";
	public ArrayList<Card> notDisproven = new ArrayList<Card>();
	
	public SmartPlayer() {
		super();
	}
	
	public SmartPlayer(String name, Game g) {
		this.name = name;

		hand = new ArrayList<Card>();

		currentGame = g;
		isComputer = true;

	}

	@Override
	public Guess makeGuess() {
		/*
		 * / this player should always guess two cards from their own hand and one card
		 * they haven't guessed yet when a guess cannot be disproved this player should
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
		System.out.println(this.name+": num proven: "+numProven);
		Card secondPick = null;
		Card firstPick = null;
		boolean isFirstPickRandom = false;
		
		switch (numProven) {
		case 0: {
			firstPick = pickFirstCard();
			smart.addCardToGuess(firstPick);
			System.out.println(this.name+": case 0: "+firstPick.getName());
		}
		case 1: {
			
			boolean isFirstCardProven = firstPick == null;
			// if first pick is not a proven card and is not from hand then it must be random so isFirstPickRandom is true
			isFirstPickRandom = !isFirstCardProven && !hand.contains(firstPick);

			Card first = smart.getNotNullCard();
			secondPick = pickSecondCard(chooseDifferentType(first.getType()), isFirstPickRandom);
			smart.addCardToGuess(secondPick);
			System.out.println(this.name+": case 1: "+secondPick.getName());
		}
		case 2: {
			// bug: when card 1 and 2 are "unknown" and "from hand" does not pick final card from hand.
			// picks random card instead
			String missingType = smart.getMissingTypes().get(0); 
			//second will be random if numProven was equal to 1
			
			// if secondPick is null that means it is a proven card
			boolean isSecondCardProven = secondPick == null;
			// if second pick in not a proven card and is not from hand then it must be random so isSecondPickRandom is true
			boolean isSecondPickRandom = !isSecondCardProven && !hand.contains(secondPick);
			boolean isFirstOrSecondRandom = isSecondPickRandom || isFirstPickRandom;
			Card thirdPick = pickThirdCard(missingType, isFirstOrSecondRandom );
			smart.addCardToGuess(thirdPick);
			System.out.println(this.name+": case 2: "+thirdPick.getName());
			
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
				first = getUnseenCardOfAtypeFromDeck(s);
				break;
			}

		}

		return first;
	}

	private Card pickSecondCard(String scndType, boolean isFirstRandom) {
		Card scnd;
		if (isFirstRandom) {
			// if first is random:
			
			// check for a proven card of scndType
			if (currentGame.findProvenCardOfType(this, scndType) != null) {
				scnd = currentGame.findProvenCardOfType(this, scndType);
				return scnd;
			} else{
				// of no proven card, check for a card of type in hand
				scnd = getRandomCardFromHandWithType(scndType);
			}
			
			// if no proven card and no card of type in hand, make a random guess from the deck instead
			if (scnd == null) {
				scnd = getUnseenCardOfAtypeFromDeck(scndType);
				while (hasSeenCard(scnd)) {
					// continue picking a random card until we have one we have never seen
					scnd = getUnseenCardOfAtypeFromDeck(scndType);
				}

			}
		} else {
			// if first is not random:
			//second card should be random instead
			// first check there is no proven card of type
			if (currentGame.findProvenCardOfType(this, scndType) != null) {
				scnd = currentGame.findProvenCardOfType(this, scndType);
				return scnd;
				}else
			    // if no proven card, select a random unseen card from deck
				scnd = getUnseenCardOfAtypeFromDeck(scndType);
				while (hasSeenCard(scnd)) {
					// pick random cards till you find one we have never been shown
					scnd = getUnseenCardOfAtypeFromDeck(scndType);
				}

		}
		return scnd;
	}

	private Card pickThirdCard(String thirdType, boolean isFirstOrSecondRandom) {
		List<Card> unknownCards = currentGame.findUnknownCards(this); // no bug

		// if the first or second card was not random:
		if (!isFirstOrSecondRandom) {
			// first check for a proven card
			Card third = currentGame.findProvenCardOfType(this, thirdType);
			if (third == null) {
				// if proven card is null us a random unknown card
				third = Game.getRandomCardFromListWithType(thirdType, unknownCards);
			}
			if (third == null) {
				// if third is still null there has been an error.
				// get a random card form deck to prevent a crash and note the error
				System.out.println(" ERROR in pickThirdCard no proven card no unknown cards");
				third = getUnseenCardOfAtypeFromDeck(thirdType);
			}
			return third;
		} else {
			// if the second card or the first card was random the third card should come from the hand
			Card third;

			// first check if there is a proven card of thirdType
			if (currentGame.findProvenCardOfType(this, thirdType) != null) {
				third = currentGame.findProvenCardOfType(this, thirdType);
			} else if (getRandomCardFromHandWithType(thirdType) != null) {
				// if there is no proven card get a card of thirdType form hand
				third = getRandomCardFromHandWithType(thirdType);

			} else {
				// if there is no card of type in hand third will be a random card of type from the deck
				third = getUnseenCardOfAtypeFromDeck(thirdType);
			}
			return third;
		}

	}

	public String getType() {

		return SMART_TYPE;
	}

}
