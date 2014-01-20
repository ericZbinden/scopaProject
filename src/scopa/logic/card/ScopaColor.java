package scopa.logic.card;

public enum ScopaColor {
	gold, stick, sword, cup, offuscated;

	public boolean isOffuscated() {
		return offuscated == this;
	}
}
