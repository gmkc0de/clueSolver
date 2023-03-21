package clueSolver.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clueSolver.Card;
import clueSolver.Game;
import clueSolver.Guess;
import clueSolver.L;

public abstract class Player implements Comparable<Player> {

	protected String name;

	protected ArrayList<Card> hand;
	protected Game currentGame;
	protected boolean isComputer;
	protected int turnOrder;

	public void addToHand(Card c) {
		hand.add(c);
	}

	public boolean isComputer() {
		return isComputer;
	}

	public String getName() {
		return name;
	}

	public int getTurnOrder() {
		return turnOrder;
	}

	public Game getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(Game newGame) {
		this.currentGame = newGame;
	}

	public void printHand() {
		for (Card c : hand) {
			System.out.println(c.getName() + "-" + c.getType());
		}
	}

	public boolean equals(Object o) {
		if (o instanceof Player) {
			Player playerToTest = (Player) o;
			return playerToTest.getName().equals(this.getName());
		}
		return false;
	}

	public boolean hasSeenCard(Card c) {
		ArrayList<Card> theirClues = currentGame.findPlayerClues(this);
		return theirClues.contains(c);
	}

	public ArrayList<Card> findDisprovingCards(Guess g) {
		ArrayList<Card> canDisprove = new ArrayList<Card>();
		ArrayList<Card> guessCards = new ArrayList<Card>();
		guessCards.add(g.getRoom());
		guessCards.add(g.getSuspect());
		guessCards.add(g.getWeapon());
		ArrayList<Card> myClues = currentGame.findPlayerClues(this);
		for (Card c : guessCards) {
			if (myClues.contains(c)) {
				canDisprove.add(c);
			}
		}

		return canDisprove;
	}

	public Card disproveGuess(Guess g) {
		ArrayList<Card> canDisprove = findDisprovingCards(g);
		if (canDisprove.size() > 0) {
			for (Card c : canDisprove) {
				if (g.getGuesser().hasSeenCard(c)) {
					return c;
				}
			}
			L.i(">>you have disproved " + g.getGuesser().getName() + "'s guess with the " + canDisprove.get(0).getName()
					+ " card" + "<<");
			return canDisprove.get(0);
		} else {
			L.i(">>you cannot disprove this guess<<");
			return null;
		}
	}

	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(Player o) {
		return this.getName().compareTo(o.getName());

	}

	public void setOrder(int order) {
		turnOrder = order;

	}

	public int getOrder() {
		return turnOrder;

	}

	public ArrayList<Card> getHand() {
		return hand;
	}

	public void setHand(ArrayList<Card> hand) {
		this.hand = hand;
	}
	
	public Card getRandomCardFromDeck() {
		Card[] allCards = currentGame.getAllCards();
		return allCards[((int) (Math.random() * allCards.length))];
		
	}
	public Card getRandomCardOfATypeFromDeck(String type) {
		Card[] allCards = currentGame.getAllCards();
		
		
	}

	public Card getRandomCardFromHandWithType(String type) {
		for (int i = 0; i < hand.size(); i++) {
			Card c = hand.get(((int) (Math.random() * hand.size())));
			if (c.getType() == type) {
				return c;
			}
		}
		// get a card, return null if needed
		return null;
	}

	public Card getRandomCardFromHandWithoutType(String type) {
		// get a card, return null if needed
		for (int i = 0; i < hand.size(); i++) {
			Card c = hand.get(((int) (Math.random() * hand.size())));
			if (c.getType() != type) {
				return c;
			}
		}
		return null;
	}

	public Card getRandomCardFromHand() {
		return hand.get((int) (Math.random() * hand.size()));
	}

	protected String findDifferentType(String type1, String type2) {

		List<String> types = Arrays.asList("room", "weapon", "suspect");
		for (int i = 0; i < 3; i++) {
			if (types.get(i) != type1 && types.get(i) != type2) {
				return types.get(i);
			}
		}
		return null;
	}

	abstract public Guess makeGuess();

	abstract public String getType();

}
