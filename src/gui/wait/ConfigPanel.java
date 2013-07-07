package gui.wait;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.server.wait.ClosedConf;
import com.server.wait.Config;
import com.server.wait.EmptyConf;

public class ConfigPanel extends Box implements ActionListener{

	//action in kick box
	private final String OPEN = "Open";
	private final String CLOSED = "Closed";
	//private final String KICK = "kick";

	
	private Config conf;
	private boolean isMaster;
	/** Used to give action back to parent*/
	private ActionListener parentListener;
	/** Pseudo of the user */
	private JLabel pseudo = new JLabel();
	private JLabel teamNumb = null;
	/** Option given to master */
	private JComboBox<String> slot = new JComboBox<>();
	/** Choose your team */
	private JComboBox<Integer> team = new JComboBox<>(new Integer[]{1,2,3,4,5,6});
	/** Ready button */
	private JButton ready = new JButton("Ready!"){
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			if(conf != null){
				if(conf instanceof ClosedConf || conf instanceof EmptyConf)
					this.setBackground(Color.GRAY);
				else
					this.setBackground(conf.isReady()? Color.GREEN : Color.RED);
			}
		}
	};
	
	public ConfigPanel(Config conf, boolean isMaster, ActionListener parentListener){
		super(BoxLayout.Y_AXIS);
		this.conf=conf;
		this.parentListener=parentListener;
		this.isMaster=isMaster;
		
		JPanel top = new JPanel();
		this.setPreferredSize(new Dimension(600,ready.getPreferredSize().height+15));
		this.setMaximumSize(this.getPreferredSize());
		top.setLayout(new GridLayout(1,3));
			
		if(isMaster){
			top.add(slot);
			slot.addItem("  "+conf.getClientID());
			slot.addActionListener(this);
			slot.setEditable(false);
		} else {
			top.add(pseudo);
			pseudo.setText("  "+conf.getClientID());
		}	
		
		JPanel teamPanel = new JPanel();
		if(conf.isSelf()){
			teamPanel.add(new JLabel("Team"));
			teamPanel.add(team);
			team.setEditable(false);
			team.addActionListener(this);
			top.add(teamPanel);
		} else {
			
			teamNumb = new JLabel("Team "+conf.getTeam());
			teamPanel.add(teamNumb);
			top.add(teamPanel);
		}		
		
		JPanel button = new JPanel();
		button.add(ready);
		top.add(button);
		
		this.add(top);
		this.add(new JSeparator(JSeparator.HORIZONTAL));		
		
		if(conf instanceof ClosedConf){
			slot.addItem(OPEN);
		} else if(conf instanceof EmptyConf){			
			slot.addItem(CLOSED);
		} else {
			
			if(conf.isSelf()){			
				ready.setBorderPainted(true);	
				ready.addActionListener(this);
			} else {
				slot.addItem(CLOSED);
				//kick.addItem(KICK);
			}
			
		}		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource().equals(ready)){
			
			if(conf.isReady()){
				conf.setReady(false);
				
			} else {
				conf.setReady(true);
			}
			ready.invalidate();
			parentListener.actionPerformed(new ActionEvent(this,1,ParentAction.ready.toString()));
			
		} else if(arg0.getSource().equals(slot)){
			String selected = (String) slot.getSelectedItem();
			
			switch(selected){
//			case KICK:
//				if(!(conf instanceof EmptyConf) && !(conf instanceof ClosedConf)){
//					conf = new EmptyConf();
//					this.invalidate();
//					parentListener.actionPerformed(new ActionEvent(this,2,ParentAction.kick.toString()));
//					
//				}
//				break;
			case CLOSED:
				if(!(conf instanceof ClosedConf)){
						this.invalidate();
						parentListener.actionPerformed(new ActionEvent(this,3,ParentAction.close.toString()));
				}
				break;
			case OPEN:
				if(conf instanceof ClosedConf){
					slot.setSelectedItem(CLOSED);
					this.invalidate();
					parentListener.actionPerformed(new ActionEvent(this,4,ParentAction.open.toString()));
				}
			default:
				
			}
			slot.setSelectedIndex(0);
			
		} else if(arg0.getSource().equals(team)){
			conf.setTeam((Integer)team.getSelectedItem());
			parentListener.actionPerformed(new ActionEvent(this,5,ParentAction.teamEdit.toString()));
		}
		
	}
	
	public Config getConfig(){
		return conf;
	}
	
	public boolean isOpen(){
		return conf instanceof EmptyConf;
	}
	
	public void updateConfig(Config config){
		conf.setReady(config.isReady());
		conf.setTeam(config.getTeam());		
		teamNumb.setText("Team "+config.getTeam());
		
		teamNumb.invalidate();	
		ready.invalidate();
		ready.repaint();
		this.invalidate();
	}
	
	public void setStatusToClose(){
		slot.removeActionListener(this);
		conf = new ClosedConf();
		slot.setSelectedItem(CLOSED);
		slot.addActionListener(this);
		ready.invalidate();
		this.invalidate();
	}
	
	public void setStatusToEmpty(Config c){
		if (isMaster){
			slot.removeActionListener(this);
			slot.removeAllItems();
			slot.addItem("  "+c.getClientID());
			slot.addItem(CLOSED);
			slot.setSelectedItem("  "+c.getClientID());
			slot.addActionListener(this);
		}		
		conf = c.clone();
		pseudo.setText("  "+conf.getClientID());
		teamNumb.setText("Team "+conf.getTeam());
		ready.invalidate();
		teamNumb.invalidate();
		pseudo.invalidate();
	}
	
	public void setStatusToOccupied(Config c){
		conf = c.clone();
		if(isMaster){
			slot.removeActionListener(this);
			slot.removeAllItems();
			slot.addItem("  "+conf.getClientID());
			//kick.addItem(KICK);
			slot.addItem(CLOSED);
			slot.setSelectedItem(conf.getClientID());
			slot.addActionListener(this);
		} else {		
			pseudo.setText("  "+conf.getClientID());
			teamNumb.setText("Team "+conf.getTeam());
			pseudo.invalidate();
			teamNumb.invalidate();
		}
		ready.invalidate();
		
	}
	
	public enum ParentAction{
		ready,/*kick,*/close,open,teamEdit
	}
	

}

