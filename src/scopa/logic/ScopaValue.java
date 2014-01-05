package scopa.logic;

public enum ScopaValue {
	as(1), two(2), three(3), four(4), five(5), six(6), seven(7), queen(8), joker(9), king(10), offuscated(Integer.MIN_VALUE);

	private int val;

	private ScopaValue(int val) {
		this.val = val;
	}

	public int getValue() {
		return val;
	}

	public static int val(ScopaCard card) {
		return card.getValue().getValue();
	}

	public static boolean greater(ScopaValue val1, ScopaValue val2) {
		return compare(val1, val2) > 0;
	}

	public static boolean isEqual(ScopaValue val1, ScopaValue val2) {
		return compare(val1, val2) == 0;
	}

	public static boolean smaller(ScopaValue val1, ScopaValue val2) {
		return compare(val1, val2) < 0;
	}

	private static int compare(ScopaValue val1, ScopaValue val2) {
		return Integer.compare(val1.getValue(), val2.getValue());
	}

	public static boolean isHigherFor7(ScopaValue val1, ScopaValue val2) {
		if (isEqual(val1, val2))
			// throw new
			// RuntimeException("Impossible to have two card of same hight: " +
			// val1 + " / " + val2);
			return false;

		if (val1 == seven)
			return true;
		else if (val2 == seven)
			return false;
		else if (val1 == six)
			return true;
		else if (val2 == six)
			return false;
		else if (val1 == as)
			return true;
		else if (val2 == as)
			return false;
		else
			return greater(val1, val2);
	}

}
