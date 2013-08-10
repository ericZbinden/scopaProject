package scopa.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import scopa.logic.ScopaCard;
import scopa.logic.ScopaFactory;

public class ScopaCardTransferable implements Transferable {
	
	private ScopaCard card;
		
	public ScopaCardTransferable(ScopaCard card) {
		this.card = card;
	}
	
	/**
	 * @deprecated better use Transferable.getTransferData(DataFlavor)
	 * @return
	 */
	@Deprecated
	public ScopaCard getCards(){
		return card;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		 DataFlavor[] result= new DataFlavor[1];		
	     result[0]= ScopaFactory.getScopaCardDataFlavor();

	     return result;	        
	}
	 
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		 return ScopaFactory.getScopaCardDataFlavor().equals(flavor);
	} 

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		 if (isDataFlavorSupported(flavor)) {
			 return card;
		 } else throw new UnsupportedFlavorException(flavor); 
	}

}
