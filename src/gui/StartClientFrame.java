package gui;

import gui.wait.WaitingFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.msg.MalformedMessageException;
import com.msg.Message;
import com.msg.MsgCaster;
import com.msg.MsgConfig;
import com.msg.MsgConnect;
import com.msg.MsgMasterGame;
import com.msg.MsgReset;
import com.msg.MsgType;
import com.server.impl.Server;

import util.Logger;
import util.PlayerName;
import util.Regex;
import util.ReservedName;

public class StartClientFrame extends JFrame implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = -1085150696868325516L;

	private JTextField pseudo = new JTextField();
	
	private JTextField serverPwd = new JTextField();	
	private JTextField ip = new JTextField();
	private JTextField port = new JTextField();
	private JLabel error = new JLabel();
	
	private JButton connect = new JButton("Connect to server");
	
	StartClientFrame(Point coordinate){
		super();
		
		this.setLayout(new BorderLayout());
		JPanel center = new JPanel();
		this.setTitle("Connect to Project Scopa Server");
		this.add(center, BorderLayout.CENTER);
		this.add(connect, BorderLayout.SOUTH);
		this.add(error, BorderLayout.NORTH);
		
		Dimension dim = new Dimension(200,20);
		ip.setPreferredSize(dim);
		ip.addKeyListener(this);
		serverPwd.setPreferredSize(dim);
		serverPwd.addKeyListener(this);
		port.setPreferredSize(dim);
		port.addKeyListener(this);
		pseudo.setPreferredSize(dim);
		pseudo.addKeyListener(this);
		connect.addActionListener(this);
		connect.setPreferredSize(dim);
		error.setForeground(Color.RED);
		
		center.setLayout(new GridLayout(5,2));
		center.add(new JLabel("  Pseudo"));
		center.add(pseudo);
		center.add(new JPanel());
		center.add(new JPanel());
		center.add(new JLabel("  Server ip"));
		center.add(ip);
		center.add(new JLabel("  Server port"));
		center.add(port);
		center.add(new JLabel("  Server pwd"));
		center.add(serverPwd);	
		
		this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocation(coordinate);
		this.setResizable(false);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		error.setText("");
		
		//Check pseudo	
		PlayerName playerId = new PlayerName(pseudo.getText());
		if(ReservedName.isReserved(playerId)){
			setErrorText("Your pseudo \""+playerId.getName()+"\" is reserved, choose another");
			return;
		}
			
		//Check ip
		String IP = ip.getText();
		if(!Regex.matchIP4Pattern(IP)){
			setErrorText("IP should be a valid IP");
			return;
		}
				
		String p = port.getText();
		Socket sock = null;
		boolean ok = false;
		try{
			//Check port
			int p2 = Integer.valueOf(p);
			if(p2 < 1 || p2 > 65535)
				throw new NumberFormatException("not good range number");
			
			sock = new Socket(IP,p2);		
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
			//Send connect msg
			out.writeObject(new MsgConnect(playerId, serverPwd.getText()));
			//Respond
			sock.setSoTimeout(5000);
			Message msg = (Message) in.readObject();
			Logger.debug("Client answer received: "+msg.getType());
			if(MsgType.config.equals(msg.getType())){
				//Accepted
				MsgConfig conf = MsgCaster.castMsg(MsgConfig.class, msg);		
				sock.setSoTimeout(0);
				WaitingFrame wf = new WaitingFrame(this.getLocation(),playerId,sock,out,in, conf.getConfigs());
				if (conf.getRule() != null){
					Logger.debug("Update game");
					wf.update(new MsgMasterGame(conf.getRule().getGameType(),Server.SERVER_NAME)); //update game type
					if (conf.getRule().getType().equals(MsgType.masterRule)){
						Logger.debug("Update rules");
						wf.update(conf.getRule());	//set current rules
					}
				}			
				ok = true;
				this.dispose(); 
			} else if(msg.getType().equals(MsgType.reset)){
				//Refused
				setErrorText("Connection reset: "+((MsgReset)msg).getReason());
			} else {
				//Else
				Logger.debug(msg.toString());
				throw new MalformedMessageException("Expected MsgConfig but exposed type: "+msg.getType());
			}
		} catch (NumberFormatException e){
			setErrorText("port should be a number [1,...,65535]");
		} catch (IOException e) {
			setErrorText("Unable to connect to host: "+e.getMessage());
		} catch (ClassNotFoundException | MalformedMessageException e) {
			setErrorText("Corrupted communication: "+e.getMessage());
		} finally {
			//Close socket if error happened
			try {
				if(sock!=null && !ok) sock.close();
			} catch (IOException e) {
				//Die silently
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {	
		//Nothing
	}

	@Override
	public void keyReleased(KeyEvent arg0) {	
		//Nothing
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (arg0.getKeyChar() == '\n') actionPerformed(new ActionEvent(connect, 0, "clic"));
	}
	
	private void setErrorText(String errorMsg){
		error.setText(errorMsg);
		error.invalidate();
		this.pack();
		this.repaint();		
	}
	
}
