package clueSolver;

import java.util.List;

public class Card {
	
	protected static Room BALLROOM = new Room("ballroom");
	protected static Room CONSERVATORY = new Room("conservatory");;
	protected static Room HALL = new Room("hall");
	protected static Room STUDY = new Room("study");
	protected static Room LIBRARY = new Room("library");
	protected static Room LOUNGE = new Room("lounge");
	protected static Room KITCHEN = new Room("kitchen");
	protected static Room DININGROOM = new Room("dining room");
	protected static Room BILLIARDROOM = new Room("billiard room");
	protected static Weapon LEADPIPE = new Weapon("lead pipe");
	protected static Weapon ROPE = new Weapon("rope");
	protected static Weapon REVOLVER = new Weapon("revolver");
	protected static Weapon WRENCH = new Weapon("wrench"); 
	protected static Weapon KNIFE = new Weapon("knife"); 
	protected static Weapon CANDLESTICK = new Weapon("candlestick");
	protected static Suspect MUSTARD = new Suspect("mustard");
	protected static Suspect PEACOCK = new Suspect("peacock"); 
	protected static Suspect SCARLET = new Suspect("scarlet"); 
	protected static Suspect PLUM = new Suspect("plum"); 
	protected static Suspect WHITE = new Suspect("white"); 
	protected static Suspect GREEN = new Suspect("green"); 
	
	
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

	public boolean isOnList(List<Card> list) {
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

	public boolean isSuspect() {
		
		return this.getType().equals("suspect");
		
	}	public boolean isWeapon() {
		
		return this.getType().equals("weapon");
		
	}	public boolean isRoom() {
		
		return this.getType().equals("room");
	}
}


