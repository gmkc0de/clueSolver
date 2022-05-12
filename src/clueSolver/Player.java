package clueSolver;

import java.util.ArrayList;

public class Player {
	private String name;
	private ArrayList<Guess> guesses;
	private ArrayList<Card> hand;
	public ArrayList<Guess> notePad;
	public Player(String name) {
		this.name = name;
		guesses = new ArrayList<Guess>();
		hand = new ArrayList<Card>();
		notePad = new ArrayList<Guess>();
	}
	public ArrayList<Guess> getGuessList(){
		return guesses;
	}
	public String getName() {
		return name;
	}
	public ArrayList<Card> getHandList(){
		return hand;
	}
	public ArrayList<Guess> getNotePad() {
		return notePad;
	}
	public void printHand() {
		for(Card c : hand) {
			System.out.println(c.getName() + "-" + c.getType() );
		}
	}
	
}
