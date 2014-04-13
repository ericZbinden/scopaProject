package scopa.logic.card;

import java.io.Serializable;

public class ScopaCard implements Serializable, Comparable<ScopaCard> {

	private static final long serialVersionUID = -7448441193550564666L;

	private ScopaValue value;
	private ScopaColor color;

	public ScopaCard(ScopaValue value, ScopaColor color) {
		this.value = value;
		this.color = color;
	}

	@Override
	public String toString() {
		return value + " of " + color;
	}

	public ScopaValue getValue() {
		return value;
	}

	public ScopaColor getColor() {
		return color;
	}

	public boolean isOffuscated() {
		return value.isOffuscated() || color.isOffuscated();
	}

	public boolean isGreater(ScopaCard card) {
		return this.value.isGreater(card.getValue());
	}

	public boolean isSmaller(ScopaCard card) {
		return this.value.isSmaller(card.getValue());
	}

	public boolean isEqualInValue(ScopaCard card) {
		return this.value.isValueEqual(card.getValue());
	}

	public boolean isHigherFor7(ScopaCard card) {
		if (card == null) {
			return true;
		}
		return value.isHigherFor7(card.getValue());
	}

	public String getImgPath() {
		return "resources/img/" + this.getColor() + "/" + ScopaValue.val(this) + ".png";
	}

	@Override
	public boolean equals(Object obj) {

		if (this == null) {
			return obj == null;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof ScopaCard) {
			ScopaCard that = (ScopaCard) obj;
			// Logger.debug("Testing equality between ScopaCard: "+this.toString()+" and: "+that.toString());
			return that.getColor().equals(this.color) && that.getValue().equals(this.value);
		}
		// Logger.debug("Not a card: "+obj.getClass().toString());
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		switch (color) {
		case cup:
			hash = 1;
			break;
		case stick:
			hash = 2;
			break;
		case gold:
			hash = 3;
			break;
		case sword:
			hash = 4;
			break;
		case offuscated:
			hash = Integer.MIN_VALUE;
			break;
		default:
			throw new RuntimeException("Unsupported enum value: " + color);
		}
		hash = hash * 13 + ScopaValue.val(this);
		return hash;
	}

	@Override
	public int compareTo(ScopaCard o) {
		int comparedValue = this.color.compareTo(o.getColor());
		if (comparedValue == 0) {
			return this.value.compareTo(o.getValue());
		}
		return comparedValue;
	}

}
