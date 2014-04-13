package scopa.logic.hand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scopa.logic.card.ScopaCard;
import scopa.logic.card.ScopaColor;
import scopa.logic.card.ScopaValue;
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
	public List<ScopaCard> getCardsInHand() {
		return Arrays.asList();
	}

	@Override
	public <T extends ScopaCard> void newHand(List<T> hand) {
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

	@Override
	public void resetScore() {
		// Nothing
	}

	@Override
	public boolean took7OfGold() {
		return false;
	}

	@Override
	public int tookCardsTotal() {
		return 0;
	}

	@Override
	public int tookGoldTotal() {
		return 0;
	}

	@Override
	public int tookSevenTotal() {
		return 0;
	}

	@Override
	public Map<ScopaColor, ScopaValue> tookBestCardInAllColor() {
		return new HashMap<>(0);
	}

}
