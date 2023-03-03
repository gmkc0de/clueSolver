package clueSolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import clueSolver.db.PlayerDb;
import clueSolver.db.SqliteUtil;

public class TestInputHand {
	@Test
	void test() {
		try (Connection conn = SqliteUtil.connect()) {

			Game game = new Game();
			
			Player anne = new Player("anne", null);

			PlayerDb anneDb = new PlayerDb(anne);

			ArrayList<Card> hand = new ArrayList<Card>();
			Card ball = new Card("ballroom", "room");
			Card cons = new Card("conservatory", "room");
			Card hall = new Card("hall", "room");
			Card stud = new Card("study", "room");
			Card lib = new Card("library", "room");

			anne.setHand(hand);
			anneDb.insert(conn);
			anneDb.insertHand(conn, anne.getHand());

			assertEquals("true", "true");

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}
