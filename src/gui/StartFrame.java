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

	@SuppressWarnings("unused")
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(createServer)){			
			ssfs.add(new StartServerFrame(this.getLocationOnScreen()));
			//super.dipose() TODO
		} else if(arg0.getSource().equals(connectToServer)){
			new StartClientFrame(this.getLocationOnScreen());
			super.dispose();
		} else if(arg0.getSource().equals(exit)){
			this.dispose();
		}
		
	}
	
	/**
	 * Unlike super.dispose, this call System.exit if no server is running or if client confirm his choice.
	 */
	@Override
	public void dispose(){
		if(canExit()){
			System.exit(0);
		}
	}
	
	private boolean canExit(){
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
				return true;
			case JOptionPane.NO_OPTION:
			case JOptionPane.CANCEL_OPTION:
			default:
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args){
		new StartFrame();
	}
}
