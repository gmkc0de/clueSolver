package clueSolver;

public class Guess {
	String suspect;
	String room;
	String weapon;
	Player disprovePerson;
	Player madeBy;
	Card disproveCard;

	
	public Guess(Player made, String sus, String r, String w, Player disP, Card disC ) {
		suspect = sus;
		room = r;
		weapon = w;
		disprovePerson = disP;
		disproveCard = disC;
		madeBy = made;
	}
	public Guess(Player made, String sus, String r, String w, Player disP) {
		suspect = sus;
		room = r;
		weapon = w;
		disprovePerson = disP;
		madeBy = made;
	}

	public String getSuspect() {
		return suspect;
	}

	public String getRoom() {
		return room;
	}

	public String getWeapon() {
		return weapon;
	}
	public String toString() {
		String s = "";
		s += "the guess was made by: " + madeBy.getName();
		s += ", suspect: " + suspect;
		s += ", weapon: " + weapon;
		s += ", room: " + room;
		s += ", disproved by: " + disprovePerson.getName();
		return s;
	}
	public Player getGuesser() {
		return madeBy;
	}
	
}
