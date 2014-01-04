package scopa.logic;

import java.io.Serializable;

public class ScopaCard implements Serializable {

	private static final long serialVersionUID = -7448441193550564666L;

	private ScopaValue value;	
	private ScopaColor color;
	
	public ScopaCard(ScopaValue value, ScopaColor color){
		this.value = value;
		this.color = color;
	}
	
	@Override
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
	
	public boolean isEqualInValue(ScopaCard card){
		return ScopaValue.isEqual(value, card.getValue());
	}
	
	public boolean isHigherFor7(ScopaCard card){
		return ScopaValue.isHigherFor7(value, card.getValue());
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof ScopaCard){
			ScopaCard that = (ScopaCard) obj;
			//Logger.debug("Testing equality between ScopaCard: "+this.toString()+" and: "+that.toString());
			return that.getColor().equals(this.color) &&
				   that.getValue().equals(this.value);
		}
		//Logger.debug("Not a card: "+obj.getClass().toString());
		return false;
	}
	
	@Override
	public int hashCode(){
		int hash = 1;
		switch(color){
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
			default:
				throw new RuntimeException("Unsupported enum value: "+color);
		}
		hash = hash * 13 + ScopaValue.val(this);
		return hash;
	}

}
