package scopa.com;

import java.util.List;

import scopa.logic.card.ScopaCard;
import util.PlayerName;

public class MsgBaseConf extends MsgScopa {

	private static final long serialVersionUID = 4133923203224677907L;

	private String playerNorth;

	private String playerWest;

	private String playerEast;

	private List<ScopaCard> hand;

	private List<ScopaCard> table;

	public MsgBaseConf(PlayerName playerNorth, PlayerName playerWest, PlayerName playerEast, List<ScopaCard> hand, List<ScopaCard> table,
			PlayerName nextPlayer) {
		super(ScopaMsgType.baseConf, nextPlayer, null, null);

		if (playerNorth != null) {
			this.playerNorth = playerNorth.getName();
		}

		if (playerWest != null) {
			this.playerWest = playerWest.getName();
		}

		if (playerEast != null) {
			this.playerEast = playerEast.getName();
		}

		this.hand = hand;
		this.table = table;
	}

	public List<ScopaCard> getHand() {
		return hand;
	}

	public List<ScopaCard> getTable() {
		return table;
	}

	public PlayerName getPlayerNorth() {
		if (playerNorth != null)
			return new PlayerName(playerNorth);

		return null;
	}

	public PlayerName getPlayerWest() {
		if (playerWest != null)
			return new PlayerName(playerWest);

		return null;
	}

	public PlayerName getPlayerEast() {
		if (playerEast != null)
			return new PlayerName(playerEast);

		return null;
	}

	@Override
	public String toString() {
		return super.toString() + "\n\tPlayers[W: " + playerWest + ", N: " + playerNorth + ", E: " + playerEast + "]" + "\n\tTable: "
				+ buildStringFromList(table) + "\n\tHand: " + buildStringFromList(hand);
	}

	private String buildStringFromList(List<ScopaCard> list) {
		String str = "";
		for (ScopaCard card : list) {
			str += card.toString() + ", ";
		}
		return str;
	}
}
