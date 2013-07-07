package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class ChatPanel extends JPanel implements MouseListener, KeyListener {
	
	private JTextField chatEntry = new JTextField();
	private JTextArea chat = new JTextArea();
	
	private ChatMsgSender chatMsgSender;

	public ChatPanel(ChatMsgSender chatMsgSender) {
		//TODO in later version, use JEditorPane or JTextPane instead (give more possibilities)
		this.chatMsgSender = chatMsgSender;
		this.chatEntry = new JTextField();
		this.chat = new JTextArea();
		
		chat.setRows(10);
		chat.setText("");
		chat.setLineWrap(true);
		chat.setEditable(false);		
		chatEntry.setPreferredSize(new Dimension(290,30));
		chatEntry.addKeyListener(this);
		chat.addMouseListener(this);

		JScrollPane jsp = new JScrollPane(chat);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setPreferredSize(new Dimension(290,270));
		Box chatbox = new Box(BoxLayout.Y_AXIS);
		chatbox.add(jsp);
		chatbox.add(chatEntry);
		this.add(chatbox);
		//this.setPreferredSize(new Dimension(290,300));
		//this.setMinimumSize(this.getPreferredSize());
	}
	
	public synchronized void writeIntoChat(String writer, String text){
		chat.append("["+writer+"]: "+text+"\n");
		chat.invalidate();
		chat.repaint();
	}
	
	public void writeIntoChatFromServer(String text){
		int textSize = chat.getText().length();
		this.writeIntoChat("Server", text);
		Highlighter.HighlightPainter serverHighlight = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
		try {
			chat.getHighlighter().addHighlight(textSize, textSize+8, serverHighlight);
		} catch (BadLocationException e1) {
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(arg0.getSource().equals(chat)){
			this.chatEntry.requestFocus();
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
	public void keyPressed(KeyEvent arg0) {		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (arg0.getKeyChar() == '\n') {
			String txt = chatEntry.getText();
			chatEntry.setText("");
			writeIntoChat(chatMsgSender.getLocalClient(),txt);
			
			chatMsgSender.sendChatMsg(txt);
		}
	}

}
