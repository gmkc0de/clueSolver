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
	// this method gets all the clues(cards the player knows arent the secret cards)
	// and removes the cards in this players hand from those clues
	// then if card c is still on the clues list that means this player has seen it
	public boolean hasSeenCard(Card c) {
		//findPlayerClues returns all the cards in [this] players hand as well as every discard they have seen
		if(hand.contains(c)) {
			return true;
		}
		ArrayList<Card> clues = currentGame.findPlayerClues(this);
		for(int i = 0; i < clues.size();i++ ) {
			if(clues.get(i).isOnList(hand)) {
				clues.remove(i);
				i--;
			}

		}
		// true if c is already a clue false if it is not
		return clues.contains(c);
	}

	public ArrayList<Card> findDisprovingCards(Guess g) {
		ArrayList<Card> canDisprove = new ArrayList<Card>();
		ArrayList<Card> guessCards = new ArrayList<Card>();
		guessCards.add(g.getRoom());
		guessCards.add(g.getSuspect());
		guessCards.add(g.getWeapon());		
		
		for (Card c : guessCards) {
			if (hand.contains(c)) {
				canDisprove.add(c);
			}
		}
		// returns a list 
		return canDisprove;
	}

	public Card disproveGuess(Guess g) {
		//this is a list of all the cards in the players hand and all the discards theyve seen 
		ArrayList<Card> canDisprove = findDisprovingCards(g);
		if (canDisprove.size() > 0) {
			for (Card c : canDisprove) {
				// if the player has a card the guesser has already seen they will show that one
				if (this.haveAlreadyShownCardTo(g.getGuesser(),c)) {
					return c;
				}
			}
			
			L.i(">>"+name+" has disproved " + g.getGuesser().getName() + "'s guess with the " + canDisprove.get(0).getName()
					+ " card" + "<<");
			// if the player doesn't have an already shown card they return the first one on the list
			return canDisprove.get(0);
		} else {
			L.i(">>"+name+" cannot disprove this guess<<");
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

	public String chooseDifferentType(String type) {
		
		ArrayList<String> choices = new ArrayList<String>();
		choices.add("room");
		choices.add("weapon");
		choices.add("suspect");
		for (int i = 0; i < choices.size(); ++i) {
			String s = choices.get(i);
			if (s.equals(type)) {
				--i;
				choices.remove(s);
			}
		}
		return choices.get(((int) (Math.random() * choices.size())));
	}

	public List<Card> getCardsFromHandOfType(String type){
		
		return hand.stream()
		.filter(c -> c.getType().equals(type))
		.toList();
	}
	
	public Card getRandomCardFromHandWithType(String type) {
		List<Card> ofType = getCardsFromHandOfType(type);
		if(ofType.isEmpty()) {
			return null;
		}
		else {
			int index = (int)(Math.random() * ofType.size());
			return ofType.get(index);
		}

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
	public boolean isProvenType(String type) {
		List<Card> ofType = new ArrayList<Card>();
		List<Card> proven = new ArrayList<Card>();

		for(Card c : currentGame.getAllOfType(type)) {
			if(hand.contains(c)) {
				ofType.add(c);
			}
		}
		for(Card c : ofType) {
			if(isCardProven(c)) {
				proven.add(c);
			}
		}
		// proven should be a list of cards from of type that have been proven
		return proven.size() > 0;
	}

	public boolean isCardProven(Card c) {
		//  should search through all guesses player has made and find a list of
		// proven cards.
		// then check c against that list

		if (isCardInHand(c)) {
			return false;
		}

		long provenCount = currentGame.getGuessList().stream()
				.filter(g -> g.getGuesser().equals(this))
				.filter(g -> !g.isDisproved())
				.filter(g -> g.containsCard(c)).count();


		return provenCount > 0;

	}

	public boolean isCardInHand(Card c) {

		return hand.contains(c);
		
		//stream way
		//hand.stream().anyMatch(h -> h.equals(c));

	}
	public boolean haveAlreadyShownCardTo(Player p, Card c) {
		//TODO this method should: 
		//get all of a players guesses from the guesslist
		List<Card> disCards = new ArrayList<Card>();
		
		for(Guess g : currentGame.getGuessList()) {
			//filter them so the only guesses remainng are ones disproven by this player
		
			if(g.getDisprovePerson()!= null && g.getDisprovePerson().equals(this)) {
				disCards.add(g.getDisproveCard());
			}
		}
		// return true if card c matches any of those cards
		return disCards.contains(c);
		
		
		
		
		
	}
	protected boolean hasTypeInHand(String s) {
		for(Card c : hand) {
			if(c.getType().equals(s)) {
				return true;
			}else {
				
			}
		}
		return false;
	}

	abstract public Guess makeGuess();

	abstract public String getType();

}
