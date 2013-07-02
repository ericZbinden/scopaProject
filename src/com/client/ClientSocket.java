package com.client;

import game.GameType;
import gui.wait.ControlPanel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import util.Logger;

import com.msg.Message;
import com.msg.MsgChat;
import com.msg.MsgMasterGame;
import com.msg.MsgMasterRule;
import com.msg.MsgPlay;
import com.msg.MsgStart;
import com.msg.MsgType;
import com.msg.MsgWRslot;
import com.server.wait.*;

public class ClientSocket implements Runnable{

	Socket sock;
	ObjectInputStream in;
	ObjectOutputStream out;
	ControlPanel cp;
	
	public ClientSocket(Socket sock, ObjectInputStream in, ObjectOutputStream out,ControlPanel cp) {
		this.sock=sock;
		this.in=in;
		this.out=out;
		this.cp=cp;
	}
	
/*********************** SEND MSG ****************************/
	
	public void sendChatMsg(String id, String txt){
		this.sendOrClose(new MsgChat(id,txt));
	}
	
	public void sendStartMsg(){
		this.sendOrClose(new MsgStart(cp.getClientID()));
	}
		
	public void sendWrSlotMsg(Config conf, String impactedID){
		MsgWRslot msg;
		if (conf instanceof ClosedConf){
			msg = new MsgWRslot((ClosedConf)conf,cp.getClientID(),impactedID);
		} else if (conf instanceof EmptyConf){
			msg = new MsgWRslot((EmptyConf)conf,cp.getClientID(),impactedID);
		} else {
			msg = new MsgWRslot(conf,cp.getClientID());
		}
		this.sendOrClose(msg);
	}
	
	public void sendGameMsg(GameType gameType){
		MsgMasterGame msg = new MsgMasterGame(gameType,cp.getClientID());
		this.sendOrClose(msg);
	}
	
	public void sendRuleMsg(MsgMasterRule msg){
		this.sendOrClose(msg);
	}
	
	public void sendPlayMsg(MsgPlay msg){
		this.sendOrClose(msg);
	}
	
/*********************** CONCREAT SEND ******************************/	
	
	/**
	 * Send the message with succes or close the socket
	 * @param msg
	 */
	private void sendOrClose(Message msg){
		try{
			sendMsg(msg);
		} catch (IOException e) {
			ioError(e,msg);
		}
	}
	
	private void sendMsg(Message msg) throws IOException{
			out.writeObject(msg);
			Logger.debug("ClientSocket sended:\n"+msg);
	}
	
	/**
	 * Unrecoverable error, close socket
	 * @param e
	 * @param msg
	 */
	private void ioError(IOException e, Message msg){
		Logger.error("Unable to send msg: "+msg.toString()+"\nCaused by: "+e.getLocalizedMessage());
		e.printStackTrace();	
		//kill 
		this.close();
	}
	
	public void close(){		
		try{
			in.close();
			out.close();
			sock.close();
		} catch(Exception e){
			//die silently
		}		
	}
	
	
/************************* MISC *******************************/
	
	
	public void setControlPanel(ControlPanel cp){
		this.cp = cp;
	}
	
	

	

/**************** HANDLER RECEIVING MSG ************************/

	@Override
	public void run() {
		boolean running = true;
		while(running){
			
			try {
				
				Logger.debug("client "+cp.getClientID()+" blocking waiting new Msg");
				Message msg = (Message) in.readObject();
				MsgType type = msg.getType();
				Logger.debug("client "+cp.getClientID()+" received msg: "+type);			
				cp.update(msg);
					
			} catch (SocketException se) {
				Logger.debug("socket closed by server: "+se.getMessage());
				cp.serverDown();
				running = false;
			} catch (ClassNotFoundException cnfe) {
				Logger.error(cnfe.getMessage());
			} catch (IOException e){
				Logger.error("Down connection: "+e.getLocalizedMessage()+"\nStop listening");
				cp.serverDown();
				running = false;
			} finally { 
				if (!running)
					this.close();
			}		
		}		
	}

}
