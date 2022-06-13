package clueSolver;

import java.util.ArrayList;

public class Player {
	private String name;
	private ArrayList<Guess> guesses;
	private ArrayList<Card> hand;
	private Game currentGame;
	private boolean isComputer;
	public ArrayList<Guess> notePad;

	public Player(String name, Game g) {
		this.name = name;
		guesses = new ArrayList<Guess>();
		hand = new ArrayList<Card>();
		notePad = new ArrayList<Guess>();
		currentGame = g;
		isComputer = true;
	}

	public ArrayList<Guess> getGuessList() {
		return guesses;
	}

	public String getName() {
		return name;
	}
	public boolean isComputer() {
		return isComputer;
	}

	public ArrayList<Card> getHandList() {
		return hand;
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
		// TODO why does it not recognize player??
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
			System.out.println(">>you have disproved "+ g.getGuesser().getName()+"'s guess with the "+ canDisprove.get(0).getName()+" card"+"<<");
			return canDisprove.get(0);
		}else {
			System.out.println(">>you cannot disprove this guess<<");
			return null;
		}
	}
}
