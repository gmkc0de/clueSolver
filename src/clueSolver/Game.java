package clueSolver;

import java.util.ArrayList;

public class Game {

	// TODO:review code in App.dealCards()
	private ArrayList<Player> players;
	private Card[] secretCards;
	private Card[] allCards;
	private ArrayList<Guess> guessList;
	private ArrayList<String> testPlayerNames;
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
		
		testPlayerNames = new ArrayList<String>();
		testPlayerNames.add("anna");
		testPlayerNames.add("ben");
		testPlayerNames.add("cinna");
		testPlayerNames.add("dane");
		testPlayerNames.add("emily");
		testPlayerNames.add("fiona");
	}

	public static Game createTestGame(int numPlayers) {
		Game game = new Game();
		
		for(int i = 0; i< numPlayers;i++ ) {
			String  name = game.testPlayerNames.get(i);
			//TODO: find a way t give all player variables different names
			Player a = new Player(name, game);
			game.addPlayer(a);
		}
	
		ArrayList<Card> cardList = new ArrayList<Card>();
		for (Card c : game.getAllCards()) {
			cardList.add(c);
		}
		System.out.println(">>test game created<<");
		return game;
	}

	public Player nextPlayersTurn() {
		Player nextPlayer = null;
		if (guessList.size() == 0) {
			// if there have been no rounds
			nextPlayer = players.get(0);
		} else {
			// get the last person who guessed
			Player lastGuesser = guessList.get(guessList.size() - 1).getGuesser();
			if (players.indexOf(lastGuesser) == players.size() - 1) {
				// if the lastGuesser was the last on the player list start over at the top
				nextPlayer = players.get(0);
			} else {
				// last guesser is not last its the next player on the player lists turn
				int num = players.indexOf(lastGuesser);
				nextPlayer = players.get(num + 1);
			}

		}

		return nextPlayer;
	}

	public void takeTurn() {
		guessList.add(generateRandomGuess(nextPlayersTurn()));
	}

	public void dealCards() {
		ArrayList<Card> toDeal = addAll(allCards);
		// pick the three sescret cards	
		Card randomSuspect = findRandomSuspect();
		Card randomWeapon = findRandomWeapon();
		Card randomRoom = findRandomRoom();
		// fill in secret cards array
		secretCards[0] = randomSuspect;
		secretCards[1] = randomWeapon;
		secretCards[2] = randomRoom;
		
		// remove the secret cards from the cards to be dealt
		toDeal.remove(randomSuspect);
		toDeal.remove(randomWeapon);
		toDeal.remove(randomRoom);
		
		// deal till there are no more cards
		int numPlayers = players.size();
		int numCardsDealt = 0;
		while (toDeal.size() > 0) {
			int num = (int) (Math.random() * toDeal.size());
			Card c = toDeal.remove(num);
			// card c goes to player number[the remainder of numCardsDealt/numPlayers]
			//this ensures it goes in a circle
			Player p = players.get(numCardsDealt % numPlayers);
			p.addToHand(c);
			++numCardsDealt;
		}
	}

	public void oldDealCards() {
		// deal the three secret cards
		String whatType = "suspect";
		String nextType = "weapon";
		ArrayList<Card> availibleCards = addAll(allCards);

		for (int i = 0; i < 3; i++) {
			if (availibleCards.size() != 0) {
				ArrayList<Card> typeList = getAllAnyType(whatType);
				int num = (int) (Math.random() * typeList.size());
				secretCards[i] = typeList.get(num);
				// removes dealt card from available list
				availibleCards.remove(getMatchingCard(typeList.get(num).getName()));
				whatType = nextType;
				nextType = "room";

			}
		}
		// deal to players
		while (availibleCards.size() != 0) {
			for (Player p : players) {
				if (availibleCards.size() != 0) {
					int num = (int) (Math.random() * availibleCards.size());
					p.getHandList().add(availibleCards.get(num));
					availibleCards.remove(num);
				}
			}
		}
	}

	public void printAllCards() {
		for (int i = 0; i < allCards.length; i++) {
			System.out.println(allCards[i]);
		}
	}

	public void printPlayers() {
		System.out.println("player list:");
		for (int i = 0; i < players.size(); i++) {
			System.out.print(players.get(i).getName() + ", ");
		}
		System.out.println();
	}

	public void askUserForHand() throws Exception {
		int c = App.getIntFromUser("how many cards do you have in your hand?");
		// ask for each card they were dealt and add the card to players hand
		for (int i = 0; i < c; i++) {
			String nameOfCard = App.getStringInputFromUser("please enter a cards name.");
			Card thisCard = getMatchingCard(nameOfCard);
			if (thisCard != null) {
				getMyPlayer().getHandList().add(thisCard);
				System.out.println(">succesfuly added<");
			} else {
				System.out.println("invalid card please try agian");
				i--;
			}

		}

	}

	public void autoGuess(int k) {
		for (int i = 0; i < k; i++) {
			guessList.add(generateRandomGuess(players.get((int) (Math.random() * players.size()))));
		}

	}

	private Player anyPlayerButThis(Player guesser) {
		Player p = players.get((int) (Math.random() * players.size()));
		if (p.equals(guesser)) {
			return anyPlayerButThis(guesser);
		}
		return p;
	}

	private ArrayList<Player> allPlayersButThis(Player guesser) {
		ArrayList<Player> allButThis = new ArrayList<Player>();
		allButThis.addAll(getPlayers());
		allButThis.remove(guesser);
		return allButThis;
	}

	// if my guess, record the card i see with my guess
	// if not my guess, record the guess and who disproved in the games guess list

	public boolean isRealCard(String name) {
		for (Card current : getAllCards()) {
			if (current.getName().equals(name)) {
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

	public boolean isValidGuess(String sus, String w, String room) {
		if (isRealCard(sus) && isRealCard(w) && isRealCard(room)) {
			return true;
		}
		return false;
	}

	private Guess generateRandomGuess(Player guesser) {
		ArrayList<Card> suspects = findUnknownSuspects(guesser);
		ArrayList<Card> weapons = findUnknownWeapons(guesser);
		ArrayList<Card> rooms = findUnknownRooms(guesser);
		ArrayList<Card> guessCards = new ArrayList<Card>();
		ArrayList<Player> possibleDisprovers = allPlayersButThis(guesser);

		// calc all random cards
		// TODO: these all keep giving me the "Index 0 out of bounds for length 0" error
		// debug to fix
		Card s = null;
		Card w = null;
		Card r = null;
		try {
			s = suspects.get((int) (Math.random() * suspects.size()));
			guessCards.add(s);
			w = weapons.get((int) (Math.random() * weapons.size()));
			guessCards.add(w);
			r = rooms.get((int) (Math.random() * rooms.size()));
			guessCards.add(r);
		}
		catch(Exception e) {
			System.err.println(e.getLocalizedMessage());
			throw e;
		}
		// will be left null of no one disproves
		Card disCard = null;
		Player disprover = null;

		for (Player p : possibleDisprovers) {
			// checks if any players can disprove the guess
			Card dis = p.disproveGuess(new Guess(guesser, s, r, w, p, null));
			if (dis != null) {
				// fills in cards if player can disprove
				disprover = p;
				disCard = dis;
				break;
			}

		}
		// if the guess was not disproved
		if (disprover == null) {
			System.out.println(">>no one could disprove this guess<<");
			return new Guess(guesser, s, r, w, null, null);
		} else {
			// if the guess was disproved
			if (guesser.equals(getMyPlayer()) || guesser.isComputer()) {
				// if guesser is me or a computerized player
				Guess rand = new Guess(guesser, s, r, w, disprover, disCard);
				return rand;
			} else {
				// if guesser the is a human who is not me
				Guess rand = new Guess(guesser, s, r, w, disprover, null);
				return rand;
			}

		}

	}

	public ArrayList<Card> findUnknownSuspects(Player p) {
		// wow dad just wow -_-
		ArrayList<Card> playerClues = findPlayerClues(p);
		ArrayList<Card> allSus = getAllSuspects();
		ArrayList<Card> unknown = addAll(allSus);
		unknown.removeAll(playerClues);

		return unknown;
	}

	public ArrayList<Card> findUnknownRooms(Player p) {
		ArrayList<Card> playerClues = findPlayerClues(p);
		ArrayList<Card> allRooms = getAllRooms();
		ArrayList<Card> unknown = addAll(allRooms);
		unknown.removeAll(playerClues);

		return unknown;

	}

	public ArrayList<Card> findUnknownWeapons(Player p) {
		ArrayList<Card> playerClues = findPlayerClues(p);
		ArrayList<Card> allWeapon = getAllWeapons();
		ArrayList<Card> unknown = addAll(allWeapon);
		unknown.removeAll(playerClues);
		return unknown;

	}

	public ArrayList<Card> findPlayerClues(Player p) {
		// add all the clues in the players hand
		ArrayList<Card> playerClues = addAll(p.getHandList());
		ArrayList<Guess> playerGuesses = findPLayerGuesses(p);
		for (Guess g : playerGuesses) {
			if (g.getDisprovingCard() != null && !playerClues.contains(g.getDisprovingCard())) {
				playerClues.add(g.getDisprovingCard());
			}
		}
		return playerClues;
	}

	public ArrayList<Guess> findPLayerGuesses(Player p) {
		ArrayList<Guess> playerList = new ArrayList<Guess>();
		for (Guess g : guessList) {
			if (g.getGuesser().equals(p)) {
				playerList.add(g);
			}
		}
		return playerList;
	}

	public Guess findWinningGuess() {

		for (Guess g : guessList) {
			if (g.getSuspect().equals(secretCards[0]) && g.getWeapon().equals(secretCards[1])
					&& g.getRoom().equals(secretCards[2])) {
				return g;
			}
		}
		return null;
	}

	private Card findRandomSuspect() {
		ArrayList<Card> s = getAllSuspects();
		int num = (int) (Math.random() * s.size());

		return s.get(num);
	}

	private Card findRandomRoom() {
		ArrayList<Card> r = getAllRooms();
		int num = (int) (Math.random() * r.size());
		return r.get(num);
	}

	private Card findRandomWeapon() {
		ArrayList<Card> w = getAllWeapons();
		int num = (int) (Math.random() * w.size());

		return w.get(num);
	}

	public boolean hasWinningGuess() {

		return findWinningGuess() != null;
	}

	// Adders
	public void addPlayers() throws Exception {
		int numPlayers = App.getIntFromUser("How many players?");
		String name = App.getStringInputFromUser("what is your name?");
		System.out.println("hello " + name);
		Player me = new Player(name, this);
		players.add(me);

		// make list of other players
		for (int i = 1; i < numPlayers; i++) {
			String otherPlayer = App.getStringInputFromUser("what is other player " + i + "'s name?");
			Player other = new Player(otherPlayer, this);
			players.add(other);

		}

	}

	public ArrayList<Card> addAll(ArrayList<Card> list) {
		ArrayList<Card> added = new ArrayList<Card>();
		for (int i = 0; i < list.size(); i++) {
			added.add(list.get(i));
		}
		return added;
	}

	public ArrayList<Card> addAll(Card[] list) {
		ArrayList<Card> added = new ArrayList<Card>();
		for (int i = 0; i < list.length; i++) {
			added.add(list[i]);
		}
		return added;
	}

	public void addPlayer(Player p) {
		getPlayers().add(p);

	}

	public void addGuess(Guess g) {
		guessList.add(g);

	}

	// Getters
	public Guess getGuessFromUser() throws Exception {
		Player guessPlayer = getMatchingPLayer(App.getStringInputFromUser("record a guesss who is making this guess?"));
		Card sus = getMatchingCard(App.getStringInputFromUser("please input suspect"));
		Card weapon = getMatchingCard(App.getStringInputFromUser("please input weapon"));
		Card room = getMatchingCard(App.getStringInputFromUser("please input room"));
		String disPlayer = App.getStringInputFromUser("who disproved the guess?");
		if (guessPlayer.equals(getMyPlayer())) {
			// my guess
			String disCardName = App.getStringInputFromUser("what was the disproving cards name?");
			Card thisCard = getMatchingCard(disCardName);
			if (isValidGuess(sus.getName(), weapon.getName(), room.getName()) && getMatchingPLayer(disPlayer) != null) {

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

	public Guess oldGetGuessFromUser() throws Exception {
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
				if (guessPlayer.equals(getMyPlayer())) {
					// i am the guesser, and my guess was disproved
					// record the disproved card in my note notepad

					String disCardName = App.getStringInputFromUser("what was the disproving cards name?");
					Card thisCard = getMatchingCard(disCardName);
					Guess g = new Guess(guessPlayer, getMatchingCard(sus), getMatchingCard(room),
							getMatchingCard(weapon), disPlayer, thisCard);
					if (isRealCard(disCardName)) {
						getMyPlayer().getNotePad().add(g);
						disPlayer.getHandList().add(thisCard);
						Guess n = new Guess(guessPlayer, getMatchingCard(sus), getMatchingCard(room),
								getMatchingCard(weapon), disPlayer, thisCard);
						getMyPlayer().getGuessList().add(n);
						System.out.println(">succesfuly added<");
					} else {
						System.out.println("invalid card");

					}
					return g;
				} else {
					// i am not the guesser
					if (guessPlayer != null && getMatchingSuspect(sus) && isValidWeapon(weapon)) {
						System.out.println(">succesfuly added<");
						return new Guess(guessPlayer, getMatchingCard(sus), getMatchingCard(room),
								getMatchingCard(weapon), disPlayer);
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

	public ArrayList<Guess> getGuessList() {
		return guessList;
	}

	private ArrayList<Card> getAllSuspects() {
		ArrayList<Card> sus = new ArrayList<Card>();
		for (Card c : allCards) {
			if (c.getType().equals("suspect")) {
				sus.add(c);
			}
		}
		return sus;
	}

	private ArrayList<Card> getAllWeapons() {
		ArrayList<Card> w = new ArrayList<Card>();
		for (Card c : allCards) {
			if (c.getType().equals("weapon")) {
				w.add(c);
			}
		}
		return w;
	}

	private ArrayList<Card> getAllRooms() {
		ArrayList<Card> r = new ArrayList<Card>();
		for (Card c : allCards) {
			if (c.getType().equals("room")) {
				r.add(c);
			}
		}
		return r;

	}

	private ArrayList<Card> getAllAnyType(String whatType) {
		if (whatType.equals("suspect")) {
			return getAllSuspects();
		} else if (whatType.equals("room")) {
			return getAllRooms();
		} else if (whatType.equals("weapon")) {
			return getAllWeapons();
		}
		System.out.println("getAllAnyType failed returned null");
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

	private Player getMatchingPLayer(String temp) {
		for (int i = 0; i < players.size(); i++) {
			if (temp.equals(players.get(i).getName())) {
				return players.get(i);
			}
		}
		return null;
	}

	public Card[] getSecretCards() {
		return secretCards;
	}

	public Card[] getAllCards() {
		return allCards;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public Player getMyPlayer() {
		return players.get(0);
	}

	private ArrayList<Card> getSuspectsFrom(ArrayList<Card> list) {
		ArrayList<Card> sus = new ArrayList<Card>();
		for (Card c : list) {
			if (c.getType().equals("suspect")) {
				sus.add(c);
			}
		}
		return sus;
	}

	private ArrayList<Card> getWeaponsFrom(ArrayList<Card> list) {
		ArrayList<Card> w = new ArrayList<Card>();
		for (Card c : list) {
			if (c.getType().equals("suspect")) {
				w.add(c);
			}
		}
		return w;
	}

	private ArrayList<Card> getRoomsFrom(ArrayList<Card> list) {
		ArrayList<Card> r = new ArrayList<Card>();
		for (Card c : list) {
			if (c.getType().equals("suspect")) {
				r.add(c);
			}
		}
		return r;
	}

// Setter
	public void setSecretCards(Card[] secretCards) {
		this.secretCards = secretCards;
	}

	public void setAllCards(Card[] allCards) {
		this.allCards = allCards;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

}
