package clueSolver;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import clueSolver.db.CardDb;
import clueSolver.db.GameDb;
import clueSolver.db.GuessDb;
import clueSolver.db.PlayerDb;
import clueSolver.db.SqliteUtil;
import clueSolver.player.Player;
import clueSolver.player.RandomPlayer;

public class Game {

	
	private int id;
	private ArrayList<Player> players;
	private Card[] secretCards;
	private Card[] allCards;
	private ArrayList<Guess> guessList;
	private ArrayList<String> testPlayerNames;
	private Connection conn;

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

		conn = SqliteUtil.connect();

	}

	public static Game createTestGame(int numPlayers) {
		Game game = new Game();
		for (int i = 0; i < numPlayers; i++) {
			String name = game.testPlayerNames.get(i);
			Player a = new RandomPlayer(name, game);
			int order = game.addPlayer(a);
			a.setOrder(order);
			
		}

		ArrayList<Card> cardList = new ArrayList<Card>();
		for (Card c : game.getAllCards()) {
			cardList.add(c);

		}
		L.i(">>test game created<<");
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
			// this ensures it goes in a circle
			int playerIndex = numCardsDealt % numPlayers;
			Player p = players.get(playerIndex);
			p.addToHand(c);
			//insert(p.getName());
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
					p.getHand().add(availibleCards.get(num));
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
				getMyPlayer().getHand().add(thisCard);
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
	// returns the list of players in [this game] minus the player you put in
	private ArrayList<Player> allPlayersButThis(Player guesser) {
		return allPlayersButThis(getPlayers(), guesser);
	}
	// returns the list of players you put in in the original order minus the player you put in 
	protected static ArrayList<Player> allPlayersButThis(List<Player> players, Player guesser) {
		ArrayList<Player> allButThis = new ArrayList<Player>();
		allButThis.addAll(players);
		allButThis.remove(guesser);
		return allButThis;
	}
	// returns a list of players minus the guesser IN THE CORRECT GUESSING ORDER
	protected static ArrayList<Player> disprovePlayerList(List<Player> players, Player guesser){
		ArrayList<Player> newPlayerOrder = new ArrayList<Player>();
		int guesserIndex = players.indexOf(guesser);
		int numPlayers = players.size();
		for(int i = 0; i < numPlayers -1; i++){
			int nextIndex = (guesserIndex +1 +i )% numPlayers;
			newPlayerOrder.add(players.get(nextIndex));
		}
		
		
		return newPlayerOrder;
	}
	
	
	
	
	// playerList (0-4)
	// player
	// players index -> 2
	// return a list in the following order
	// 3,4,0,1
	// 0,1,2,3,4
	
	
	

	
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
		
		Guess guess = guesser.makeGuess();
		ArrayList<Player> possibleDisprovers = disprovePlayerList(getPlayers() ,guesser);		
		Player disprover = null;
		Card disCard = null;
		
		for (Player p : possibleDisprovers) {
			// checks if any players can disprove the guess
			Card dis = p.disproveGuess(guess);
			if (dis != null) {
				// fills in cards if player can disprove
				guess.setDisprovePerson(p);
				disprover = p;
				disCard = dis;
				break;
			}

		}
		return guess;
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
		ArrayList<Card> playerClues = addAll(p.getHand());
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
		Player me = new RandomPlayer(name, this);
		players.add(me);

		// make list of other players
		for (int i = 1; i < numPlayers; i++) {
			String otherPlayer = App.getStringInputFromUser("what is other player " + i + "'s name?");
			Player other = new RandomPlayer(otherPlayer, this);
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

	public int addPlayer(Player p) {
		getPlayers().add(p);
		return getPlayers().size() - 1;
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
						disPlayer.getHand().add(thisCard);
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

	public  ArrayList<Player> getPlayers() {
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

	public int getId() {
		return id;
	}
	//returns the index of a player on the players list
	public int getPlayerOrder(String name) {
		int order;
		ArrayList<Player> list = getPlayers();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName() == name) {
				order = i;
				return order;
			}
		}
		return -1;

	}

	
	public Player findPLayerIndexOf(int num){
		ArrayList<Player> players = getPlayers();
		if(num <= players.size()) {
			return players.get(num);
		}
		return null;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public void save() {
		
		// inserts all cards into db when the db does not already have cards
		boolean needsCards = CardDb.countCards(conn) <= 0;
		if (needsCards) {
			for (Card c : allCards) {
				CardDb cdb = new CardDb(c);
				cdb.insert(conn);
			}
		}
		// inserts all GameDb attributes into db
		String winner = null;
		String secretSuspect = secretCards[0].getName();
		String secretWeapon = secretCards[1].getName();
		String secretRoom = secretCards[2].getName();
		if (this.findWinningGuess() != null) {
			winner = this.findWinningGuess().getMadeBy().getName();
		}
		
		GameDb g = new GameDb(winner, secretSuspect, secretRoom, secretWeapon);
		this.setId(g.insert(conn));

		// inserts this games players to db and insert their hands
		for (Player p : players) {
			PlayerDb pDb = new PlayerDb(p);
			pDb.insert(conn);
			pDb.insertHand(conn, p.getHand());
		}

		
		
		// inserts this games guesses into db
		for (int i = 0; i < guessList.size(); ++i) {
			GuessDb gdb = new GuessDb(guessList.get(i), i, getId());
			gdb.insert(conn);

		}

	}

	public void cleanup() {
		try {
			conn.close();
		} catch (Exception e) {
			System.err.println("error closing connection of game " + getId() + " " + e.getLocalizedMessage());
		}

	}

	

}
