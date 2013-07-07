package scopa.logic;

public class ScopaFactory {

	public ScopaFactory() {
	}
	
	public static ScopaTable getNewScopaTable(){
		return new ScopaTableImpl();
	}
	
	public static ScopaDeck getNewScopaDeck(){
		return new ScopaDeckImpl();
	}
	
	

}
