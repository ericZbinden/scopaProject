package scopa.gui;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import scopa.logic.ScopaFactory;
import scopa.logic.card.ScopaCard;
import util.Logger;


public class ScopaCardTransfertHandler extends TransferHandler {
	
	private static final long serialVersionUID = -2839290592644692803L;
	
	private ScopaGamePanel sgp;
	private ScopaCard played;
	private List<ScopaCard> taken;
	
	public ScopaCardTransfertHandler(ScopaGamePanel sgp) {
		this.sgp=sgp;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) { 
		if (c instanceof CardLabel){
			CardLabel cardPanel = (CardLabel) c;
			this.setDragImage(cardPanel.getImage());
			return new ScopaCardTransferable(cardPanel.getCard());
		} else 
			return null;
  }

  @Override
public int getSourceActions(JComponent c) {
        return MOVE;
  }
  

  @Override
public boolean canImport(JComponent c, DataFlavor[] flavors) {
	  for (DataFlavor flavor : flavors) {
		  if (flavor.equals(ScopaFactory.getScopaCardDataFlavor())) {
			  return canImportOn(c);
	      }
	  }     
      return false;
  }
  
  /** If c instance of TablePanel return true else return false */
  private boolean canImportOn(JComponent c){
	  return (c instanceof ScopaTablePanel) || (c instanceof CardLabel && c.getParent() instanceof ScopaTablePanel);
  }

  @Override
  public boolean importData(JComponent c, Transferable t) {
      if (canImport(c,t.getTransferDataFlavors())) {
          try {
        	  ScopaTablePanel table;
        	  if(c instanceof ScopaTablePanel){
        		  table = (ScopaTablePanel) c;  
        	  } else if (c.getParent() instanceof ScopaTablePanel){
        		  table = (ScopaTablePanel) c.getParent();
        	  } else {
        		  return false;
        	  }
        		  
        	  played = (ScopaCard) t.getTransferData(ScopaFactory.getScopaCardDataFlavor());
        		  
        	  List<ScopaCard> selected = table.getSelectedCards();        		  
        	  taken = table.putCard(played, selected);
       		  if(taken == null){
       			  //Play is wrong, alert UI
       			  sgp.alertPlayerWrongPlay();
       			  return false;
       		  } else {
       			  //Play ok and done
       			  return true;
       		  }      
       	       		  
          } catch (UnsupportedFlavorException | IOException e) {
              Logger.error("Unable to import data "+t.toString()+" into "+c.toString());
          } 
      }
      return false;
  }
  
  @Override
  protected void exportDone(JComponent c, Transferable data, int action){
	  if(action == MOVE){
		  try{
			  CardLabel cardPanel = (CardLabel) c;
			  //ScopaCardTransferable scopaTransfer = (ScopaCardTransferable) data;
			  cardPanel.setCard(null);  
			  sgp.sendMsgScopaPlay(played, taken);
		  } catch (Exception e){
			  Logger.error("Unable to remove card from component "+c.toString());
		  }
	  } 
  }
  
  

}
