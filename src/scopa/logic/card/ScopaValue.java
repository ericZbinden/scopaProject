package scopa.logic.card;

public enum ScopaValue {
	as(1), two(2), three(3), four(4), five(5), six(6), seven(7), queen(8), joker(9), king(10), offuscated(Integer.MIN_VALUE);

	private int val;

	private ScopaValue(int val) {
		this.val = val;
	}

	public int getValue() {
		return val;
	}

	public boolean isOffuscated() {
		return offuscated.getValue() == val;
	}

	public static int val(ScopaCard card) {
		return card.getValue().getValue();
	}

	public boolean isGreater(ScopaValue val2) {
		return this.compareTo(val2) > 0;
	}

	public boolean isValueEqual(ScopaValue val2) {
		return this.compareTo(val2) == 0;
	}

	public boolean isSmaller(ScopaValue val2) {
		return this.compareTo(val2) < 0;
	}

	public boolean isHigherFor7(ScopaValue val2) {

		if (val2 == null) {
			return true;
		} else if (val2.isOffuscated()) {
			return true;
		} else if (this.isOffuscated()) {
			return false;
		} else if (isValueEqual(val2)) {
			return false;
		} else if (this == seven) {
			return true;
		} else if (val2 == seven) {
			return false;
		} else if (this == six) {
			return true;
		} else if (val2 == six) {
			return false;
		} else if (this == as) {
			return true;
		} else if (val2 == as) {
			return false;
		} else {
			return isGreater(val2);
		}
	}
}
