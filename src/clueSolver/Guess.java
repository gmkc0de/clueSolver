package clueSolver;

public class Guess {
	// TODO sus room and weapon should be cards
	Card suspect;
	Card room;
	Card weapon;
	Player disprovePerson;
	Player madeBy;
	Card disproveCard;

	
	public Guess(Player made, Card sus, Card r, Card w, Player disP, Card disC ) {
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
		s += ", suspect: " + suspect.getName();
		s += ", weapon: " + weapon.getName();
		s += ", room: " + room.getName();
		s += ", disproved by: " + disprovePerson.getName();
		if(disproveCard != null) {
			s+= ", disproving card: " + disproveCard.getName();
		}
		return s;
	}
	public Player getGuesser() {
		return madeBy;
	}
	public Card getDisprovingCard() {
		return disproveCard;
	}
	
}
