package scopa.logic;

import java.awt.datatransfer.DataFlavor;

import scopa.logic.card.ScopaCard;

public class ScopaFactory {
	
	private final static String MIME= DataFlavor.javaJVMLocalObjectMimeType +
		    ";class=" + ScopaCard.class.getName();
	
	public ScopaFactory() {
	}
	
	public static ScopaTable getNewScopaTable(){
		return new ScopaTableImpl();
	}
	
	public static ScopaDeck getNewScopaDeck(){
		return new ScopaDeckImpl();
	}
	
	public static DataFlavor getScopaCardDataFlavor(){
		try {
			return new DataFlavor(MIME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	

}
