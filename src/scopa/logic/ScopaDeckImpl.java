package scopa.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScopaDeckImpl extends ArrayList<ScopaCard> implements ScopaDeck {

	private static final long serialVersionUID = 7062358612411467718L;

	ScopaDeckImpl(){
		super();
		
		for(ScopaColor color : ScopaColor.values()){
			for(ScopaValue value : ScopaValue.values()){
				ScopaCard card = new ScopaCard(value,color);
				this.add(card);
			}
		}
	}
	
	
	@Override
	public void shuffle() {
		Collections.shuffle(this);

	}

	@Override
	public ScopaCard drawCard() {
		return this.remove(0);
	}

	@Override
	public List<ScopaCard> draw3Cards() {
		List<ScopaCard> cards = new ArrayList<ScopaCard>(3);
		cards.add(this.drawCard());
		cards.add(this.drawCard());
		cards.add(this.drawCard());
		return cards;
	}
	
	@Override
	public boolean isEmpty(){
		return super.isEmpty();
	}


	@Override
	public List<ScopaCard> drawInitialCards() {
		List<ScopaCard> cards = draw3Cards();
		cards.add(drawCard());
		return cards;
	}

}
