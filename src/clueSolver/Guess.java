package clueSolver;

public class Guess {
	String suspect;
	String room;
	String weapon;
	Player disprovePerson;
	String madeBy;
	Card disproveCard;
	
	public Guess(String madeBy, String sus, String r, String w, Player disP, Card disC ) {
		suspect = sus;
		room = r;
		weapon = w;
		disprovePerson = disP;
		disproveCard = disC;
		this.madeBy = madeBy;
	}
	public Guess(String madeBy, String sus, String r, String w, Player disP) {
		suspect = sus;
		room = r;
		weapon = w;
		disprovePerson = disP;
		this.madeBy = madeBy;
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
