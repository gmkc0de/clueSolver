package clueSolver;

public class Guess {
	Card suspect;
	Card room;
	Card weapon;
	Player disprovePerson;
	Player madeBy;
	Card disproveCard;

	
	public Guess(Player made, Card sus, Card r, Card w, Player disP, Card disC ) {
		
		if(made == null || sus == null || r == null || w == null) {
			throw new NullPointerException("guesser :"+made +" sus: "+sus+", room:"+r+", weapon: "+w+" should all be not null");
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
		s += ", weapon: " + (weapon != null ? weapon.getName()  : "null");
		s += ", room: " + (room != null ? room.getName() : "null");
		if(disprovePerson != null) {
			s += ", disproved by: " + disprovePerson.getName();

		}else {
			s += ", disproved by: NULL";
		}
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
	public Player getDisprovePlayer() {
		return disprovePerson;
	}
	public Player getMadeBy() {
		return madeBy;
	}
	public void setMadeBy(Player madeBy) {
		this.madeBy = madeBy;
	}
	public boolean equals(){
		
		return false;
	}
	public boolean isDisproved() {
		return disproveCard != null;
	}
	
}
