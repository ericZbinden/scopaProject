package scopa.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import scopa.logic.ScopaCard;
import scopa.logic.ScopaFactory;
import scopa.logic.ScopaTable;
import util.Logger;

public class TablePanel extends JPanel implements MouseListener, ScopaTable {

	private final GridLayout defaultLayout; 
	private boolean biggerThan11;
	private int emptyToAdd;
	
	private Map<ScopaCard,CardLabel> cards;
	
	private ScopaTable table;
		
	public TablePanel() {
		defaultLayout = new GridLayout(2,6);
		cards = new HashMap<>();
		biggerThan11 = false;
		emptyToAdd = 12;
		table = ScopaFactory.getNewScopaTable();
		
		
		this.setLayout(defaultLayout);
		this.setBackground(Color.GREEN);
	}
	
	private void removeCard(ScopaCard card){
		CardLabel cl = cards.remove(card);
		if(cl != null){
			if(biggerThan11 && cards.size() < 12){
				this.setLayout(defaultLayout);
			}
			emptyToAdd ++;
			this.remove(cl);
			this.invalidate();
		} else {
			Logger.debug("Try to remove card "+card.toString()+" but was not present in the table");
		}
	}
	
	private void removeCards(List<ScopaCard> cards){
		for(ScopaCard card : cards){
			removeCard(card);
		}
	}
	
	private void addCard(ScopaCard card){
		CardLabel cLabel = new CardLabel(card);
		cLabel.addMouseListener(this);
		cards.put(card, cLabel);
		int size = cards.size();
		if(size > 11){
			this.setLayout(new GridLayout(3,6));
			biggerThan11 = true;
		}
		
		this.add(cLabel/*,size*/); //TODO ins�rer avant les blank
		emptyToAdd --;
		this.invalidate();
	}
	
	private void addCards(List<ScopaCard> cards){
		for(ScopaCard card : cards){
			addCard(card);
		}
	}
	
	public List<ScopaCard> getSelectedCards(){
		List<ScopaCard> selected = new ArrayList<>(cards.size());
		for(CardLabel cl : cards.values()){
			if (cl.isSelected()){
				selected.add(cl.getCard());
			}
		}
		return selected;
	}
	
	public void paint(Graphics g){
		if(emptyToAdd < 0){
			removeEmptyPanel(0-emptyToAdd);
		} else if(emptyToAdd > 0){
			do {
				CardLabel ecl = new CardLabel(null);
				ecl.addMouseListener(this);

				this.add(ecl);
				emptyToAdd --;
			} while(emptyToAdd != 0);
		}
		super.paint(g);
	}
	
	private void removeEmptyPanel(int nb){
		for(int i=0;i<this.getComponentCount();i++){
			Component compo = this.getComponent(i);
			if(compo instanceof CardLabel){
				CardLabel cardLabel = (CardLabel) compo;
				if(cardLabel.isEmpty()){
					this.remove(compo);
					i --;
					nb --;
					emptyToAdd++;
					if(nb ==0){
						assert emptyToAdd == 0;
						return;
					}
				}		
			}
		}
		Logger.error("Unable to remove enough emptyPanel. Left: "+nb);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Component compo = arg0.getComponent();
		
		if (compo instanceof CardLabel){
			CardLabel label = (CardLabel) compo;
			
			label.setSelected(!label.isSelected());				
		}	
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {      
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {		
	}
	
	@Override
	public boolean isEmpty() {
		return table.isEmpty();
	}

	@Override
	public List<ScopaCard> putCard(ScopaCard card) {
		List<ScopaCard> returned = table.putCard(card);
//		if (returned == null){
//			
//		}
		return returned;
	}

	@Override
	public List<ScopaCard> putCard(ScopaCard card, List<ScopaCard> cards) {
		List<ScopaCard> returned = table.putCard(card,cards);
//		if (returned == null){
//		
//	}
		return returned;
	}

	@Override
	public void putCards(List<ScopaCard> cards) {
		table.putCards(cards);		
	}

	@Override
	public List<ScopaCard> takeAll() {
		return table.takeAll();
	}

	@Override
	public boolean putInitial(List<ScopaCard> cards) {
		return table.putInitial(cards);
	}

	@Override
	public List<ArrayList<ScopaCard>> allPossibleTakeWith(ScopaCard card) {
		return table.allPossibleTakeWith(card);
	}

	@Override
	public List<ScopaCard> cardsOnTable() {
		return table.cardsOnTable();
	}
	
 //TEST	
//	public static void main(String[] args){
//		JFrame frame = new JFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setSize(new Dimension(300,300));
//		
//		TablePanel table = new TablePanel();
//		
//		
//		frame.add(table);
//		frame.setVisible(true);
//		
//		table.addCard(new ScopaCard(ScopaValue.as,ScopaColor.cup));
//		frame.repaint();
//		
//		ScopaDeck deck = ScopaFactory.getNewScopaDeck();
//		deck.shuffle();
//		table.addCards(deck.drawInitialCards());
//		table.addCards(deck.draw3Cards());
//		frame.repaint();
//		
//	}

	
}
