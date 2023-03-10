package clueSolver.player;

import java.util.ArrayList;

import clueSolver.Card;
import clueSolver.Game;
import clueSolver.Guess;

public class SmartPlayer extends Player {
	
	public SmartPlayer(String name, Game g) {
		this.name = name;
		guesses = new ArrayList<Guess>();
		hand = new ArrayList<Card>();
		notePad = new ArrayList<Guess>();
		currentGame = g;
		isComputer = true;
		
	}
	
	@Override
	public Guess makeGuess() {
		// TODO write a smart player make guess
		return null;
	}

}
