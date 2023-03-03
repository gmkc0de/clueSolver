package clueSolver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import clueSolver.Card;
import clueSolver.Player;

public class PlayerDb {
	int id;
	String name;
	int gameId;
	boolean isComputer;
	int turnOrder;
	
	public PlayerDb(Player p) {
		name = p.getName();
		gameId = p.getCurrentGame().getId();
		isComputer = p.isComputer();
		turnOrder = p.getTurnOrder();
	}

	public static String ALL_COLS = "name, game_id, is_computer, turn_order";

	public void insertHand(Connection conn, List<Card> hand) {
		int gId = this.gameId;
		int pId = this.id;
		for(Card c : hand) {
			PlayerHandDb handDb = new PlayerHandDb(gId,pId,c.getName());
			handDb.insert(conn);
		}
	}

	public void insert(Connection conn) {
		// insert a player into the db
		try {
			String sql = "insert into player(" + ALL_COLS + ") values(?,?,?,?)";
			PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, name);
			statement.setInt(2, gameId);
			statement.setBoolean(3, isComputer);
			statement.setInt(4, turnOrder);
			statement.execute();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				this.id = rs.getInt(1);				
			}
			rs.close();
			statement.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
	public static PlayerDb findPlayerById(int id, Connection conn) {
		try {
		String sql = "select * from player where id = ? ";
		
		
		try(PreparedStatement statement = conn.prepareStatement(sql)){
			statement.setInt(1,id);
			
			try(ResultSet rs = statement.executeQuery(sql)){
				if(rs.next()) {
					PlayerDb p = (PlayerDb) rs.getObject(0);
					return p;
				}
			}
		}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		
		return null;
		
	}
	
	public PlayerDb findThisPlayersWins() {
		
		return null;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	

}
