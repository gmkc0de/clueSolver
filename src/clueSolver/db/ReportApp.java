package clueSolver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReportApp {
	public Connection conn;
	public static final String FILTER_TABLE_NAME = "working_games";

	public static void main(String[] args) throws SQLException {
		Connection dbConn = SqliteUtil.connect();
		ReportApp n = new ReportApp(dbConn);
		n.createFilterTableForGamesWithNumPlayers(3);
		n.printAllReports();

//		System.out.println("--Three Player Games Report--");
//		System.out.println("total games in this section " + n.totalGames(true));
//		System.out.println("the longest game took: " + n.countLongestGame(true)+ " turns ");
//		System.out.println("the shortest game took: " + n.countShortestGame(true)+ " turns ");
//		System.out.println("the average turns to win was: " + n.averageTurnsToWin(true) );
//		System.out.println("the longest it took to win was: " + n.findPlayersNumGuesses(n.findLongestWinner(true), n.findLongestGame(true), true) + " turns");
//		System.out.println("total num guesses: " + n.totalGuesses(true));
//		System.out.println("wins per player: " + n.getWinsPerPLayer(true));
//		System.out.println("the first player advantage was: "+ n.calcFirstPLayerAdvantage(true));
//		
	}

	public ReportApp(Connection inConn) {
		conn = inConn;
	}

//	--Three Player Games Report--
//	total games in this section 2.0
//	the longest game took: 27 turns 
//	the shortest game took: 13 turns 
//	the average turns to win was: 7.0
//	the longest it took to win was: 9 turns
//	total num guesses: 40.0
//	wins per player: {anna=1, cinna=1}
//	the first player advantage was: 0.0
	private void printAllReports() {

		Integer numPlayers = 3;
		while (numPlayers <= 6) {
			createFilterTableForGamesWithNumPlayers(numPlayers);
			System.out.println("--" + numPlayers.toString() + " Player Games Report--");
			System.out.println("total games in this section " + totalGames(true));
			System.out.println("the longest game took: " + countLongestGame(true) + " turns ");
			System.out.println("the shortest game took: " + countShortestGame(true) + " turns ");
			System.out.println("the average turns to win was: " + averageTurnsToWin(true));
			System.out.println("the longest it took to win was: "
					+ findPlayersNumGuesses(findLongestWinner(true), findLongestGame(true), true) + " turns");
			System.out.println("total num guesses: " + totalGuesses(true));
			System.out.println("wins per player: " + getWinsPerPLayer(true));
			System.out.println("the first player advantage was: " + calcFirstPLayerAdvantage(true));
			numPlayers++;
		}

	}

	// select count(*), game_id from guess group by game_id order by count(*) desc
	// limit 1;
	public int countLongestGame(boolean useFilterTable) {

		int total = -1;
		String whereClause = "";

		if (useFilterTable) {
			whereClause = " where game_id in (select id from " + FILTER_TABLE_NAME + ") ";
		}
		String finalSql = "select count(*) as total, game_id " + "from guess " + whereClause + "group by game_id "
				+ "order by count(*) desc " + "limit 1";

		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(finalSql)) {
			if (rs.next()) {
				// read the result set
				total = rs.getInt("total");
			}
			return total;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
//	" select count(*) as total, game_id from guess " 
//	+ whereClause 
//	+ " group by game_id order by count(*) asc limit 1 ");

	public int countShortestGame(boolean useFilterTable) {
		int total = -1;
		String whereClause = "";
		if (useFilterTable) {
			whereClause = " where game_id in (select id from " + FILTER_TABLE_NAME + ") ";
		}
		String query = " select count(*) as total, game_id from guess " + whereClause
				+ " group by game_id order by count(*) asc limit 1 ";

		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(query);) {
			if (rs.next()) {
				// read the result set
				total = rs.getInt("total");
			}
			return total;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public double countTotalWinnersInGame(boolean useFilterTable) {
		String whereClause = "";
		double winners = -1;
		if (useFilterTable) {
			whereClause = " where id in (select id from " + FILTER_TABLE_NAME + ") ";
		}
		String query = "select count(winner) as winners from game " + whereClause;
		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(query);) {
			if (rs.next()) {
				// read the result set
				winners = rs.getInt("winners");
				return winners;
			}

			return winners;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// get all the guesses made and divide by num games
	public double averageGameSize(boolean useFilterTable) {

		double guessNum = totalGuesses(useFilterTable);
		double games = totalGames(useFilterTable);

		return guessNum / games;
	}

	// add up all the guesses made by a winning player then divide by number of
	// winning players
	// retunrs average num guesses for winners
	public double averageTurnsToWin(boolean useFilterTable) {

		double total = 0;
		String whereClause = "";
		if (useFilterTable) {
			whereClause = " game_id in (select id from " + FILTER_TABLE_NAME + ") and ";
		}

		String query = "select count(*) as total, guesser_name, game_id from guess where " + whereClause
				+ " (guesser_name, game_id) in (select winner, id from game) group by guesser_name, game_id;";
		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(query);) {
			while (rs.next()) {
				// read the result set

				total += rs.getInt("total");

			}

			return total / countTotalWinnersInGame(useFilterTable);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public int findPlayersNumGuesses(String nm, int gId, boolean useFilterTable) {
		// find the number of turns it took a player to win a certain game
		try {
			String name = nm;
			int gameId = gId;
			String whereClause = "";
			if (useFilterTable) {
				whereClause = " game_id in (select id from " + FILTER_TABLE_NAME + ") and ";
			}
			String sql = "select count(*) as count from guess where " + whereClause
					+ " guesser_name = ? and game_id = ? order by guess_number desc limit 1";

			try (PreparedStatement statement = conn.prepareStatement(sql);) {

				statement.setString(1, name);
				statement.setInt(2, gameId);
				try (ResultSet rs = statement.executeQuery();) {
					if (rs.next()) {
						int num = rs.getInt("count");
						return num;

					}
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return 0;
	}

	public String findLongestWinner(boolean useFilterTable) {
		try {

			int gameId = findLongestGame(useFilterTable);
			String whereClause = "";
			if (useFilterTable) {
				whereClause = " game_id in (select id from " + FILTER_TABLE_NAME + ") and ";
			}
			String sql = "select guesser_name from guess where " + whereClause
					+ " game_id = ? order by guess_number desc limit 1";
			try (PreparedStatement statement = conn.prepareStatement(sql);) {

				statement.setInt(1, gameId);
				try (ResultSet rs = statement.executeQuery();) {
					if (rs.next()) {
						String name = rs.getString("guesser_name");
						return name;

					}
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public int findLongestGame(boolean useFilterTable) {
		int gameId = -1;
		String whereClause = "";
		if (useFilterTable) {
			whereClause = " where game_id in (select id from " + FILTER_TABLE_NAME + ")";
		}

		String query = "select count(*) as total, game_id from guess " + whereClause
				+ "group by game_id order by count(*) desc limit 1";
		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(query);) {
			if (rs.next()) {
				gameId = rs.getInt("game_id");
				return gameId;
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return gameId;
	}

	public int findShortestGameId(boolean useFilterTable) {
		int gameId = -1;
		String whereClause = "";
		if (useFilterTable) {
			whereClause = " where game_id in (select id from " + FILTER_TABLE_NAME + ")";
		}
		String query = "select count(*) as total, game_id from guess " + whereClause
				+ " group by game_id order by count(*) asc limit 1";
		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(query);) {
			while (rs.next()) {
				// read the result set

				gameId = rs.getInt("game_id");
				return gameId;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return gameId;
	}

	// Find a list of all games with three players
	//
	public Map<Integer, Integer> findAllGamesWithNumPlayers(int num, boolean useFilterTable) {

		String whereClause = "";
		if (useFilterTable) {
			whereClause = " where game_id in (select id from " + FILTER_TABLE_NAME + ")";
		}
		Map<Integer, Integer> results = new HashMap<Integer, Integer>();
		String query = ("Select game_id, count(*) as play_num  from player " + whereClause
				+ " group by game_id  having  play_num = ? ");
		try (PreparedStatement statement = conn.prepareStatement(query);) {

			statement.setInt(1, num);
			try (ResultSet rs = statement.executeQuery();) {
				while (rs.next()) {
					int game_id = rs.getInt("game_id");
					int play_num = rs.getInt("play_num");
					results.put(game_id, play_num);

				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return results;
	}

	// create a table in sql with only the games that have the correct num players
	public void createFilterTableForGamesWithNumPlayers(int num) {
		try (Statement dropStatement = conn.createStatement();) {
			dropStatement.execute("drop table if exists working_games");
			dropStatement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		String sql = ("create table " + FILTER_TABLE_NAME
				+ " as select id from game where id in (select game_id from player group by game_id having count(*) = ?) ");
		
		try (PreparedStatement statement = conn.prepareStatement(sql);) {
			statement.setInt(1, num);
			statement.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public double totalGuesses(boolean useFilterTable) {
		double guessNum;
			String whereClause = "";
			if (useFilterTable) {
				whereClause = " where game_id in (select id from " + FILTER_TABLE_NAME + ")";
			}
			String sql = "select count(*) as guessNum from guess " + whereClause;
			
			try(Statement statement = conn.createStatement(); 
				ResultSet rs = statement.executeQuery(sql);){
				while (rs.next()) {
					// read the result set
					guessNum = rs.getInt("guessNum");
					return guessNum;
				}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	public double totalGames(boolean useFilterTable) {
		double games;
		try {
			String whereClause = "";
			if (useFilterTable) {
				whereClause = " where id in (select id from " + FILTER_TABLE_NAME + ")";
			}
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select count(*) as games from game " + whereClause);
			while (rs.next()) {
				// read the result set
				games = rs.getInt("games");
				return games;
			}

			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	// select count(*), guesser_name from guess group by guesser_name;
	public Map<String, Integer> getWinsPerPLayer(boolean useFilterTable) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		try {
			String whereClause = "";
			if (useFilterTable) {
				whereClause = " where id in (select id from " + FILTER_TABLE_NAME + ")";
			}
			String sql = "select count(*) as count, winner from game " + whereClause + " group by winner";
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				int count = rs.getInt("count");
				String name = rs.getString("winner");
				result.put(name, count);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public int getLongestGameId(boolean useFilterTable) {
		try {
			String whereClause = "";
			if (useFilterTable) {
				whereClause = " where id in (select id from " + FILTER_TABLE_NAME + ")";
			}
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select id from game " + whereClause + " order by id desc limit 1");
			while (rs.next()) {
				// read the result set

				int id = rs.getInt("id");
				return id;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	// Write a query that finds all the first turn players that win and compare that
	// to second turn third turn etc
	// to calc first player adv

	public double calcFirstPLayerAdvantage(boolean useFilterTable) {

		try {
			// original query
			// select count(*) as wins, name from player where turn_order = ? and (name,
			// game_id) in (select winner, id from game) group by name
			String whereClause = "";
			if (useFilterTable) {
				whereClause = " and game_id in (select id from " + FILTER_TABLE_NAME + ")";
			}
			String sql = "select count(*) as wins, turn_order from player where (name, game_id) in (select winner, id from game) "
					+ whereClause + " group by turn_order";
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();

			int firstPWins = 0;
			int secondPWins = 0;

			if (rs.next()) {
				firstPWins = rs.getInt("wins");
			}
			if (rs.next()) {
				secondPWins = rs.getInt("wins");
			}

			rs.close();
			statement.close();

			return firstPWins - secondPWins;
			// returns min value instead of -1 to avoid being confused for a proper answer
			// if there is ever a case where the advantage should be negative
			// (such as when the first player has less wins than the second)

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static ArrayList<String> setToSortedList(Set<String> set) {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(set);
		Collections.sort(list);
		return list;
	}
}
