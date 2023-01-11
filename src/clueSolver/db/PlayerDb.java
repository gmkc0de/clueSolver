package clueSolver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import clueSolver.Player;

public class PlayerDb {
	String name;
	int gameId;
	boolean isComputer;
	int turnOrder;
	
	
	public PlayerDb(String nm, int gI, boolean isComp, int order) {
		name = nm;
		gameId = gI;
		isComputer = isComp;
		turnOrder = order;
	}
	
	public PlayerDb(Player p) {
		name = p.getName();
		gameId = p.getCurrentGame().getId();
		isComputer = p.isComputer();
		turnOrder = p.getTurnOrder();
	}
	
	public static String ALL_COLS = "name, game_id, is_computer, turn_order";
	
	public void insert(Connection conn)  {
		
		try {
			String sql = "insert into player("+ ALL_COLS +") values(?,?,?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, name);
			statement.setInt(2, gameId);
			statement.setBoolean(3, isComputer);
			statement.setInt(4, turnOrder);
			statement.execute();
			statement.close();
		}
		catch(SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
