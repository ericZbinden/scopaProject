package gui;

import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Entry point here
 * @author Coubii
 *
 */
public class StartFrame extends JFrame implements ActionListener {
	
	private List<StartServerFrame> ssfs = new ArrayList <>();
	private JButton createServer = new JButton("Create new Server");
	private JButton connectToServer = new JButton("Connect to a server");
	private JButton exit = new JButton("Exit");

	public StartFrame() throws HeadlessException {
		super();
        setLayout(new GridLayout(3,1));				
		this.add(createServer);
		this.add(connectToServer);
		this.add(exit);
		
		createServer.addActionListener(this);
		connectToServer.addActionListener(this);
		exit.addActionListener(this);
		
		this.setTitle("Scopa Project");
		this.setLocationRelativeTo(null); //center on screen
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(createServer)){			
			ssfs.add(new StartServerFrame(this.getLocationOnScreen()));
		} else if(arg0.getSource().equals(connectToServer)){
			new StartClientFrame(this.getLocationOnScreen());
			this.dispose();
		} else {
			boolean serverRunning = false;
			for(StartServerFrame ssf : ssfs){
				if (ssf.serverRunnuing()){
					serverRunning = true;
					break;
				}
			}
			if(serverRunning){
				int choice = JOptionPane.showConfirmDialog(this, "Server still running, do you really want to exit ?");
				switch(choice){
				case JOptionPane.YES_OPTION:
					for(StartServerFrame ssf : ssfs){
						ssf.closeServer();
					}
					System.exit(0);
				case JOptionPane.NO_OPTION:
				case JOptionPane.CANCEL_OPTION:
				default:
				}
			} else {
				System.exit(0);
			}
		}
		
	}
	
	public static void main(String[] args){
		new StartFrame();
	}
}
