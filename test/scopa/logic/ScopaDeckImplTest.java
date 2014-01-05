package scopa.logic;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScopaDeckImplTest {

	@Test
	public void testDeckSize() {
		ScopaDeckImpl deck = (ScopaDeckImpl) ScopaFactory.getNewScopaDeck();

		assertTrue(deck.size() == 40);

	}

	@Test
	public void testDrawInitial() {
		ScopaDeck deck = ScopaFactory.getNewScopaDeck();

		assertTrue(deck.drawInitialCards().size() == 4);

	}

	@Test
	public void testDraw3Cards() {
		ScopaDeck deck = ScopaFactory.getNewScopaDeck();

		assertTrue(deck.draw3Cards().size() == 3);

	}

}
