package clueSolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestGame {

	@Test
	void test() {
		List<Player> players = new ArrayList<Player>();
		Player anne  = new Player("anne", null);
		Player ben   = new Player("ben", null);
		Player cinna = new Player("cinna", null);
		Player dane  = new Player("dane", null);
		Player emily = new Player("emily", null);
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
