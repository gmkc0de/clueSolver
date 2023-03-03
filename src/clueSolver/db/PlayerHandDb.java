package clueSolver.db;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class PlayerHandDb {

	int playerId;
	int gameId;
	String cardName;
	
	private static String ALL_COLS = " game_id, player_id, card_name ";

	public PlayerHandDb(int gId, int pId, String cName) {
		playerId = pId;
		gameId = gId;
		cardName = cName;
	}

	public void insert(Connection conn) {
		try {
			String sql = "insert into player_card (" + ALL_COLS + ") values(?,?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, gameId);
			statement.setInt(2, playerId);
			statement.setString(3, cardName);
			statement.executeUpdate();
			statement.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main (String[] args) throws SQLException {
		Connection conn = SqliteUtil.connect();
		PlayerHandDb p = new PlayerHandDb(1,3, "wrench");
		p.insert(conn);
		conn.close();
		
	}

}
