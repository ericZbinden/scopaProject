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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.Logger;
import util.PlayerName;
import util.ReservedName;

import com.msg.MsgConnect;
import com.server.Server;

public class StartServerFrame extends JFrame implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = 4491088652275662971L;
	
	private JTextField playerName = new JTextField();
	private JTextField password = new JTextField();
	private JTextField port = new JTextField();
	private JLabel error = new JLabel();
	private JPanel center = new JPanel();
	private JButton start = new JButton("Start Server");
	private JButton close = new JButton("Close Server");
	private JLabel running = new JLabel();
	
	private Server server;
	
	StartServerFrame(Point coordinate){

		//Sub component
		start.addActionListener(this);
		close.addActionListener(this);
		error.setForeground(Color.RED);	
		playerName.setPreferredSize(new Dimension(200,50));
		port.setPreferredSize(new Dimension(200,50));
		port.addKeyListener(this);
		password.setPreferredSize(new Dimension(200,50));
		//gameProposed.setPreferredSize(new Dimension(200,50));	
		
		//Frame
		this.setTitle("Server settings");
		this.setLayout(new BorderLayout());
		this.add(center, BorderLayout.CENTER);
		this.add(start, BorderLayout.SOUTH);
		this.add(error,BorderLayout.NORTH);
		this.setLocation(coordinate);
		
		//Center panel
		center.setLayout(new GridLayout(3,2));
		center.add(new JLabel("  Player name"));
		center.add(playerName);
		center.add(new JLabel("  Server password"));
		center.add(password);
		//center.add(gameProposed);
		center.add(new JLabel("  Server port"));
		center.add(port);
		
		//Misc to print out frame
		this.setPreferredSize(center.getPreferredSize());
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	public boolean serverRunnuing(){
		return server != null && server.isRunning();
	}
	
	public void closeServer(){
		if(server != null){
			server.shutDownServer();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource().equals(start)){

			//Check name
			PlayerName name = new PlayerName(playerName.getText());
			if(ReservedName.isReserved(name)){
				setErrorText("Player name \""+name.getName()+"\" is reserved, choose anthoer");
				return;
			}
			
			Socket sock = null;
			boolean ok = false;
			try{
				//Check port
				String p = port.getText();
				int p2 = Integer.valueOf(p);
				
				if(p2 < 1 || p2 > 65535){
					throw new NumberFormatException("too big or too low number");
				}
				
				//Start server
				running.setText(name+" listening on port: "+p2);
				server = Server.getInstance(p2,password.getText(),this);
				this.remove(center);
				this.remove(start);
				this.add(close,BorderLayout.SOUTH);
				this.add(running,BorderLayout.CENTER);
				setErrorText("");
				//Start waiting frame
				sock = new Socket("localhost",p2);
				ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
				out.writeObject(new MsgConnect(name, password.getText()));
				in.readObject();
				new WaitingFrame(this.getLocation(), name, sock, out,in);		
				ok = true;
				
			} catch (NumberFormatException e){
				setErrorText("Port should be a number between 1 and 65535!");

			} catch (Exception e){
				setErrorText("Server crashed: "+e.getLocalizedMessage());
				e.printStackTrace();
			} finally {
				if(sock != null && !ok){
					try {
						sock.close();
					} catch (IOException e) {
					}
				}
			}
			
		} else if (arg0.getSource().equals(close)) {
			
			closeServer();
			server = null;
			this.remove(running);
			this.remove(close);
			this.add(start,BorderLayout.SOUTH);
			this.add(center,BorderLayout.CENTER);			
			
		} else {
			
			running.setText("Server crashed: "+arg0.getActionCommand());
			this.closeServer();
		}
		
		repaintThis();
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (arg0.getKeyChar() == '\n') actionPerformed(new ActionEvent(start, 0, "clic"));
	}
	
	@Override
	public void dispose(){
		
		if(server != null && server.isRunning()){
			
			int choice = JOptionPane.showConfirmDialog(this, "Server still running, do you really want to exit ?");
			switch(choice){
			case JOptionPane.YES_OPTION:
				closeServer();
				super.dispose();
				return;
			case JOptionPane.NO_OPTION:
			case JOptionPane.CANCEL_OPTION:
			default:
				return;
			}
			
		} else {
			super.dispose();
		}
	}
	
	private void setErrorText(String text){
		error.setText(text);
		Logger.error(text);
		error.invalidate();
		this.repaint();
	}
	
	private void repaintThis(){
		this.invalidate();
		this.pack();
		this.repaint();
	}

}
