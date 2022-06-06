package clueSolver;

import java.util.ArrayList;

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
	
	public void printCard(Guess g) {
		System.out.println(g.toString()); 
	}

	public boolean isOnList(ArrayList<Card> list) {
		for(Card v: list) {
			if(v.getName().equals(this.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Card) {
			Card cardToTest = (Card)o;
			return cardToTest.getName().equals(this.getName());
		}
		return false;
	}
}


