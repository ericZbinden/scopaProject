package scopa.logic;

public enum ScopaValue {
as,two,three,four,five,six,seven,queen,joker,king;

	public static int val(ScopaValue val){
		switch(val){
		case as: return 1;
		case two: return 2;
		case three: return 3;
		case four: return 4;
		case five: return 5;
		case six: return 6;
		case seven: return 7;
		case queen: return 8;
		case joker: return 9;
		case king: return 10;
		default:
			throw new RuntimeException("Unknown value of ScopaValue: "+val);
		}
	}
	
	public static int val(ScopaCard card){
		return val(card.getValue());
	}
	
	public static boolean greater(ScopaValue val1, ScopaValue val2){
		return compare(val1,val2) > 0;
	}
	
	public static boolean isEqual(ScopaValue val1, ScopaValue val2){
		return compare(val1,val2) == 0;
	}
	
	public static boolean smaller(ScopaValue val1, ScopaValue val2){
		return compare(val1,val2) < 0;
	}		
	
	private static int compare(ScopaValue val1, ScopaValue val2){
		return Integer.compare(val(val1), val(val2));
	}
	
	public static boolean isHigherFor7(ScopaValue val1, ScopaValue val2){
		if (isEqual(val1, val2)) throw new RuntimeException("Impossible to have two card of same hight: "+val1+" / "+val2);
		
		if(val1==seven) return true;
		else if(val2 == seven) return false;
		else if (val1 == six) return true;
		else if(val2 == six) return false;
		else if(val1 == as) return true;
		else if (val2 == as) return false;
		else return greater(val1,val2);
	}



}
