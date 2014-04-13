package scopa.gui;

import gui.api.ChatMsgSender;
import gui.api.GameGui;
import gui.game.GamePanel;
import gui.util.ChatPanel;
import gui.util.EmptyPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JSplitPane;

import scopa.com.ScopaClient;
import scopa.com.ScopaClientImpl;
import scopa.logic.ScopaRules;
import scopa.logic.card.ScopaCard;
import scopa.logic.hand.ScopaHand;
import util.Logger;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgPlay;

public class ScopaFramePanel extends GamePanel implements ScopaGui {

	private JSplitPane split;
	private ScopaGamePanel gamePanel;
	private ScopaScoreFrame scoreScoreFrame;
	private ScopaLastMovePanel lastMovePanel;
	private ChatPanel chatPanel;

	private ScopaClient msgReceiver;

	public ScopaFramePanel() {

		msgReceiver = new ScopaClientImpl(this);
		chatPanel = new ChatPanel(null);
		scoreScoreFrame = new ScopaScoreFrame(new ScopaRules(), 0); //FIXME update somewhere
		lastMovePanel = new ScopaLastMovePanel();

		split = new JSplitPane();
		split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		EmptyPanel titlePanel = new EmptyPanel();
		titlePanel.setMinimumSize(new Dimension(800, 800));
		titlePanel.setBackground(Color.MAGENTA);
		split.add(titlePanel, JSplitPane.LEFT);

		JSplitPane split2 = new JSplitPane();
		split2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split.add(split2, JSplitPane.RIGHT);
		split2.add(lastMovePanel, JSplitPane.TOP);
		split2.add(chatPanel, JSplitPane.BOTTOM);

		this.add(split);
	}

	@Override
	protected void setGameGui(GameGui gameGui) {
		this.gameGui = gameGui;
		this.gameGui.registerFrame(scoreScoreFrame); //Can not register before
		this.setPreferredSize(this.gameGui.getSize());
		chatPanel.setChatMsgSender(gameGui);
	}

	@Override
	public void update(MsgPlay msg) {

		try {
			msgReceiver.update(msg);
		} catch (MalformedMessageException e) {
			String errMsg = "Malformed msg of type: " + msg.getGameType().getGameType() + ". Ignoring it.";
			Logger.debug(errMsg);
			chatPanel.writeIntoChatFromServer(errMsg);
			return;
		}
	}

	@Override
	public GamePanel clone() {

		ScopaFramePanel newPanel = new ScopaFramePanel();
		if (gamePanel != null) {
			newPanel.gamePanel = gamePanel.clone();
			newPanel.gamePanel.invalidate();
		}
		newPanel.chatPanel.setChatMsgSender(chatPanel.getChatMsgSender());
		newPanel.scoreScoreFrame.setRules(scoreScoreFrame.getRules());
		newPanel.scoreScoreFrame.updateScore(scoreScoreFrame.getResults(), false);
		newPanel.lastMovePanel.updateLastMove(lastMovePanel.getLastPlayed().getCard(), lastMovePanel.getLastTaken().getCards());
		newPanel.scoreScoreFrame.invalidate();
		newPanel.lastMovePanel.invalidate();

		return newPanel;
	}

	@Override
	public void writeIntoChat(PlayerName writer, String text) {
		chatPanel.writeIntoChat(writer, text);
	}

	@Override
	public void writeIntoChatFromServer(String text) {
		chatPanel.writeIntoChatFromServer(text);
	}

	@Override
	public ChatMsgSender getChatMsgSender() {
		return chatPanel.getChatMsgSender();
	}

	@Override
	public void setChatMsgSender(ChatMsgSender chatMsgSender) {
		chatPanel.setChatMsgSender(chatMsgSender);
	}

	@Override
	public void updateNextPlayer(PlayerName nextPlayer) {
		lastMovePanel.updateNextPlayer(nextPlayer);
	}

	@Override
	public void setUpBaseConfiguration(ScopaHand playerHand, PlayerName eastPlayer, PlayerName northPlayer, PlayerName westPlayer,
			List<ScopaCard> cardsOnTable) {
		gamePanel = new ScopaGamePanel(this);
		gamePanel.build(playerHand, eastPlayer, northPlayer, westPlayer, cardsOnTable);
		gamePanel.invalidate();
		split.add(gamePanel, JSplitPane.LEFT);
		split.setDividerLocation(1000 + split.getInsets().left); //FIXME try to resize left panel to expected size correctly
		split.invalidate();
		this.revalidate(); //FIXME ScopaTablePanel does not display correctly in the first time
	}

	@Override
	public void giveNewHand(List<ScopaCard> cards) {
		gamePanel.giveNewHand(cards);
		gamePanel.revalidate();
	}

	@Override
	public void play(PlayerName playingPlayer, ScopaCard playedCard, List<ScopaCard> takenCards) {
		//update table and player hand
		gamePanel.playedByNetworkClient(playingPlayer, playedCard, takenCards);
		//update last played
		updateLastMove(playedCard, takenCards);
	}

	@Override
	public PlayerName getNextPlayer() {
		return lastMovePanel.getNextPlayer();
	}

	@Override
	public boolean isLocalPlayerNextToPlay() {
		return this.getLocalClient().equals(this.getNextPlayer());
	}

	@Override
	public void updateLastMove(ScopaCard played, List<ScopaCard> taken) {
		lastMovePanel.updateLastMove(played, taken);
	}

}
