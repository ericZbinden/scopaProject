package scopa.gui;

import java.util.List;

import scopa.logic.card.ScopaCard;
import scopa.logic.hand.ScopaHand;
import util.PlayerName;

import com.msg.MsgPlay;

public interface ScopaGui {

	public void updateNextPlayer(PlayerName nextPlayer);

	public void updateLastMove(ScopaCard played, List<ScopaCard> taken);

	public void showMessageDialog(String msg);

	public PlayerName getLocalClient();

	public PlayerName getNextPlayer();

	public boolean isLocalPlayerNextToPlay();

	public void setUpBaseConfiguration(ScopaHand playerHand, PlayerName eastPlayer, PlayerName northPlayer, PlayerName westPlayer,
			List<ScopaCard> cardsOnTable);

	public void giveNewHand(List<ScopaCard> cards);

	public void play(PlayerName playingPlayer, ScopaCard playedCard, List<ScopaCard> takenCards);

	public void sendMsgPlay(MsgPlay msg);

}
