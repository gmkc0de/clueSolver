package clueSolver.db;

public class PlayerWins implements Comparable<PlayerWins> {
	public String name;
	public int wins;
	public double totalGames;

	PlayerWins(String name, int wins, double totalGames) {
		this.name = name;
		this.wins = wins;
		this.totalGames = totalGames;
	}

	PlayerWins() {

	}

// method land
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public double getTotalGames() {
		return totalGames;
	}

	public void setTotalGames(double totalGames) {
		this.totalGames = totalGames;
	}
	
	public double calcWinPercentage() {
		if( totalGames < 1) {
			return 0;
		}
		return (double)(wins/totalGames) * 100;
	}

	@Override
	public String toString() {
		return String.format("%s = %s wins / %.2f%%", name, wins, calcWinPercentage() );
	}

	@Override
	public int compareTo(PlayerWins o) {
		if(o == null) {
			return 1;
		}
		return wins - o.getWins();
	}



}
