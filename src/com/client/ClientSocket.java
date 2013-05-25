package com.client;

import gui.wait.ControlPanel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import util.Logger;

import com.msg.Message;
import com.msg.MsgChat;
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
	
	
	public void sendChatMsg(String id, String txt){
		sendMsg(new MsgChat(id,txt));
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
		sendMsg(msg);
	}
	
	
	public void sendMsg(Message msg){
		try {
			out.writeObject(msg);
			Logger.debug("ClientSocket sended:\n"+msg);
		} catch (IOException e) {
			Logger.debug("Pas réussi à envoyer ce msg");
			e.printStackTrace();
		}
	}


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
	
	public void close(){		
		try{
			in.close();
			out.close();
			sock.close();
		} catch(Exception e){
			//die silently
		}		
	}

}
