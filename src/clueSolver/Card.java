package clueSolver;

public class Card {
	String type;
	String name;
	Card[] allCards = new Card[21];
	
	public Card(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
		
	}
	public String getType() {
		return type;
		
	}
	public String toString() {
		return name + ", "+ type;
	}
	

	
}


