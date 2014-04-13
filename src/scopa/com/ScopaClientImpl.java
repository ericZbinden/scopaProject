package scopa.com;

import java.util.List;

import scopa.gui.ScopaGui;
import scopa.logic.card.ScopaCard;
import scopa.logic.hand.ScopaHand;
import scopa.logic.hand.ScopaHandImpl;
import util.Logger;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgCaster;
import com.msg.MsgPlay;

public class ScopaClientImpl implements ScopaClient {

	private ScopaGui scopaGui;

	public ScopaClientImpl(ScopaGui scopaGui) {
		this.scopaGui = scopaGui;
	}

	@Override
	public void update(MsgPlay msg) throws MalformedMessageException {

		MsgScopa msgScopa = MsgCaster.castMsg(MsgScopa.class, msg);
		PlayerName nextPlayer = msgScopa.nextPlayerToPlayIs();
		switch (msgScopa.getScopaType()) {
		case baseConf:
			MsgBaseConf msgConf = MsgCaster.castMsg(MsgBaseConf.class, msgScopa);
			ScopaHand playerHand = new ScopaHandImpl(scopaGui.getLocalClient(), 0);
			playerHand.newHand(msgConf.getHand());

			PlayerName eastPlayer = msgConf.getPlayerEast();
			PlayerName westPlayer = msgConf.getPlayerWest();
			PlayerName northPlayer = msgConf.getPlayerNorth();

			scopaGui.setUpBaseConfiguration(playerHand, eastPlayer, northPlayer, westPlayer, msgConf.getTable());
			break;
		case play:
			MsgScopaPlay msgPlay = MsgCaster.castMsg(MsgScopaPlay.class, msgScopa);
			ScopaCard played = msgPlay.getPlayed();
			List<ScopaCard> taken = msgPlay.getTaken();
			PlayerName playingPlayer = msgPlay.getSenderID();

			scopaGui.play(playingPlayer, played, taken);

			//			List<ScopaCard> check = table.putCard(played, taken);
			//			if (check == null) {
			//				// Should not happen
			//				Logger.error("Invalid move!!!\n\tTable: " + table.toString() + "\n\tPlayed: " + played.toString() + "\n\tTaken: "
			//						+ Arrays.toString(taken.toArray()));
			//			}
			//			lastMovePanel.updateLastMove(played, taken);
			//			this.dudePlay(msgPlay.getSenderID());
			//			table.invalidate();
			break;
		case hand:
			MsgScopaHand msgHand = MsgCaster.castMsg(MsgScopaHand.class, msgScopa);

			List<ScopaCard> cards = msgHand.getCards();
			scopaGui.giveNewHand(cards);
			//			south.newHand(msgHand.getCards());
			//			north.newHand((List<ScopaCard>) offuscatedHand);
			//			east.newHand((List<ScopaCard>) offuscatedHand);
			//			west.newHand((List<ScopaCard>) offuscatedHand);
			//			south.invalidate();
			//			north.invalidate();
			//			east.invalidate();
			//			west.invalidate();
			break;
		case ack:
			// MsgScopaAck msgAck = MsgCaster.castMsg(MsgScopaAck.class, msgScopa);
			// Nothing todo
			break;
		case nack:
			MsgScopaNack msgNack = MsgCaster.castMsg(MsgScopaNack.class, msgScopa);
			scopaGui.showMessageDialog("The server refused your last play:\n" + msgNack.getReason());
			// TODO Recover lastMove and undo it / until then, this breaks the game workflow
			//scopaGui.recoverLastMove()
			break;
		default:
			Logger.debug("Unknown scopa type: " + msgScopa.getScopaType() + ", ignoring it.");
		}

		scopaGui.updateNextPlayer(nextPlayer);
	}

}
