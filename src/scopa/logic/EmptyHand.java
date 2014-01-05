package scopa.logic;

import java.util.Arrays;
import java.util.List;

import util.PlayerName;
import util.ReservedName;

/**
 * Empty Hand used to be integrated into gui for non playing slot
 * @author Coubii
 * 
 */
public class EmptyHand implements ScopaHand {

	private PlayerName playerName;

	public EmptyHand() {
		playerName = new PlayerName(ReservedName.EMPTY_NAME);
	}

	@Override
	public PlayerName getPlayerName() {
		return playerName;
	}

	@Override
	public List<ScopaCard> getHand() {
		return Arrays.asList();
	}

	@Override
	public void newHand(List<ScopaCard> hand) {
		// Do nothing
	}

	@Override
	public boolean playCard(ScopaCard card) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public int getNumberCardInHand() {
		return 0;
	}

	@Override
	public void addCardsToHeap(List<ScopaCard> taken) {
		// Do nothing
	}

	@Override
	public boolean isOffuscated() {
		return false;
	}

}
