package clueSolver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import clueSolver.Guess;

public class GuessDb {
	int id;
	int gameId;
	int guessNumber;
	String guesserName;
	String suspect;
	String weapon;
	String room;
	boolean isDisproved;
	String disproveCard;
	String disprovePlayer;
	
	public static String ALL_COLS = "game_id,guess_number, guesser_name, suspect, weapon, room, is_disproved, disprove_card, disprove_player";
	
	public GuessDb(Guess g, int guessNum, int gId) {
		super();
		gameId = gId;
		guessNumber = guessNum;
		guesserName = g.getGuesser().getName();
		suspect = g.getSuspect().getName();
		weapon = g.getWeapon().getName();
		room = g.getRoom().getName();
		isDisproved = g.isDisproved();
		if (g.getDisprovingCard() != null) {
			disproveCard = g.getDisprovingCard().getName();
		}
		if (g.getDisprovePlayer() != null) {
			disprovePlayer = g.getDisprovePlayer().getName();

		}
	}
	
	public GuessDb(int id, int gameId, int guessNumber, String guesserName, String suspect, String weapon, String room,
			boolean isDisproved, String disprovedCard, String disprovedPlayer) {
		super();
		this.id = id;
		this.gameId = gameId;
		this.guessNumber = guessNumber;
		this.guesserName = guesserName;
		this.suspect = suspect;
		this.weapon = weapon;
		this.room = room;
		this.isDisproved = isDisproved;
		this.disproveCard = disprovedCard;
		this.disprovePlayer = disprovedPlayer;
	}

	
	public void insert(Connection conn) {
		try {
		String sql = "insert into guess("+ALL_COLS+") values(?,?,?,?,?,?,?,?,?)";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, gameId);
		statement.setInt(2, guessNumber);
		statement.setString(3, guesserName);
		statement.setString(4, suspect);
		statement.setString(5, weapon);
		statement.setString(6, room);
		statement.setBoolean(7, isDisproved);
		statement.setString(8, disproveCard);
		statement.setString(9, disprovePlayer);
		statement.execute();
		statement.close();
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void update(Connection conn) throws SQLException {
		// select * from guess where id = 5;
		// insert into guess (id, name) values (1, 'ben');
		// update guess set name = 'glynis' where id = 1;
		//update guess set col_name = ?, col_name = ? ... where id = ?	
		String sql = "update guess set game_id = ?, guess_number = ?, guesser_name = ?,suspect = ?, weapon = ?, room = ?, is_disproved = ?, disproved_card = ?, disproved_player = ? where id = ?";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, gameId);
		statement.setInt(2, guessNumber);
		statement.setString(3, guesserName);
		statement.setString(4, suspect);
		statement.setString(5, weapon);
		statement.setString(6, room);
		statement.setBoolean(7, isDisproved);
		statement.setString(8, disproveCard);
		statement.setString(9, disprovePlayer);
		statement.setInt(10, id);
		statement.execute();
		statement.close();
	}

	public static GuessDb findById(int id, Connection conn) throws SQLException {
		String sql = "select "+ALL_COLS+" from guess where id = ?";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, id);
		ResultSet rs = statement.executeQuery();
		GuessDb guess = null;
		if(rs.next()) {
			guess = rsToGuessDb(rs);			
		}
		statement.close();
		rs.close();
		return guess;
	}
	
	public static List<GuessDb> findByGuesserName(String guesserName, Connection conn) throws SQLException {
		
		String sql = "select "+ALL_COLS+" from guess where guesser_name = ?"; 
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, guesserName); 
		ResultSet rs = statement.executeQuery();
		List<GuessDb> guessList = new ArrayList<GuessDb>();
		while(rs.next()) {
			GuessDb guess = rsToGuessDb(rs);
			guessList.add(guess);
		}
		statement.close();
		rs.close();
		return guessList;

	}

	private static GuessDb rsToGuessDb(ResultSet rs) throws SQLException {
		GuessDb guess = null;
		int id = rs.getInt("id");
		int gameId = rs.getInt("game_id");
		int guessNumber = rs.getInt("guess_number");
		String name = rs.getString("guesser_name");
		String room = rs.getString("room");
		String suspect = rs.getString("suspect");
		String weapon = rs.getString("weapon");
		boolean isDisproved = rs.getBoolean("is_disproved");
		String disprovedCard = rs.getString("disproved_card");
		String disprovedPlayer = rs.getString("disproved_player");

		guess = new GuessDb(id, gameId, guessNumber, name, suspect, weapon, room, isDisproved, disprovedCard, disprovedPlayer);
		return guess;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGuessNumber() {
		return guessNumber;
	}

	public void setGuessNumber(int guessNumber) {
		this.guessNumber = guessNumber;
	}

	public String getGuesserName() {
		return guesserName;
	}

	public void setGuesserName(String guesserName) {
		this.guesserName = guesserName;
	}

	public String getSuspect() {
		return suspect;
	}

	public void setSuspect(String suspect) {
		this.suspect = suspect;
	}

	public String getWeapon() {
		return weapon;
	}

	public void setWeapon(String weapon) {
		this.weapon = weapon;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public boolean isDisproved() {
		return isDisproved;
	}

	public void setDisproved(boolean isDisproved) {
		this.isDisproved = isDisproved;
	}

	public String getDisproveCard() {
		return disproveCard;
	}

	public void setDisproveCard(String disprovedCard) {
		this.disproveCard = disprovedCard;
	}

	public String getDisprovePlayer() {
		return disprovePlayer;
	}

	public void setDisprovePlayer(String disprovedPlayer) {
		this.disprovePlayer = disprovedPlayer;
	}

	@Override
	public String toString() {
		return String.format(
				"GuessDb [id=%s, gameId=%s, guessNumber=%s, guesserName=%s, suspect=%s, weapon=%s, room=%s, isDisproved=%s, disprovedCard=%s, disprovedPlayer=%s]",
				id, gameId, guessNumber, guesserName, suspect, weapon, room, isDisproved, disproveCard,
				disprovePlayer);
	}
}
