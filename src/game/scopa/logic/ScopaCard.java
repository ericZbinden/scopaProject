package game.scopa.logic;

public class ScopaCard {
	
	private ScopaValue value;
	
	private ScopaColor color;
	
	public ScopaCard(ScopaValue value, ScopaColor color){
		this.value = value;
		this.color = color;
	}
	
	public String toString(){
		return value + " of " +color;
	}

	public ScopaValue getValue() {
		return value;
	}

	public ScopaColor getColor() {
		return color;
	}
	
	public boolean isGreater(ScopaCard card){
		return ScopaValue.greater(value, card.getValue());
	}
	
	public boolean isSmaller(ScopaCard card){
		return ScopaValue.smaller(value, card.getValue());
	}
	
	public boolean isEqual(ScopaCard card){
		return ScopaValue.isEqual(value, card.getValue());
	}
	
	public boolean isHigherFor7(ScopaCard card){
		return ScopaValue.isHigherFor7(value, card.getValue());
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof ScopaValue){
			ScopaCard card = (ScopaCard) obj;
			if(card.color==this.color&&card.value==this.value)
				return true;
		}
		return false;
	}

}
