package scopa.gui;

import gui.game.GamePanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import scopa.com.MsgBaseConf;
import scopa.com.MsgScopa;
import scopa.com.MsgScopaHand;
import scopa.com.MsgScopaNack;
import scopa.com.MsgScopaPlay;
import scopa.logic.EmptyHand;
import scopa.logic.OffuscatedScopaCard;
import scopa.logic.ScopaCard;
import scopa.logic.ScopaGame;
import scopa.logic.ScopaHand;
import scopa.logic.ScopaHandImpl;
import util.Logger;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgCaster;
import com.msg.MsgPlay;

public class ScopaGamePanel extends GamePanel {

	private Dimension minSize = new Dimension(800, 800);
	private boolean built = false;

	private PlayerName nextPlayer;

	private PlayerBorderPanel south;
	private BorderPanel west;
	private BorderPanel east;
	private BorderPanel north;
	private ScopaTablePanel table;

	public ScopaGamePanel() {
		// logic
		// this.table = ScopaFactory.getNewScopaTable();
		nextPlayer = ScopaGame.SRV_NAME;

		this.setPreferredSize(minSize);
		this.setMinimumSize(this.getPreferredSize());

		// gui
		// this.build();
	}

	private void build(ScopaHand playerHand, PlayerName eastPlayer, PlayerName northPlayer, PlayerName westPlayer) {

		this.setBackground(Color.GREEN);
		this.setPreferredSize(minSize);
		this.setMinimumSize(this.getPreferredSize());
		this.setLayout(new BorderLayout());

		// TODO retrieve correct team (if needed)
		this.south = new PlayerBorderPanel(playerHand, this);
		if (eastPlayer == null) {
			this.east = new BorderPanel(new EmptyHand(), BorderLayout.EAST);
		} else {
			this.east = new BorderPanel(eastPlayer, 0, BorderLayout.EAST);
		}
		if (westPlayer == null) {
			this.west = new BorderPanel(new EmptyHand(), BorderLayout.WEST);
		} else {
			this.west = new BorderPanel(westPlayer, 0, BorderLayout.WEST);
		}
		if (northPlayer == null) {
			this.north = new BorderPanel(new EmptyHand(), BorderLayout.NORTH);
		} else {
			this.north = new BorderPanel(northPlayer, 0, BorderLayout.NORTH);
		}

		this.table = new ScopaTablePanel();

		this.add(south, BorderLayout.SOUTH);
		this.add(west, BorderLayout.WEST);
		this.add(east, BorderLayout.EAST);
		this.add(north, BorderLayout.NORTH);
		this.add(table, BorderLayout.CENTER);

		built = true;
	}

	public void giveNewHand(List<ScopaCard> playerCards) {
		south.newHand(playerCards);
		west.newHand(null);
		east.newHand(null);
		north.newHand(null);
	}

	public void southPlay(ScopaCard playedCard) {
		south.playCard(playedCard);
	}

	public void northPlay() {
		north.playCard(new OffuscatedScopaCard());
	}

	public void eastPlay() {
		east.playCard(new OffuscatedScopaCard());
	}

	public void westPlay() {
		west.playCard(new OffuscatedScopaCard());
	}

	public void dudePlay(PlayerName name) {
		if (west.getPlayerName().equals(name)) {
			westPlay();
		} else if (east.getPlayerName().equals(name)) {
			eastPlay();
		} else if (north.getPlayerName().equals(name)) {
			northPlay();
		} else {
			Logger.error("Unknown player " + name + ". Move ignored");
		}
	}

	@Override
	public void update(MsgPlay msg) {

		try {

			MsgScopa msgScopa = MsgCaster.castMsg(MsgScopa.class, msg);
			nextPlayer = msgScopa.nextPlayerToPlayIs();
			switch (msgScopa.getScopaType()) {
			case baseConf:
				MsgBaseConf msgConf = MsgCaster.castMsg(MsgBaseConf.class, msgScopa);
				ScopaHand playerHand = new ScopaHandImpl(this.getLocalClient(), 0);
				playerHand.newHand(msgConf.getHand());

				PlayerName eastPlayer = msgConf.getPlayerEast();
				PlayerName westPlayer = msgConf.getPlayerWest();
				PlayerName northPlayer = msgConf.getPlayerNorth();

				this.build(playerHand, eastPlayer, northPlayer, westPlayer);

				table.putInitial(msgConf.getTable());
				table.invalidate();
				break;
			case play:
				MsgScopaPlay msgPlay = MsgCaster.castMsg(MsgScopaPlay.class, msgScopa);
				ScopaCard played = msgPlay.getPlayed(); // TODO display who
														// played and the replay
														// thing
				List<ScopaCard> taken = msgPlay.getTaken();
				List<ScopaCard> check = table.putCard(played, taken);
				if (check == null) {
					// Should not happen
					Logger.error("Invalid move!!!\n\tTable: " + table.toString() + "\n\tPlayed: " + played.toString() + "\n\tTaken: "
							+ Arrays.toString(taken.toArray()));
				}
				this.dudePlay(msgPlay.getSenderID());
				table.invalidate();
				break;
			case hand:
				MsgScopaHand msgHand = MsgCaster.castMsg(MsgScopaHand.class, msgScopa);
				List<? extends ScopaCard> offuscatedHand = Arrays
						.asList(new OffuscatedScopaCard(), new OffuscatedScopaCard(), new OffuscatedScopaCard());
				south.newHand(msgHand.getCards());
				north.newHand((List<ScopaCard>) offuscatedHand);
				east.newHand((List<ScopaCard>) offuscatedHand);
				west.newHand((List<ScopaCard>) offuscatedHand);
				south.invalidate();
				north.invalidate();
				east.invalidate();
				west.invalidate();
				break;
			case ack:
				// MsgScopaAck msgAck = MsgCaster.castMsg(MsgScopaAck.class,
				// msgScopa);
				// Nothing todo
				break;
			case nack:
				MsgScopaNack msgNack = MsgCaster.castMsg(MsgScopaNack.class, msgScopa);
				this.showWarningToPlayer("The server refused your last play:\n" + msgNack.getReason());
				// TODO Recover lastMove and undo it
				break;
			default:
				Logger.debug("Unknown scopa type: " + msgScopa.getScopaType() + ", ignoring it.");
			}
			this.invalidate();
			this.repaint();
		} catch (MalformedMessageException e) {
			Logger.debug("Malformed msg of type: " + msg.getGameType().getGameType() + ". Ignoring it.");
		}
	}

	public List<ScopaCard> clientPlayedOnTable(ScopaCard card) {
		List<ScopaCard> cards = table.putCard(card);
		return cards;
	}

	public List<ScopaCard> getSelectedCardsOnTable() {
		return table.getSelectedCards();
	}

	public List<List<ScopaCard>> allPossibleTakeWith(ScopaCard card) {
		return table.allPossibleTakeWith(card);
	}

	public PlayerName getNextPlayerToPlay() {
		return nextPlayer;
	}

	public boolean isLocalClientNextPlayer() {
		return south.getPlayerName().equals(nextPlayer);
	}

	public boolean play(ScopaCard playedCard, List<ScopaCard> taken) {
		List<ScopaCard> cards = table.putCard(playedCard, taken);

		if (cards == null) {
			return false;
		}
		this.nextPlayer = ScopaGame.SRV_NAME; // avoid double play
		return true;
	}

	public void sendMsgScopaPlay(ScopaCard played, List<ScopaCard> taken) {
		MsgScopaPlay msg = new MsgScopaPlay(this.getLocalClient(), played, taken, null);
		this.sendMsgPlay(msg);
	}

	public void alertPlayerWrongPlay() {
		this.showWarningToPlayer("The move you tried to do is not a valid move");
	}

	@Override
	public GamePanel clone() {
		ScopaGamePanel sgp = new ScopaGamePanel();

		if (built) {
			// FIXME does not clone properly
			// sgp.build(south.getHand(), east.getPlayerName(),
			// north.getPlayerName(), west.getPlayerName());
			// sgp.table.putCards(table.cardsOnTable());
			// sgp.nextPlayer = new PlayerName(nextPlayer.getName());
		}

		return sgp;
	}

}
