package clueSolver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import clueSolver.Card;
import clueSolver.player.Player;

public class GameDb {
	int id;
	String winner;
	String secretSuspect;
	String secretRoom;
	String secretWeapon;

	private static String ALL_COLS = " winner, secret_suspect, secret_room, secret_weapon";

	public GameDb(String w, String secretSus, String secretRm, String secretWpn) {
		winner = w;
		secretSuspect = secretSus;
		secretRoom = secretRm;
		secretWeapon = secretWpn;

	}

	public GameDb(Player w, Card secretSus, Card secertRm, Card secretWpn) {
		winner = w.getName(); 
		secretSuspect = secretSus.getName();
		secretRoom = secertRm.getName();
		secretWeapon = secretWpn.getName();
	}

	public int insert(Connection conn) {
		try {
			String sql = "insert into game (" + ALL_COLS + ") values(?,?,?,?)";
			PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, winner);
			statement.setString(2, secretSuspect);
			statement.setString(3, secretRoom);
			statement.setString(4, secretWeapon);
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				int gId = rs.getInt(1);
				this.id = gId;
			}
			rs.close();
			statement.close();
			return this.id;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
