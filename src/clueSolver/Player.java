package clueSolver;

import java.util.ArrayList;

public class Player implements Comparable<Player> {
	private String name;
	private ArrayList<Guess> guesses;
	private ArrayList<Card> hand;
	private Game currentGame;
	private boolean isComputer;
	public ArrayList<Guess> notePad;
	private int turnOrder;

	public Player(String name, Game g) {
		this.name = name;
		guesses = new ArrayList<Guess>();
		hand = new ArrayList<Card>();
		notePad = new ArrayList<Guess>();
		currentGame = g;
		isComputer = true;
		
	}

	public void addToHand(Card c) {
		hand.add(c);
	}
	public boolean isComputer() {
		return isComputer;
	}
	
	public ArrayList<Guess> getGuessList() {
		return guesses;
	}

	public String getName() {
		return name;
	}
	
	public int getTurnOrder() {
		return turnOrder;
	}
	
	public ArrayList<Guess> getNotePad() {
		return notePad;
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
			L.i(">>you have disproved "+ g.getGuesser().getName()+"'s guess with the "+ canDisprove.get(0).getName()+" card"+"<<");
			return canDisprove.get(0);
		}else {
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

	

	
	
	
}
