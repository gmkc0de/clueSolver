package clueSolver;

public class Guess {
	String suspect;
	String room;
	String weapon;
	Player disprovePerson;
	Player madeBy;
	Card disproveCard;
	//TOD: madeby should be a player object
	
	public Guess(Player madeBy, String sus, String r, String w, Player disP, Card disC ) {
		suspect = sus;
		room = r;
		weapon = w;
		disprovePerson = disP;
		disproveCard = disC;
		madeBy = madeBy;
	}
	public Guess(Player madeBy, String sus, String r, String w, Player disP) {
		suspect = sus;
		room = r;
		weapon = w;
		disprovePerson = disP;
		madeBy = madeBy;
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
		return"";
	}
}
