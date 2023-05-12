package clueSolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import clueSolver.player.Player;
import clueSolver.player.RandomPlayer;

class TestGame {

	@Test
	
	void test() {
		
		List<Player> players = new ArrayList<Player>();
		Player anne  = new RandomPlayer("anne", null);
		Player ben   = new RandomPlayer("ben", null);
		Player cinna = new RandomPlayer("cinna", null);
		Player dane  = new RandomPlayer("dane", null);
		Player emily = new RandomPlayer("emily", null);
		players.add(anne );
		players.add(ben  );
		players.add(cinna);
		players.add(dane );
		players.add(emily);
		
		List<Player> expected = new ArrayList<Player>();
		expected.add(cinna);
		expected.add(dane );
		expected.add(emily);
		expected.add(anne );

		List<Player> actual = Game.disprovePlayerList(players,ben);
		
		assertEquals(expected, actual);

		
	}

}
