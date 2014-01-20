package scopa.logic.card;

public class OffuscatedScopaCard extends ScopaCard {

	public OffuscatedScopaCard() {
		super(ScopaValue.offuscated, ScopaColor.offuscated);
	}

	@Override
	public boolean isOffuscated() {
		return true;
	}

}