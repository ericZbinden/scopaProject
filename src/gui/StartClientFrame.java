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
import com.msg.MsgConfig;
import com.msg.MsgConnect;
import com.msg.MsgMasterGame;
import com.msg.MsgReset;
import com.msg.MsgType;

import util.Logger;
import util.Regex;

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
		Logger.debug("Button pressed");
		if(!pseudo.getText().equals("")){
			//Check ip
			String IP = ip.getText();
			if(Regex.matchIP4Pattern(IP)){
				
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
					out.writeObject(new MsgConnect(pseudo.getText(), serverPwd.getText()));
					//Respond
					sock.setSoTimeout(5000);
					Message msg = (Message) in.readObject();
					Logger.debug("Client answer received: "+msg.getType());
					if(msg.getType().equals(MsgType.config)){
						//Accepted
						MsgConfig conf = (MsgConfig) msg;		
						sock.setSoTimeout(0);
						WaitingFrame wf = new WaitingFrame(this.getLocation(),pseudo.getText(),sock,out,in, conf.getConfigs());
						if (conf.getRule() != null){
							Logger.debug("Update game");
							wf.update(new MsgMasterGame(conf.getRule().getGameType(),"server")); //update game type
							if (conf.getRule().getType().equals(MsgType.masterRule)){
								Logger.debug("Update rules");
								wf.update(conf.getRule());	//set current rules
							}
						}			
						ok = true;
						this.dispose(); 
					} else if(msg.getType().equals(MsgType.reset)){
						//Refused
						error.setText("Connection reset: "+((MsgReset)msg).getReason());
					} else {
						//Else
						Logger.debug(msg.toString());
						throw new MalformedMessageException(MsgType.config);
					}
				} catch (NumberFormatException e){
					error.setText("port should be a number [1,...,65535]");
				} catch (IOException e) {
					error.setText("Unable to connect to host: "+e.getMessage());
				} catch (ClassNotFoundException | MalformedMessageException e) {
					error.setText("Corrupted communication: "+e.getMessage());
				} finally {
					//Close socket if error happened
					try {
						if(sock!=null && !ok) sock.close();
					} catch (IOException e) {
					}
				}
				
			} else {
				error.setText("IP should be a valid IP");
			}
		} else {
			error.setText("Pseudo can not be empty");
		}
		
		error.invalidate();
		this.pack();
		this.repaint();		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (arg0.getKeyChar() == '\n') actionPerformed(new ActionEvent(connect, 0, "clic"));
	}
	
}
