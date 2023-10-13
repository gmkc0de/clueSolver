package clueSolver;

import java.util.ArrayList;
import java.util.List;

import clueSolver.player.Player;

public class Guess {
	Card suspect;
	Card room;
	Card weapon;
	Player disprovePerson;
	Player madeBy;
	Card disproveCard;

	public Guess() {
		super();
	}

	public Guess(Player made, Card sus, Card r, Card w, Player disP, Card disC) {

		if (made == null || sus == null || r == null || w == null) {
			throw new NullPointerException(
					"guesser :" + made + " sus: " + sus + ", room:" + r + ", weapon: " + w + " should all be not null");
		}

		suspect = sus;
		room = r;
		weapon = w;
		disprovePerson = disP;
		disproveCard = disC;
		madeBy = made;
	}

	public Guess(Player made, Card sus, Card r, Card w, Player disP) {
		suspect = sus;
		room = r;
		weapon = w;
		disprovePerson = disP;
		madeBy = made;
	}

	public Card getSuspect() {
		return suspect;
	}

	public Card getRoom() {
		return room;
	}

	public Card getWeapon() {
		return weapon;
	}

	public String toString() {

		String s = "";
		s += "the guess was made by: " + madeBy.getName();
		s += ", suspect: " + (suspect != null ? suspect.getName() : "null");
		s += ", weapon: " + (weapon != null ? weapon.getName() : "null");
		s += ", room: " + (room != null ? room.getName() : "null");
		if (disprovePerson != null) {
			s += ", disproved by: " + disprovePerson.getName();

		} else {
			s += ", disproved by: NULL";
		}
		if (disproveCard != null) {
			s += ", disproving card: " + disproveCard.getName();
		}
		return s;
	}

	public Player getGuesser() {
		return madeBy;
	}

	public Card getDisprovingCard() {
		return disproveCard;
	}

	public List<Card> getCards() {
		List<Card> all = new ArrayList<Card>();
		all.add(getSuspect());
		all.add(getWeapon());
		all.add(getRoom());
		return all;
	}
	public Card getNotNullCard() {
		List<Card> all = getCards();
		for(Card c : all) {
			if(c != null) {
				return c;
			}
		}
		System.out.println("getNotNullCard() could not find a non null card");
		return null;
	}

	public Player getDisprovePlayer() {
		return disprovePerson;
	}

	public Player getMadeBy() {
		return madeBy;
	}

	public void setMadeBy(Player madeBy) {
		this.madeBy = madeBy;
	}

	public boolean equals() {

		return false;
	}

	public boolean isDisproved() {
		return disproveCard != null;
	}

	public Player getDisprovePerson() {
		return disprovePerson;
	}

	public void setDisprovePerson(Player disprovePerson) {
		this.disprovePerson = disprovePerson;
	}

	public void setSuspect(Card suspect) {
		this.suspect = suspect;
	}

	public void setRoom(Card room) {
		this.room = room;
	}

	public void setWeapon(Card weapon) {
		this.weapon = weapon;
	}

	public Card getDisproveCard() {
		return disproveCard;
	}

	public void setDisproveCard(Card disproveCard) {
		this.disproveCard = disproveCard;
	}

	public boolean containsCard(Card c) {

		if (this.getCardOfTypeFromGuess(c.getType()) != null) {
			return true;
		}
		return false;

	}

	public Card getCardOfTypeFromGuess(String type) {
		if (type.equals("suspect")) {
			return this.getSuspect();
		} else if (type.equals("room")) {
			return this.getRoom();
		} else if (type.equals("weapon")) {
			return this.getWeapon();
		} else {
			return null;
		}
	}

	public void addCardToGuess(Card card) {
		RuntimeException notNull = new RuntimeException("the card you tried to overwrite was not null");
		if (card == null) {
			return;
		}

		if (card.isSuspect()) {
			if (this.getSuspect() == null) {
				suspect = card;
			} else {
				throw notNull;
			}
		} else if (card.isRoom()) {
			if (this.getRoom() == null) {
				room = card;
			} else {
				throw notNull;
			}
		} else if (card.isWeapon()) {
			if (this.getWeapon() == null) {
				weapon = card;
			} else {
				throw notNull;
			}
		} else {
			RuntimeException notType = new RuntimeException("card does not have a recognized type: " + card.getType());
			throw notType;
		}

	}
	
	
	public void addAllCards(List<Card> cards) {
		for(Card c : cards) {
			addCardToGuess(c);
		}
	}
	
	public List<String> getMissingTypes() {
		List<String> cardTypes = new ArrayList<String>();		
		if(weapon == null) {
			cardTypes.add("weapon");
		}
		if(room == null) {
			cardTypes.add("room");
		}
		if(suspect == null) {
			cardTypes.add("suspect");
		}
		return cardTypes;
	}

	

}
