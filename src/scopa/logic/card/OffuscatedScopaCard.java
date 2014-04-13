package scopa.logic.card;

public class OffuscatedScopaCard extends ScopaCard {

	private static final String offuscatedCardImgPath = "resources/img/gui/carte1.png";

	public OffuscatedScopaCard() {
		super(ScopaValue.offuscated, ScopaColor.offuscated);
	}

	@Override
	public boolean isOffuscated() {
		return true;
	}

	@Override
	public String getImgPath() {
		return offuscatedCardImgPath;
	}

}
