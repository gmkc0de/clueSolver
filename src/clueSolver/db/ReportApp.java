package clueSolver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import clueSolver.Game;

public class ReportApp {
	public Connection conn;

	/*
	 * anna won 259.0 times. which is 25.90% of the time ben won 259.0 times. which
	 * is 25.90% of the time cinna won 212.0 times. which is 21.20% of the time dane
	 * won 138.0 times. which is 13.80% of the time emily won 132.0 times. which is
	 * 13.20% of the time >>the first player has an advantage over the others by
	 * 0.00 >>longest game: 71 turns, shortest game: 1 turns, average number of
	 * turns: 39.725 >>average num guesses for winner 8 >>the longest it took to win
	 * was 14.2 turns
	 */

	public static void main(String[] args) {
		Connection dbConn = SqliteUtil.connect();
		ReportApp n = new ReportApp(dbConn);
		System.out.println("longest game: " + n.countLongestGame() + " turns " + ", shortest game: "
				+ n.countShortestGame() + ", turns average game: " + n.averageGameSize());
		System.out.println("average num guesses for winner " + n.averageTurnsToWin());
		System.out.println("the longest it took to win was "
				+ n.findPlayersNumGuesses(n.findLongestWinner(), n.findLongestGameId()) + " turns");
		Map<String, Integer> wins = n.getWinsPerPLayer();
		ArrayList<String> winners = ReportApp.setToSortedList(wins.keySet());
		for (String winner : winners) {
			Integer numWins = wins.get(winner);
			String output = String.format("%s won %s times which is %.2f percent of the time", winner, numWins,
					(((double) numWins) / n.totalGames()) * 100);
			System.out.println(output);

		}
		System.out.println("the first player advantage was "  );

	}

	public ReportApp(Connection inConn) {
		conn = inConn;
	}

	// select count(*), game_id from guess group by game_id order by count(*) desc
	// limit 1;
	public int countLongestGame() {
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(
					"select count(*) as total, game_id from guess group by game_id order by count(*) desc limit 1");
			while (rs.next()) {
				// read the result set

				int total = rs.getInt("total");
				return total;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	public int countShortestGame() {
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(
					"select count(*) as total, game_id from guess group by game_id order by count(*) asc limit 1");
			while (rs.next()) {
				// read the result set

				int total = rs.getInt("total");
				return total;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	// get all the guesses made and divide by num games
	public double averageGameSize() {
		double guessNum = totalGuesses();
		double games = totalGames();

		return guessNum / games;
	}

	// add up all the guesses made by a winning player then divide by number of
	// winning players
	// retunrs average num guesses for winners
	public double averageTurnsToWin() {
		try {
			int total = 0;
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(
					"select count(*) as total, guesser_name, game_id from guess where (guesser_name, game_id) in (select winner, id from game) group by guesser_name, game_id;");
			while (rs.next()) {
				// read the result set

				total += rs.getInt("total");

			}

			rs.close();
			statement.close();
			return total / countTotalWinners();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public int findPlayersNumGuesses(String nm, int gId) {
		// find the number of turns it took a player to win a certain game
		try {
			String name = nm;
			int gameId = gId;
			String sql = "select count(*) as count from guess where guesser_name = ? and game_id = ? order by guess_number desc limit 1";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, name);
			statement.setInt(2, gameId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				int num = rs.getInt("count");
				return num;

			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return 0;
	}

	public String findLongestWinner() {
		try {
			int gameId = findLongestGameId();
			String sql = "select guesser_name from guess where game_id = ? order by guess_number desc limit 1";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, gameId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String name = rs.getString("guesser_name");
				return name;

			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public int findLongestGameId() {
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(
					"select count(*) as total, game_id from guess group by game_id order by count(*) desc limit 1");
			while (rs.next()) {
				// read the result set

				int gameId = rs.getInt("game_id");
				return gameId;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	public int findShortestGame() {
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(
					"select count(*) as total, game_id from guess group by game_id order by count(*) asc limit 1");
			while (rs.next()) {
				// read the result set

				int gameId = rs.getInt("game_id");
				return gameId;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	public double totalGuesses() {
		double guessNum;
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select count(*) as guessNum from guess ");
			while (rs.next()) {
				// read the result set
				guessNum = rs.getInt("guessNum");
				return guessNum;
			}

			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	public double totalGames() {
		double games;
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select count(*) as games from game ");
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

	public double countTotalWinners() {
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select count(winner) as winners from game");
			while (rs.next()) {
				// read the result set
				double winners = rs.getInt("winners");
				return winners;
			}

			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

	// select count(*), guesser_name from guess group by guesser_name;
	public Map<String, Integer> getWinsPerPLayer() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		try {
			String sql = "select count(*) as count, winner from game group by winner";
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

	public int getLongestGameId() {
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select id from game order by id desc limit 1");
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

	public double calcFirstPLayerAdvantage() {
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select id from game order by id desc limit 1");
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

	public static ArrayList<String> setToSortedList(Set<String> set) {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(set);
		Collections.sort(list);
		return list;
	}
}
