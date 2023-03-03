package clueSolver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import clueSolver.Card;

public class CardDb {
	String name;
	String type;

	public CardDb() {
		super();
	}

	public CardDb(String n, String t) {
		name = n;
		type = t;
	}

	public CardDb(Card c) {
		name = c.getName();
		type = c.getType();
	}

	static String ALL_COLS = "name, type";

	public static CardDb findCardWithName(Connection conn, String name) {
		CardDb cardDb = new CardDb();
		String sql = " select name, type from card where name = ? ";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, name);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				cardDb.setName(rs.getString(1));
				cardDb.setType(rs.getString(2));
				statement.close();
				rs.close();
				return cardDb;
			} else {
				// if card with the name provided is not found
				statement.close();
				rs.close();
				return null;
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void insert(Connection conn) {

		try {
			String sql = "insert into card(" + ALL_COLS + ") values(?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, name);
			statement.setString(2, type);
			statement.execute();
			statement.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static int countCards(Connection conn) {
		// count the number of cards in the database
		try {
			int count = 0;
			String sql = "select count(*) from card";
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			statement.close();
			rs.close();

			return count;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
