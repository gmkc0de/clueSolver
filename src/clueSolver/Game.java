package clueSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: make an aoutomaticGuesser method.
public class Game {

	private ArrayList<Player> players;
	private Card[] secretCards;
	private Card[] allCards;
	private ArrayList<Guess> guessList;

	// allGuesses

	public Game() {
		players = new ArrayList<Player>();
		secretCards = new Card[3];
		guessList = new ArrayList<Guess>();
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
		allCards[15] = new Card("mustard", "suspect");
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

	public static Game createTestGame() {
		Game game = new Game();
		Player g = new Player("g");
		Player h = new Player("h");
		Player j = new Player("j");
		game.addPlayer(g);
		game.addPlayer(h);
		game.addPlayer(j);
		ArrayList<Card> cardList = new ArrayList<Card>();
		for (Card c : game.getAllCards()) {
			cardList.add(c);
		}
		for (int i = 0; i < 3; i++) {
			int num = (int) Math.random() * cardList.size();
			g.getHandList().add(cardList.get(num));
			cardList.remove(num);
		}
		System.out.println("test game created");
		return game;
	}

	public void addPlayer(Player p) {
		getPlayers().add(p);

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

	public boolean isRealCard(String name) {
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

	// if my guess, record the card i see with my guess
	// if not my guess, record the guess and who disproved in the games guess list

	public Guess oldGetGuessFromUser() throws Exception {

		// TODO: handle invalid input so that players can reenter their data

		Player guessPlayer = getMatchingPLayer(App.getStringInputFromUser("record a guesss who is making this guess?"));
		String sus = App.getStringInputFromUser("please input suspect");
		String weapon = App.getStringInputFromUser("please input weapon");
		String room = App.getStringInputFromUser("please input room");
		if (isValidGuess(sus, weapon, room)) {
			String disPlayerUserInput = App.getStringInputFromUser("who disproved the guess?");
			Player disPlayer = getMatchingPLayer(disPlayerUserInput);
			// if the player is the one making the guess record the card as well;
			if (disPlayerUserInput.equals("none")) {
				// not disproved!
			} else if (disPlayer != null) {
				// the guess was disproved
				if (guessPlayer.equals(players.get(0))) {
					// i am the guesser, and my guess was disproved
					// record the disproved card in my note notepad

					String disCardName = App.getStringInputFromUser("what was the disproving cards name?");
					Card thisCard = getMatchingCard(disCardName);
					Guess g = new Guess(guessPlayer, sus, room, weapon, disPlayer, thisCard);
					if (isRealCard(disCardName)) {
						players.get(0).getNotePad().add(g);
						disPlayer.getHandList().add(thisCard);
						Guess n = new Guess(guessPlayer, sus, room, weapon, disPlayer, thisCard);
						players.get(0).getGuessList().add(n);
						System.out.println(">succesfuly added<");
					} else {
						System.out.println("invalid card");

					}
					return g;
				} else {
					// i am not the guesser
					if (guessPlayer != null && getMatchingSuspect(sus) && isValidWeapon(weapon)) {
						System.out.println(">succesfuly added<");
						return new Guess(guessPlayer, sus, room, weapon, disPlayer);
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

	public Guess getGuessFromUser() throws Exception {
		Player guessPlayer = getMatchingPLayer(App.getStringInputFromUser("record a guesss who is making this guess?"));
		String sus = App.getStringInputFromUser("please input suspect");
		String weapon = App.getStringInputFromUser("please input weapon");
		String room = App.getStringInputFromUser("please input room");
		String disPlayer = App.getStringInputFromUser("who disproved the guess?");
		if (guessPlayer.equals(players.get(0))) {
			// my guess
			String disCardName = App.getStringInputFromUser("what was the disproving cards name?");
			Card thisCard = getMatchingCard(disCardName);
			if (isValidGuess(sus, weapon, room) && getMatchingPLayer(disPlayer) != null) {

				return new Guess(guessPlayer, sus, room, weapon, getMatchingPLayer(disPlayer), thisCard);
			}
		} else {
			// not my guess
			return new Guess(guessPlayer, sus, room, weapon, getMatchingPLayer(disPlayer), null);
		}

		// ask user to input guess
		// if its our guess, also ask for card shown
		// return the guess with all information filled in the guess
		// if the guess was not disproved, leave disprove card null, and leave
		// disproving player null
		// NOTE: we do not add the guess to the the game in this method

		return null;
	}

	private Card getMatchingCard(String disCardName) {
		for (int i = 0; i < allCards.length; i++) {
			if (allCards[i].getName().equals(disCardName)) {
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

	public void addGuess(Guess g) {
		guessList.add(g);

	}

	public ArrayList<Guess> findPLayerGuesses(Player p) {
		ArrayList<Guess> playerList = new ArrayList<Guess>();
		for (Guess g : guessList) {
			if (g.getGuesser().getName().equals(p.getName())) {
				playerList.add(g);
			}
		}
		return playerList;
	}

	public void autoGuess(int k) {
		for (int i = 0; i < k; i++) {
			guessList.add(generateRandomGuess());
		}

	}

	private Guess generateRandomGuess() {
		// TODO actually only generates one guess
		ArrayList<Card> suspects = new ArrayList<Card>();
		for (int i = 15; i <= 20; i++) {
			suspects.add(allCards[i]);
		}
		ArrayList<Card> weapons = new ArrayList<Card>();
		for (int j = 9; j <= 14; j++) {
			weapons.add(allCards[j]);
		}
		ArrayList<Card> rooms = new ArrayList<Card>();
		for (int k = 0; k < 9; k++) {
			rooms.add(allCards[k]);
		}
		// calc all randoms
		String s = suspects.get((int) (Math.random() * suspects.size())).getName();
		String w = weapons.get((int) (Math.random() * weapons.size())).getName();
		String r = rooms.get((int) (Math.random() * rooms.size())).getName();
		Player guesser = players.get((int) (Math.random() * players.size()));
		// TODO fix so it picks random people instead of just player 0
		double disprover = Math.random() * players.size();
		double discard = Math.random() * allCards.length;
		if (guesser.getName().equals(players.get(0).getName())) {
			// gueser is me
			Guess rand = new Guess(guesser, s, r, w, players.get((int) disprover), allCards[(int) discard]);
			return rand;
		} else {
			// guesser not me
			Guess rand = new Guess(guesser, s, r, w, players.get((int) disprover), null);
			return rand;
		}

	}

	public ArrayList<Card> findMyClues() {
		ArrayList<Card> myClues = new ArrayList<Card>();
		System.out.println("my clues:");
		for (Card v : players.get(0).getHandList()) {
			myClues.add(v);
			System.out.println(v.getName());
		}
		for (Guess k : guessList) {
			// select our guesses
			if (k.getGuesser().getName().equals(players.get(0).getName())) {
				if (!k.getDisprovingCard().isOnList(myClues)) {
					myClues.add(k.getDisprovingCard());
				}
				System.out.println(k.getDisprovingCard().getName());
			}
		}
		return myClues;
	}

	public ArrayList<Card> findUnknownSuspects() {
		// TODO
		ArrayList<Card> myClues = findMyClues();
		ArrayList<Card> unknown = new ArrayList<Card>();
		for (int i = 15; i <= 20; i++) {
			unknown.add(allCards[i]);
		}
		for (Card c : myClues) {
			for (int i = 0; i < unknown.size(); i++) {
				if (c.getName().equals(unknown.get(i).getName())) {
					unknown.remove(i);
					i--;

				}

			}

		}

		return unknown;
	}

	public ArrayList<Card> findUnknownRooms() {
		ArrayList<Card> myClues = findMyClues();
		ArrayList<Card> unknown = new ArrayList<Card>();
		for (int i = 0; i <= 8; i++) {
			unknown.add(allCards[i]);
		}
		for (Card c : myClues) {
			for (int i = 0; i < unknown.size(); i++) {
				if (c.getName().equals(unknown.get(i).getName())) {
					unknown.remove(i);
					i--;

				}

			}

		}

		return unknown;

	}

	public ArrayList<Card> findUnknownWeapons() {
		ArrayList<Card> myClues = findMyClues();
		ArrayList<Card> unknown = new ArrayList<Card>();
		for (int i = 10; i <= 14; i++) {
			unknown.add(allCards[i]);
		}
		for (Card c : myClues) {
			for (int i = 0; i < unknown.size(); i++) {
				if (c.getName().equals(unknown.get(i).getName())) {
					unknown.remove(i);
					i--;

				}

			}

		}

		return unknown;

	}

	public ArrayList<Guess> getGuessList() {
		return guessList;
	}
}
