package clueSolver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import clueSolver.Player;

public class ReportApp {
	public Connection conn;
	public static final String FILTER_TABLE_NAME = "working_games";

	public static void main(String[] args) throws SQLException {
		Connection dbConn = SqliteUtil.connect();
		ReportApp n = new ReportApp(dbConn);
		n.createFilterTableForGamesWithNumPlayers(3);
		n.printAllReports();

	}

	public ReportApp(Connection inConn) {
		conn = inConn;
	}

	private void printAllReports() {

		Integer numPlayers = 3;
		while (numPlayers <= 7) {
			boolean useFilter = numPlayers != 7;
			createFilterTableForGamesWithNumPlayers(numPlayers);
			System.out.println("");
			if (useFilter) {
				System.out.println("--" + numPlayers.toString() + " Player Games Report--");
			} else {
				System.out.println("-- All Player Games Report--");
			}
			System.out.println("total games in this section " + (int) totalGames(useFilter));
			System.out.println("the longest game took: " + countLongestGame(useFilter) + " turns ");
			System.out.println("the shortest game took: " + countShortestGame(useFilter) + " turns ");
			System.out.println(String.format("the average turns to win was: %.2f%%", averageTurnsToWin(useFilter)));
			System.out.println("the longest it took to win was: "
					+ findPlayersNumGuesses(findLongestWinner(useFilter), findLongestGame(useFilter), useFilter)
					+ " turns");
			System.out.println("total num guesses: " + (int) totalGuesses(useFilter));
			System.out.println("wins per player:  " + getWinsPerPLayer(useFilter).stream()
					.sorted(Comparator.reverseOrder()).collect(Collectors.toList()));

			System.out.println(
					String.format("the first player advantage was: %.2f%%", calcFirstPLayerAdvantage(useFilter)));
			System.out.println(
					String.format("the last player disadvantage was:  %.2f%% ", calclastPlayerDisadvantage(useFilter)));
			// System.out.println(String.format(" the most cards advantage was %.2f ", calcMostCardsAdvantage(useFilter)));
			numPlayers++;
		}

	}

	private Object calcMostCardsAdvantage(boolean useFilterTable) {
		String whereClause = "";
		PlayerDb mostCards;
		PlayerDb secondMostCards;
		int counter = 0;
		if (useFilterTable) {
			whereClause = " where p.game_id in (select id from " + FILTER_TABLE_NAME + ") ";
		}
		String sqlOrder = " group by p.name, p.id, p.game_id order by count(*) desc ";
		String sql = "select p.name, p.id, p.game_id, count(*) from player p inner join player_card pc " + whereClause + sqlOrder;
		
		
		
		try(Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(sql)){
			
			while(rs.next() && counter < 2) {
				
				if (counter == 0) {
					mostCards = PlayerDb.findPlayerById(rs.getInt(2),conn).findThisPlayersWins();
				}
				if(counter == 0) {
					secondMostCards = PlayerDb.findPlayerById(rs.getInt(2),conn);
				}
				counter ++;
			}
			
			
			
			
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
		
		
		return null;
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

		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(sql);) {
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
			try (Statement statement = conn.createStatement();
					ResultSet rs = statement.executeQuery("select count(*) as games from game " + whereClause);) {
				while (rs.next()) {
					// read the result set
					games = rs.getInt("games");
					return games;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	// select count(*), guesser_name from guess group by guesser_name;
	public List<PlayerWins> getWinsPerPLayer(boolean useFilterTable) {
		List<PlayerWins> result = new ArrayList<PlayerWins>();
		String whereClause = "";
		if (useFilterTable) {
			whereClause = " where id in (select id from " + FILTER_TABLE_NAME + ")";
		}
		String sql = "select count(*) as count, winner from game " + whereClause + " group by winner ";
		try (PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery();) {

			while (rs.next()) {
				int count = rs.getInt("count");
				String name = rs.getString("winner");
				PlayerWins data = new PlayerWins(name, count);
				result.add(data);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public int getLongestGameId(boolean useFilterTable) {

		String whereClause = "";
		if (useFilterTable) {
			whereClause = " where id in (select id from " + FILTER_TABLE_NAME + ")";
		}
		String sql = "select id from game " + whereClause + " order by id desc limit 1";
		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
			while (rs.next()) {
				// read the result set

				int id = rs.getInt("id");
				return id;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	// Write a query that finds all the first turn players that win and compare that
	// to second turn third turn etc
	// to calc first player adv

	public double calcFirstPLayerAdvantage(boolean useFilterTable) {
		List<Integer> numWinsList = new ArrayList<Integer>();
		int totalGames = 0;
		String whereClause = "";
		if (useFilterTable) {
			whereClause = " and game_id in (select id from " + FILTER_TABLE_NAME + ")";
		}
//   wins  turn_order
//		34|0
//		21|1
//		21|2
//		14|3
//		1|4
//		9|5

		String sql = "select count(*) as wins, turn_order from player where (name, game_id) in (select winner, id from game) "
				+ whereClause + " group by turn_order order by turn_order asc";
		try (PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery();) {

			while (rs.next()) {
				numWinsList.add(rs.getInt("wins"));
			}
			if (numWinsList.size() > 1) {
				double firstPWins = (double) numWinsList.get(0);
				double secondPWins = (double) numWinsList.get(1);

				for (int wins : numWinsList) {
					totalGames += wins;
				}
//			
				return ((firstPWins - secondPWins) / totalGames) * 100;
			} else {
				return -1;
			}
			// returns min value instead of -1 to avoid being confused for a proper answer
			// if there is ever a case where the advantage should be negative
			// (such as when the first player has less wins than the second)

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public double calclastPlayerDisadvantage(boolean useFilterTable) {
		List<Integer> numWinsList = new ArrayList<Integer>();
		int totalGames = 0;
		String whereClause = "";
		if (useFilterTable) {
			whereClause = " and game_id in (select id from " + FILTER_TABLE_NAME + ")";
		}
		String sql = "select count(*) as wins, turn_order from player where (name, game_id) in (select winner, id from game) "
				+ whereClause + " group by turn_order order by turn_order asc";
		try (PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery();) {

			while (rs.next()) {
				numWinsList.add(rs.getInt("wins"));
			}

			if (numWinsList.size() > 1) {
				double firstPWins = (double) numWinsList.get(0);
				double lastPWins = (double) numWinsList.get(numWinsList.size() - 1);

				for (int wins : numWinsList) {
					totalGames += wins;
				}
				return ((lastPWins - firstPWins) / totalGames) * 100;
			} else {
				return -1;
			}

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
