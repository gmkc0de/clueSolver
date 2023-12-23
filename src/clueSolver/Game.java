package clueSolver;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import clueSolver.db.CardDb;
import clueSolver.db.GameDb;
import clueSolver.db.GuessDb;
import clueSolver.db.PlayerDb;
import clueSolver.db.SqliteUtil;
import clueSolver.player.Player;
import clueSolver.player.RandomPlayer;
import clueSolver.player.SmartPlayer;

public class Game {

	protected int id;
	protected List<Player> players;
	protected Card[] secretCards;
	protected Card[] allCards;
	protected List<Guess> guessList;
	protected List<String> testPlayerNames;
	protected Connection conn;

	public Game() {

		players = new ArrayList<Player>();
		secretCards = new Card[3];
		guessList = new ArrayList<Guess>();
		allCards = new Card[21];
		allCards[0] = Card.BALLROOM;
		allCards[1] = Card.CONSERVATORY;
		allCards[2] = Card.HALL;
		allCards[3] = Card.STUDY;
		allCards[4] = Card.LIBRARY;
		allCards[5] = Card.LOUNGE;
		allCards[6] = Card.KITCHEN;
		allCards[7] = Card.DININGROOM;
		allCards[8] = Card.BILLIARDROOM;
		allCards[9] = Card.LEADPIPE;
		allCards[10] = Card.ROPE;
		allCards[11] = Card.REVOLVER;
		allCards[12] = Card.WRENCH;
		allCards[13] = Card.KNIFE;
		allCards[14] = Card.CANDLESTICK;
		allCards[15] = Card.MUSTARD;
		allCards[16] = Card.PEACOCK;
		allCards[17] = Card.SCARLET;
		allCards[18] = Card.PLUM;
		allCards[19] = Card.WHITE;
		allCards[20] = Card.GREEN;

		testPlayerNames = new ArrayList<String>();
		testPlayerNames.add("anna");
		testPlayerNames.add("ben");
		testPlayerNames.add("cinna");
		testPlayerNames.add("dane");
		testPlayerNames.add("emily");
		testPlayerNames.add("fiona");

		conn = SqliteUtil.connect();

	}

	// public static
	public static Game createTestGame(int numPlayers, Game game) {
		for (int i = 0; i < numPlayers; i++) {
			String name = game.testPlayerNames.get(i);
			if (i == 0) {
				Player a = new SmartPlayer(name, game);
				int order = game.addPlayer(a);
				a.setOrder(order);
			} else {
				Player a = new RandomPlayer(name, game);
				int order = game.addPlayer(a);
				a.setOrder(order);
			}

		}

		List<Card> cardList = new ArrayList<Card>();
		for (Card c : game.getAllCards()) {
			cardList.add(c);

		}
		L.i(">>test game created<<");
		return game;

	}
	
	public static Game createTestGame(int numPlayers) {
		Game game = new Game();
		return createTestGame(numPlayers, game);
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
		List<Card> toDeal = addAll(allCards);
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
			++numCardsDealt;

		}
	}

	public void oldDealCards() {
		// deal the three secret cards
		String whatType = "suspect";
		String nextType = "weapon";
		List<Card> availibleCards = addAll(allCards);

		for (int i = 0; i < 3; i++) {
			if (availibleCards.size() != 0) {
				List<Card> typeList = getAllOfType(whatType);
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
	private List<Player> allPlayersButThis(Player guesser) {
		return allPlayersButThis(getPlayers(), guesser);
	}

	// returns the list of players you put in in the original order minus the player
	// you put in
	protected static List<Player> allPlayersButThis(List<Player> players, Player guesser) {
		List<Player> allButThis = new ArrayList<Player>();
		allButThis.addAll(players);
		allButThis.remove(guesser);
		return allButThis;
	}

	// returns a list of players minus the guesser IN THE CORRECT GUESSING ORDER
	protected static List<Player> disprovePlayerList(List<Player> players, Player guesser) {
		List<Player> newPlayerOrder = new ArrayList<Player>();
		int guesserIndex = players.indexOf(guesser);
		int numPlayers = players.size();
		for (int i = 0; i < numPlayers - 1; i++) {
			int nextIndex = (guesserIndex + 1 + i) % numPlayers;
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
		List<Player> possibleDisprovers = disprovePlayerList(getPlayers(), guesser);

		for (Player p : possibleDisprovers) {
			// checks if any players can disprove the guess
			Card dis = p.disproveGuess(guess);
			if (dis != null) {
				// fills in cards if player can disprove
				guess.setDisprovePerson(p);
				guess.setDisproveCard(dis);
				break;
			}

		}
		return guess;
	}

	public List<Card> findUnknownCards(Player p) {
		List<Card> playerClues = findPlayerClues(p);
		Card[] allCards = getAllCards();
		List<Card> unknown = addAll(allCards);
		unknown.removeAll(playerClues);

		return unknown;
	}

	public List<Card> findUnknownSuspects(Player p) {
		List<Card> playerClues = findPlayerClues(p);
		List<Card> allSus = getAllSuspects();
		List<Card> unknown = addAll(allSus);
		unknown.removeAll(playerClues);

		return unknown;
	}

	public List<Card> findUnknownRooms(Player p) {
		List<Card> playerClues = findPlayerClues(p);
		List<Card> allRooms = getAllRooms();
		List<Card> unknown = addAll(allRooms);
		unknown.removeAll(playerClues);

		return unknown;

	}

	public List<Card> findUnknownWeapons(Player p) {
		List<Card> playerClues = findPlayerClues(p);
		List<Card> allWeapon = getAllWeapons();
		List<Card> unknown = addAll(allWeapon);
		unknown.removeAll(playerClues);
		return unknown;

	}

	// clues know to the player (in their hand or shown by another player)
	public List<Card> findPlayerClues(Player p) {
		// add all the clues in the players hand
		List<Card> playerClues = addAll(p.getHand());
		List<Guess> playerGuesses = findPLayerGuesses(p);
		for (Guess g : playerGuesses) {
			if (g.getDisprovingCard() != null && !playerClues.contains(g.getDisprovingCard())) {
				playerClues.add(g.getDisprovingCard());
			}
		}
		// returns a list of all the cards in players hand
		// as well as a list of all the disproving cards they player has seen so far
		return playerClues;
	}

	public List<Guess> findPlayerGuessesStream(Player p) {
		// returns list of all guesses made by player p

		List<Guess> playerGuess = guessList.stream().filter(g -> g.getGuesser().equals(p)).collect(Collectors.toList());
		return playerGuess;
	}

	public List<Guess> findPlayerGuessesNotDisproven(Player p) {

		List<Guess> playerGuesses = findPlayerGuessesStream(p);

		List<Guess> playerGuess = playerGuesses.stream().filter(g -> !g.isDisproved()).collect(Collectors.toList());

		return playerGuess;

	}

	// findProvenCardOfType() -> null if not yet found, otherwise returns the card
	public List<Card> findProvenCards(Player p) {

		List<Guess> notDisprovedGuesses = findPlayerGuessesNotDisproven(p);
		Set<Card> cardsFromGuesses = new HashSet<Card>();
		List<Card> provenCards = new ArrayList<Card>();

		notDisprovedGuesses.stream().forEach(g -> cardsFromGuesses.addAll(g.getCards()));

		provenCards = cardsFromGuesses.stream().filter(c -> !(p.isCardInHand(c))).toList();

		return provenCards;
	}

	// findProvenCardOfType() -> null if not yet found, otherwise returns the card
	public Card findProvenCardOfType(Player p, String type) {

		// find all not disproven guesses
		List<Guess> notDisproved = findPlayerGuessesNotDisproven(p);
		List<Card> cardsOfTypeFromGuesses = new ArrayList<Card>();
		List<Card> provenCards = new ArrayList<Card>();

		notDisproved.stream()
				// get all the cards from those guesses
				.forEach(g -> cardsOfTypeFromGuesses.add(g.getCardOfTypeFromGuess(type)));

		// find which cards are not in our hand - those cards are "proven"
		provenCards = cardsOfTypeFromGuesses.stream().filter(c -> !(p.isCardInHand(c))).toList();

		if (provenCards.size() > 0) {
			return provenCards.get(0);
		} else {
			return null;
		}
	}

	public List<Guess> findPLayerGuesses(Player p) {
		List<Guess> playerList = new ArrayList<Guess>();
		for (Guess g : guessList) {
			if (g.getGuesser().equals(p)) {
				playerList.add(g);
			}
		}
		return playerList;
	}

	public Guess findWinningGuess() {
		// returns null unless all the cards in a guess match the secret cards in wich
		// case it returns tht guess

		for (Guess g : guessList) {
			if (g.getSuspect().equals(secretCards[0]) && g.getWeapon().equals(secretCards[1])
					&& g.getRoom().equals(secretCards[2])) {
				return g;
			}
		}
		return null;
	}

	protected Card findRandomSuspect() {
		List<Card> s = getAllSuspects();
		int num = (int) (Math.random() * s.size());

		return s.get(num);
	}

	protected Card findRandomRoom() {
		List<Card> r = getAllRooms();
		int num = (int) (Math.random() * r.size());
		return r.get(num);
	}

	protected Card findRandomWeapon() {
		List<Card> w = getAllWeapons();
		int num = (int) (Math.random() * w.size());

		return w.get(num);
	}

	public boolean hasWinningGuess() {
		if (findWinningGuess() != null) {

			return true;
		}
		return false;
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

	public <T extends Player> void addPlayerWithHand(String name, Class<T> playerType, Card... cards) {

		Player player = createPlayerOfType(playerType);
		List<Card> hand = Arrays.asList(cards);
		player.setName(name);
		player.setHand(hand);
		player.setCurrentGame(this);
		player.setIsComputer(true);
		this.addPlayer(player);
		
	}
	
    public static <T extends Player> T createPlayerOfType(Class<T> typeOfPlayer){
        try {
        	Constructor<T> constructor = typeOfPlayer.getDeclaredConstructor();
            return constructor.newInstance();
        }
        catch(Exception e) {
        	throw new RuntimeException(e);
        }
    }

	public List<Card> addAll(List<Card> list) {
		List<Card> added = new ArrayList<Card>();
		for (int i = 0; i < list.size(); i++) {
			added.add(list.get(i));
		}
		return added;
	}

	public List<Card> addAll(Card[] list) {
		List<Card> added = new ArrayList<Card>();
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

	public List<Guess> getGuessList() {
		return guessList;
	}

	private List<Card> getAllSuspects() {
		List<Card> sus = new ArrayList<Card>();
		for (Card c : allCards) {
			if (c.getType().equals("suspect")) {
				sus.add(c);
			}
		}
		return sus;
	}

	private List<Card> getAllWeapons() {
		List<Card> w = new ArrayList<Card>();
		for (Card c : allCards) {
			if (c.getType().equals("weapon")) {
				w.add(c);
			}
		}
		return w;
	}

	private List<Card> getAllRooms() {
		List<Card> r = new ArrayList<Card>();
		for (Card c : allCards) {
			if (c.getType().equals("room")) {
				r.add(c);
			}
		}
		return r;

	}

	public List<Card> getAllOfType(String whatType) {
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

	protected Card[] getSecretCards() {
		return secretCards;
	}

	public Card[] getAllCards() {
		return allCards;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Player getMyPlayer() {
		return players.get(0);
	}

	private List<Card> getSuspectsFrom(List<Card> list) {
		List<Card> sus = new ArrayList<Card>();
		for (Card c : list) {
			if (c.getType().equals("suspect")) {
				sus.add(c);
			}
		}
		return sus;
	}

	private List<Card> getWeaponsFrom(List<Card> list) {
		List<Card> w = new ArrayList<Card>();
		for (Card c : list) {
			if (c.getType().equals("suspect")) {
				w.add(c);
			}
		}
		return w;
	}

	private List<Card> getRoomsFrom(List<Card> list) {
		List<Card> r = new ArrayList<Card>();
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
	
	public void setSecretCards(Suspect suspect, Weapon weapon, Room room) {

		this.secretCards[0] = suspect;
		this.secretCards[1] = weapon;
		this.secretCards[2] = room;

		
	}


	public void setAllCards(Card[] allCards) {
		this.allCards = allCards;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public int getId() {
		return id;
	}

	// returns the index of a player on the players list
	public int getPlayerOrder(String name) {
		int order;
		List<Player> list = getPlayers();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName() == name) {
				order = i;
				return order;
			}
		}
		return -1;

	}

	public Player findPLayerIndexOf(int num) {
		List<Player> players = getPlayers();
		if (num <= players.size()) {
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

	// TODO move the methods below to a game util class
	public static List<Card> getAllCardsWithType(String type, List<Card> cards) {
		List<Card> typeCardList = new ArrayList<Card>();
		for (Card c : cards) {
			if (c.getType().equals(type)) {
				typeCardList.add(c);
			}
		}
		return typeCardList;
	}

	public static Card getRandomCardFromListWithType(String type, List<Card> cards) {
		List<Card> choose = getAllCardsWithType(type, cards);
		if (choose != null && choose.size() > 0) {
			Card c = choose.get(((int) (Math.random() * choose.size())));

			return c;
		} else {
			return null;
		}

	}

	public Card getRandomCardFromDeck() {
		Card[] allCards = this.getAllCards();
		return allCards[((int) (Math.random() * allCards.length))];

	}

	public Card getRandomCardOfATypeFromDeck(String type) {
		Card[] allCards = this.getAllCards();
		List<Card> ofType = new ArrayList<Card>();
		for (Card c : allCards) {
			if (c.getType() == type) {
				ofType.add(c);
			}

		}
		return ofType.get((int) (Math.random() * ofType.size()));
	}

}
