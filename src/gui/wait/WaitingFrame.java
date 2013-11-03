package gui.wait;

import game.GameType;
import gui.ChatMsgSender;
import gui.ChatPanel;
import gui.PlayMsgSender;
import gui.RulePanel;
import gui.game.GameGui;
import gui.game.GameGuiFrame;
import gui.wait.ConfigPanel.ParentAction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import util.Logger;
import util.PlayerName;

import com.client.ClientSocket;
import com.msg.MalformedMessageException;
import com.msg.Message;
import com.msg.MsgChat;
import com.msg.MsgDeco;
import com.msg.MsgMasterGame;
import com.msg.MsgMasterRule;
import com.msg.MsgNewPlayer;
import com.msg.MsgPlay;
import com.msg.MsgReset;
import com.msg.MsgStartAck;
import com.msg.MsgStartNack;
import com.msg.MsgWRslot;
import com.server.wait.ClosedConf;
import com.server.wait.Config;
import com.server.wait.EmptyConf;

public class WaitingFrame extends JFrame implements ActionListener, ChatMsgSender, PlayMsgSender, ControlPanel{
	
	//SRV
	ClientSocket clientSocket;
	boolean msgStartSend = false;
	
	//CLIENT
	private PlayerName clientID;
	private boolean isMaster;

	//GUI
	private static final long serialVersionUID = -3752588378621496399L;
	private Map<PlayerName,ConfigPanel> slots ;
	private Box conf = new Box(BoxLayout.Y_AXIS);
	private ChatPanel chat;
	private RulePanel rulePanel;
	private Box bottom = new Box(BoxLayout.X_AXIS);
	private GameType lastGame;
	private GameGui gameGui;
	private JButton startGame = new JButton("Start Game!"){
		boolean ready = true;
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			
			if(isReady()) this.setBackground(Color.GREEN);
			else this.setBackground(Color.RED);
		}
		public boolean isReady(){
			ready = true;
			for(ConfigPanel cp : slots.values()){
				if(!cp.getConfig().isReady()){
					ready = false;
					break;
				}
			}
			return ready;
		}
	};
	private JComboBox<GameType> gameChoice = new JComboBox<>(GameType.values());

	/**
	 * Default Master constructor
	 * @param coordinate
	 * @param sock
	 * @param clientID
	 */
	public WaitingFrame(Point coordinate, PlayerName clientID, Socket sock, ObjectOutputStream out, ObjectInputStream in){
		this(coordinate,clientID,true,sock,out,in,Arrays.asList((Config)new ClosedConf()));
	}
	
	/**
	 * Default Client constructor
	 * @param coordinate
	 * @param sock
	 * @param clientID
	 * @param configs
	 */
	public WaitingFrame(Point coordinate,PlayerName clientID, Socket sock, ObjectOutputStream out, ObjectInputStream in, List<Config> configs){
		this(coordinate,clientID,false,sock,out,in,configs);
	}
	
	/**
	 * General constructor
	 * @param coordinate
	 * @param clientID
	 * @param master
	 * @param sock
	 * @param configs
	 */
	private WaitingFrame(Point coordinate, PlayerName clientID, boolean master, Socket sock, ObjectOutputStream out, ObjectInputStream in, List<Config> configs){
		this.clientID=clientID;
		this.isMaster=master;
		
		gameGui = new GameGuiFrame(this);
		slots = new HashMap<>();
		clientSocket = new ClientSocket(sock,in,out,this);
		new Thread(clientSocket).start();
		this.build(coordinate,configs);
	}
	
	/**
	 * Build the complete frame
	 * @param coordinate where to print the frame
	 * @param initialConfs configuration of player already in the WR. Null if first player to join
	 */
	public void build(Point coordinate, List<Config> initialConfs){
		this.setTitle("Scopa Project waiting server");	
		Box total = new Box(BoxLayout.Y_AXIS);
		this.add(total);
		
		buildConf(conf,initialConfs);
		JScrollPane jsp = new JScrollPane(conf);
		jsp.setPreferredSize(new Dimension(300,200));
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		total.add(jsp);
	
		JPanel middle = new JPanel();
		buildMiddle(middle);
		total.add(middle);
	
		buildBottom(bottom);
		total.add(bottom);
	
		this.pack();
		this.setLocation(coordinate);
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	/**
	 * Build the player slots and their options
	 * @param conf
	 * @param initialConfs
	 */
	private void buildConf(Box conf, List<Config> initialConfs){
		
			//Current player
			ConfigPanel cp = new ConfigPanel(new Config(clientID,true),isMaster,this);
			boolean playerAdded = false;
			//Other players
			for(Config c : initialConfs){
				if (c.getClientID().equals(clientID)){
					slots.put(clientID, cp);
					conf.add(cp);
					playerAdded = true;
				} else {
					ConfigPanel cp2 = new ConfigPanel(c,isMaster,this);
					slots.put(c.getClientID(),cp2);
					conf.add(cp2);
				}		
			}
			
			if (!playerAdded){
				slots.put(clientID, cp);
				conf.add(cp,0);
			}
	}
	
	/**
	 * Build the Middle panel
	 * @param middle
	 */
	private void buildMiddle(JPanel middle){
		
		middle.setPreferredSize(new Dimension(610,30));
		middle.setMaximumSize(middle.getPreferredSize());
		middle.setBackground(Color.BLACK);
		
		if(isMaster){
			middle.setLayout(new BorderLayout());
			
			gameChoice.addActionListener(this);
			JPanel choice = new JPanel();
			choice.add(gameChoice);
			choice.setBackground(Color.BLACK);
			middle.add(choice,BorderLayout.CENTER);
		
			startGame.addActionListener(this);
			middle.add(startGame,BorderLayout.EAST);

		} else {

		}
	}
	
	/**
	 * Build the bottom panel (chat and game rule)
	 * @param bottom
	 */
	private void buildBottom(Box bottom){
		
		//CHAT
		chat = new ChatPanel(this);
		bottom.add(chat);
		
		bottom.add(new JSeparator(JSeparator.VERTICAL));
		
		//RULES
		rulePanel = ((GameType)gameChoice.getSelectedItem()).getRulePanel();
		if(rulePanel != null){
			if(isMaster){
				rulePanel.setEdit(true);
				rulePanel.addActionListener(this);
			} else {
				rulePanel.setEdit(false);
			}
		} else {
			rulePanel = new DefaultRulePanel();
			rulePanel.setBackground(Color.blue);
		}
		
		rulePanel.setPreferredSize(new Dimension(290,300));
		rulePanel.setMaximumSize(rulePanel.getPreferredSize());
		bottom.add(rulePanel);
		
	}
	
	public void update(Message msg){
		switch(msg.getType()){
		case wrSlot:
			MsgWRslot slot = (MsgWRslot) msg;
			Logger.debug(slot.toString());
			PlayerName impactedID = slot.getImpactedID();
			ConfigPanel prev = slots.get(impactedID);	
				if(prev != null){
						if(slot.getConf() instanceof ClosedConf){
							//closing a slot
							ConfigPanel cp = slots.remove(impactedID);
							conf.remove(cp);
							if (!(cp.getConfig() instanceof EmptyConf)){
								chat.writeIntoChatFromServer(impactedID+" haz been kicked");
							}
						} else if (slot.getConf() instanceof EmptyConf){
							//a player disconnected or kicked
							ConfigPanel cp = slots.remove(impactedID);
							conf.remove(cp);
							ConfigPanel newCP = new ConfigPanel(slot.getConf(), isMaster, this);
							slots.put(slot.getConf().getClientID(), newCP);
							conf.add(newCP,conf.getComponentCount()-1);
							chat.writeIntoChatFromServer(impactedID+" haz left");
						} else {
							//Updating a player conf
							Config c = slot.getConf();
							this.updateSlot(prev, c);
						}	
				} else {
					Logger.debug("new open slot: "+slot.getImpactedID());
					if (slot.getConf() instanceof EmptyConf){
						ConfigPanel cp = new ConfigPanel(slot.getConf(),isMaster,this);
						slots.put(impactedID, cp);
						conf.add(cp,conf.getComponentCount()-((isMaster)?0:1));
					} else {
						Logger.debug("Unknown client: "+impactedID);
					}
				}
			conf.invalidate();
			this.pack();
			this.repaint();
			break;
		case masterGame:
			MsgMasterGame masterGame = (MsgMasterGame) msg;
			GameType gType = masterGame.getGameType();
			lastGame = gType;
			gameChoice.setSelectedItem(gType);
			this.updateGameChoice(gType);
			break;
		case masterRule:
			MsgMasterRule masterRule = (MsgMasterRule) msg;
			try {
				rulePanel.editRules(masterRule);
				rulePanel.invalidate();
				rulePanel.repaint();
			} catch (MalformedMessageException e) {
				Logger.debug("failed to update panel: "+e.getMessage());
			}
			break;
//		case newPlayer:
//		case config:
//			//talk to srv
//			break;
//		case start:
		case startNack:
			MsgStartNack startNack = (MsgStartNack) msg;
			chat.writeIntoChatFromServer("Game can not start: "+startNack.getReason());
			this.msgStartSend = false;
			ConfigPanel clientConf = slots.get(clientID);
			clientConf.getConfig().setReady(false);
			clientConf.invalidate();
			startGame.invalidate();
			this.repaint();
			this.setVisible(true);
			gameGui.setVisibleToFalse();
			break;
		case chat:
			MsgChat chatMsg = (MsgChat) msg;
			chat.writeIntoChat(chatMsg.getSenderID(),chatMsg.getText());
			break;		
		case startAck:
			//start a game
			MsgStartAck startAck = (MsgStartAck) msg;
			GameType gameType = startAck.getGameType();
			gameGui.start(clientID, gameType, this);		
			this.setVisible(false);
			break;			
		case play:
			MsgPlay play = (MsgPlay) msg;
			gameGui.update(play);
			break;
		case newPlayer:
			//a new player just connect
			MsgNewPlayer co = (MsgNewPlayer) msg;
			chat.writeIntoChat(co.getNewPlayerID(),"haz connect");
			ConfigPanel cpNew = slots.remove(co.getOpenID());
			cpNew.setStatusToOccupied(new Config(co.getNewPlayerID()));
			slots.put(co.getNewPlayerID(), cpNew);
			cpNew.invalidate();
			this.repaint();
			break;
		case reco:
			//TODO
			break;
		case reset:
			MsgReset reset = (MsgReset) msg;
			//Only warn client of the reset, connection will be closed by server
			clientSocket.setClosing();
			JOptionPane.showMessageDialog(this, "Connection reset: "+reset.getReason());
			this.setVisible(false);
			break;
		case disconnect:
			MsgDeco deco = (MsgDeco) msg;
			ConfigPanel cpDeco = slots.get(deco.getDecoClient());
			if (cpDeco != null){
				chat.writeIntoChat(deco.getDecoClient(),"haz quit");
				slots.remove(deco.getDecoClient());
				cpDeco.setStatusToEmpty(new EmptyConf(deco.getEmptyID()));
				slots.put(cpDeco.getConfig().getClientID(), cpDeco);
				cpDeco.invalidate();
				this.repaint();			
			} else {
				Logger.error("Unknown user "+deco.getDecoClient()+" haz quit");
			}
			break;
		case refresh:
		default:
			Logger.error("Unknown msg: "+msg.toString());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		for(ConfigPanel cp : slots.values()){
			if(e.getSource().equals(cp)){
				ParentAction pa = null;
				try{
					pa = ParentAction.valueOf(e.getActionCommand());
				} catch(Exception ex){}
				if(pa != null){
					switch(pa){
					case ready:
						startGame.invalidate(); //no break statement (that's normal)
					case teamEdit:
						clientSocket.sendWrSlotMsg(cp.getConfig(), cp.getConfig().getClientID());
						break;
					case close:
						Config closed = cp.getConfig();
						PlayerName closedID = closed.getClientID();
						if (!(closed instanceof EmptyConf)){
							chat.writeIntoChatFromServer("you kicked "+closedID);
						}
						slots.remove(closedID);
						//cp.getConfig().setReady(true); //hack to avoid kicked player doesn't allow to start the game
						conf.remove(cp);
						startGame.invalidate();
						clientSocket.sendWrSlotMsg(new ClosedConf(),closedID);					
						break;
//					case kick:
//						this.kickSlot(cp,true);
//						break;
					case open:
						this.openNewSlot();
						cp.setStatusToClose();
						cp.invalidate();
						break;
					default:
						Logger.debug("Unknown parentAction: "+pa);
					}
				}
				this.pack();
				this.repaint();
				return;
			}
		}
		
		if(e.getSource().equals(gameChoice)){
			GameType t = (GameType) gameChoice.getSelectedItem();
			if(lastGame!=t){ //Check if need to update
				lastGame = t;
				this.updateGameChoice((GameType)gameChoice.getSelectedItem());
				clientSocket.sendGameMsg((GameType)gameChoice.getSelectedItem());		
			}	
			
		} else if(e.getSource().equals(startGame)){
			askGameStart();
		} else if(e.getSource().equals(rulePanel)){
			clientSocket.sendRuleMsg(rulePanel.getMsgRule(clientID));
			
		} else if(e.getSource().equals(clientSocket)){
			chat.writeIntoChatFromServer("Server crashed");
		} else {
			Logger.debug("Unknown source: "+e.getSource());
		}
		
	}
	
	private void askGameStart(){
		if(startGame.getBackground().equals(Color.GREEN)){
			if(!this.msgStartSend){ //send msg only once
				clientSocket.sendStartMsg();
				msgStartSend = true;
			} 
			chat.writeIntoChatFromServer("Game will start");
		} else {
			chat.writeIntoChatFromServer("All players are not ready");
		}
	}
	
	/**
	 * Update the slot with a new config
	 * @param impactedPanel
	 * @param newConfig
	 */
	private void updateSlot(ConfigPanel impactedPanel, Config newConfig){
		impactedPanel.updateConfig(newConfig);
		impactedPanel.invalidate();
	}
	
	/**
	 * The config panel is replaced by an emptyConfig
	 * @param kicked
	 * @param sendToSrv need to tell other players 
	 */
//	private void kickSlot(ConfigPanel kicked, boolean sendToSrv){
//
//		String impactedID = new String(kicked.getConfig().getClientID());
//		Logger.debug(impactedID);
//		slots.remove(impactedID);
//		EmptyConf emptyConf = this.createEmptyConf();
//		kicked.setStatusToEmpty(emptyConf);
//		slots.put(emptyConf.getClientID(), kicked);		
//		conf.invalidate();
//		conf.repaint();
//
//		//Send to srv
//		if(sendToSrv)
//			srv.sendMsg(new MsgWRslot(emptyConf,this.clientID,impactedID));					
//
//	}
	
	/** @return a new empty conf with a unique id */
	private EmptyConf createEmptyConf(){
		//Open new slot
		int emptyIndex = 0;
		for(int i=0;;i++){
			//Find the 1st empty slot non used
			if (slots.containsKey(EmptyConf.EMPTY_CONF_NAME))
				continue;
					
			emptyIndex = i;
			break;
		}
		
		return new EmptyConf(emptyIndex);
	}
	
	/**
	 * Open a new slot
	 */
	private void openNewSlot(){
		
		//Open new slot
		ConfigPanel newCP = new ConfigPanel(this.createEmptyConf(),isMaster,this);
		slots.put(newCP.getConfig().getClientID(), newCP);
		conf.add(newCP,conf.getComponentCount()-1);
		conf.invalidate();
		//Send to srv
		clientSocket.sendWrSlotMsg((EmptyConf)newCP.getConfig(),newCP.getConfig().getClientID());					

	}
	
	private void updateGameChoice(GameType type){
		bottom.remove(rulePanel);
		rulePanel = type.getRulePanel();
		if(rulePanel != null){
			if(isMaster){
				rulePanel.setEdit(true);
				rulePanel.addActionListener(this);
			} else {
				rulePanel.setEdit(false);
			}
		}
		rulePanel.setPreferredSize(new Dimension(290,300));
		rulePanel.setMaximumSize(rulePanel.getPreferredSize());
		bottom.add(rulePanel);
		this.pack();
		this.repaint();
	}
	
	public void disposeWithMsgDialog(String msgToDisplay){
		
		if(msgToDisplay != null && !msgToDisplay.equals("")){
			JOptionPane.showMessageDialog(this, msgToDisplay);
		} else {
			JOptionPane.showMessageDialog(this, "Reset following to unknown reason");
		}
		dispose();
	}
	
	@Override
	public void dispose(){
		clientSocket.close();
		super.dispose();
	}
	
	public PlayerName getClientID(){
		return clientID;
	}
	
	public void serverDown(){
		this.disposeWithMsgDialog("Server connection closed");
		super.dispose();
	}

	@Override
	public void sendChatMsg(String txtMsg) {
		clientSocket.sendChatMsg(getClientID(), txtMsg);
	}

	@Override
	public PlayerName getLocalClient() {
		return getClientID();
	}

	@Override
	public void sendMsgPlay(MsgPlay msg) {
		clientSocket.sendPlayMsg(msg);
		
	}

}
