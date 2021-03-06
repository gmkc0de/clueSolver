package clueSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {

	ArrayList<Player> players;
	Card[] secretCards;
	Card[] allCards;

	public Game() {
		players = new ArrayList<Player>();
		secretCards = new Card[3];
		allCards = new Card[21];
		allCards[0] = new Card("ballroom", "room");
		allCards[1] = new Card("conservatory", "room");
		allCards[2] = new Card("hall", "room");
		allCards[3] = new Card("study", "room");
		allCards[4] = new Card("library", "room");
		allCards[5] = new Card("lounge", "room");
		allCards[6] = new Card("kitchen", "room");
		allCards[7] = new Card("dining room", "room");
		allCards[8] = new Card("billiard room", "room");
		allCards[9] = new Card("lead pipe", "weapon");
		allCards[10] = new Card("rope", "weapon");
		allCards[11] = new Card("revolver", "weapon");
		allCards[12] = new Card("wrench", "weapon");
		allCards[13] = new Card("knife", "weapon");
		allCards[14] = new Card("candlestick", "weapon");
		allCards[15] = new Card("mustard", "weapon");
		allCards[16] = new Card("peacock", "suspect");
		allCards[17] = new Card("scarlet", "suspect");
		allCards[18] = new Card("plum", "suspect");
		allCards[19] = new Card("white", "suspect");
		allCards[20] = new Card("green", "suspect");
	}

	public void printAllCards() {
		for (int i = 0; i < allCards.length; i++) {
			System.out.println(allCards[i]);
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public Card[] getSecretCards() {
		return secretCards;
	}

	public void setSecretCards(Card[] secretCards) {
		this.secretCards = secretCards;
	}

	public Card[] getAllCards() {
		return allCards;
	}

	public void setAllCards(Card[] allCards) {
		this.allCards = allCards;
	}

	public void addPlayers() throws Exception {
		int numPlayers = App.getIntFromUser("How many players?");
		String name = App.getStringInputFromUser("what is your name?");
		System.out.println("hello " + name);
		Player me = new Player(name);
		players.add(me);

		// make list of other players
		for (int i = 1; i < numPlayers; i++) {
			String otherPlayer = App.getStringInputFromUser("what is other player " + i + "'s name?");
			Player other = new Player(otherPlayer);
			players.add(other);

		}

	}

	public void printPlayers() {
		System.out.println("player list");
		for (int i = 0; i < players.size(); i++) {
			System.out.print(players.get(i).getName() + ", ");
		}
		System.out.println();
	}

	public boolean isRealCard(String name ) {
		for (Card current : getAllCards()) {
			if (current.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void askUserForHand() throws Exception {
		int c = App.getIntFromUser("how many cards do you have in your hand?");
		// ask for each card they were dealt and add the card to players hand
		for (int i = 0; i < c; i++) {
			String nameOfCard = App.getStringInputFromUser("please enter a cards name.");
			Card thisCard = getMatchingCard(nameOfCard);
			if (thisCard != null) {
				players.get(0).getHandList().add(thisCard);
				System.out.println(">succesfuly added<");
			} else {
				System.out.println("invalid card please try agian");
				i--;
			}

		}

	}

	public Guess getGuessFromUser() throws Exception {
		// TODO: handle invalid input so that players can reenter their data
		//TODO: condense questions so player always enters a card and a type at the same time
		String name = App.getStringInputFromUser("record a guesss who is making this guess?");
		String sus = App.getStringInputFromUser("please input suspect");
		String weapon = App.getStringInputFromUser("please input weapon");
		String room = App.getStringInputFromUser("please input room");
		if (isValidGuess(sus, weapon, room)) {
			Player disPlayer = getMatchingPLayer(App.getStringInputFromUser("who disproved the guess?"));
			// if the player is the one making the guess record the card as well;
			if (disPlayer != null) {
				if (name.equals(players.get(0).getName())) {
					String disCardName = App.getStringInputFromUser("what was the disproving cards name?");
					Card thisCard = getMatchingCard(disCardName);
					Guess g = new Guess(name, sus, room, weapon, disPlayer, thisCard );
					if (isRealCard(disCardName)) {
						players.get(0).getNotePad().add(g);
						disPlayer.getHandList().add(thisCard);
						Guess n = new Guess(name, sus, room, weapon, disPlayer, thisCard);
						players.get(0).getGuessList().add(n);
						System.out.println(">succesfuly added<");
					} else {
						System.out.println("invalid card");

					}
					return g;
				} else {
					if (getMatchingPLayer(name) != null && getMatchingSuspect(sus) && isValidWeapon(weapon)) {
						System.out.println(">succesfuly added<");
						return new Guess(name, sus, room, weapon, disPlayer);
					} else {
						System.out.println("invalid guess - cannot be entered");
					}

				}
			} else {
				System.out.println("failure - the disproving player is not a valid player");
			}

		} else {
			System.out.println("invalid guess - cannot be entered");
		}
		return null;
	}

	private Card getMatchingCard(String disCardName) {
		for(int i = 0; i < allCards.length; i++) {
			if(allCards[i].getName().equals(disCardName)) {
				return allCards[i];
			}
		}
		return null;
	}

	private boolean getMatchingSuspect(String sus) {
		for (int i = 0; i < allCards.length; i++) {
			if (sus.equals(allCards[i].getName()) && allCards[i].getType().equals("suspect")) {
				return true;
			}
		}
		return false;
	}

	private boolean isValidWeapon(String w) {
		for (int i = 0; i < allCards.length; i++) {
			if (w.equals(allCards[i].getName()) && allCards[i].getType().equals("weapon")) {
				return true;
			}
		}
		return false;
	}

	private Player getMatchingPLayer(String temp) {
		for (int i = 0; i < players.size(); i++) {
			if (temp.equals(players.get(i).getName())) {
				return players.get(i);
			}
		}
		return null;
	}

	public boolean isValidGuess(String sus, String w, String room) {
		if (isRealCard(sus) && isRealCard(w) && isRealCard(room)) {
			return true;
		}
		return false;
	}
}
