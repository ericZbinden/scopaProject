package scopa.logic;

import game.GameType;
import game.Playable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scopa.com.MsgBaseConf;
import scopa.com.MsgScopa;
import scopa.com.MsgScopaAck;
import scopa.com.MsgScopaHand;
import scopa.com.MsgScopaNack;
import scopa.com.MsgScopaPlay;
import scopa.com.MsgScopaRules;
import scopa.com.MsgScopaScore;
import scopa.com.ScopaMsgType;
import scopa.logic.card.ScopaCard;
import scopa.logic.hand.EmptyHand;
import scopa.logic.hand.ScopaHand;
import scopa.logic.hand.ScopaHandImpl;
import util.Logger;
import util.PlayerName;

import com.msg.MalformedMessageException;
import com.msg.MsgCaster;
import com.msg.MsgMasterRule;
import com.msg.MsgPlay;
import com.server.api.ServerApi;
import com.server.exceptions.IllegalInitialConditionException;
import com.server.impl.Server;
import com.server.wait.ClosedConf;
import com.server.wait.Config;
import com.server.wait.EmptyConf;

public class ScopaGame implements Playable {

	public final static PlayerName SRV_NAME = Server.SERVER_NAME;

	private ServerApi api;

	private int nPlayer;

	private PlayerName lastPlayerToTake;
	private State donour;

	private ScopaScore score;

	private State state;

	private ScopaDeck deck;
	private ScopaTable table;
	private Map<PlayerName, ScopaHand> hands; // PLAYERS - HAND
	private Map<State, PlayerName> players; // Player number - Players name
	private Map<ScopaRule, Boolean> rules;

	public ScopaGame() {
		// CURRENT MAX PLAYER = 4
		this.state = State.notStarted;
		hands = new HashMap<>(4);
		rules = new HashMap<ScopaRule, Boolean>();
		players = new HashMap<>(4);
	}

	@Override
	public void initGame(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException {
		/** check condition **/
		List<Config> playableConfig = new ArrayList<>(configs.size());
		Map<Integer, Integer> teams = new HashMap<Integer, Integer>();
		for (Config conf : configs) {
			if (conf instanceof EmptyConf || conf instanceof ClosedConf) {
				continue;
			} else {
				playableConfig.add(conf);
				int team = conf.getTeam();
				Integer teamMate = teams.get(team);
				if (teamMate == null) {
					teams.put(team, 1);
				} else {
					teams.put(team, teamMate + 1);
				}
			}
		}
		checkInitialConfig(playableConfig, rules);

		/** players **/
		nPlayer = playableConfig.size();

		/** team repartition **/
		int nbTeam = teams.size();

		// to obtain a random n°1 player to start with
		Collections.shuffle(playableConfig);

		// Create all scopa hand
		Config conf = playableConfig.get(0);
		ScopaHand hand1 = new ScopaHandImpl(conf.getClientID(), conf.getTeam());
		donour = State.p1;
		conf = playableConfig.get(1);
		ScopaHand hand2 = new ScopaHandImpl(conf.getClientID(), conf.getTeam());
		ScopaHand hand3 = null;
		ScopaHand hand4 = null;

		if (nPlayer > 3) {
			conf = playableConfig.get(3);
			hand4 = new ScopaHandImpl(conf.getClientID(), conf.getTeam());
			conf = playableConfig.get(2);
			hand3 = new ScopaHandImpl(conf.getClientID(), conf.getTeam());
		} else if (nPlayer > 2) {
			conf = playableConfig.get(2);
			hand3 = new ScopaHandImpl(conf.getClientID(), conf.getTeam());
			hand4 = new EmptyHand();
		} else {
			hand3 = new EmptyHand();
			hand4 = new EmptyHand();
		}

		playableConfig.add(0, null); // little hack for team readability

		if (nbTeam == nPlayer) {
			// Each player play by itself
			hands.put(hand1.getPlayerName(), hand1);
			players.put(State.p1, hand1.getPlayerName());

			hands.put(hand2.getPlayerName(), hand2);
			players.put(State.p2, hand2.getPlayerName());

			if (nPlayer > 2) {
				hands.put(hand3.getPlayerName(), hand3);
				players.put(State.p3, hand3.getPlayerName());
			}

			if (nPlayer > 3) {
				hands.put(hand4.getPlayerName(), hand4);
				players.put(State.p4, hand4.getPlayerName());
			}

		} else {
			// Play by team of 2
			// Teams are: (P1,P3) and (P2,P4)
			hands.put(hand1.getPlayerName(), hand1);
			players.put(State.p1, hand1.getPlayerName());

			// is in the same team than p1
			if (playableConfig.get(1).getTeam() == playableConfig.get(2).getTeam()) {
				// YES : set as p3
				hands.put(hand2.getPlayerName(), hand2);
				players.put(State.p3, hand2.getPlayerName());

				// the two next player are teaming
				hands.put(hand3.getPlayerName(), hand3);
				players.put(State.p2, hand3.getPlayerName());
				hands.put(hand4.getPlayerName(), hand4);
				players.put(State.p4, hand4.getPlayerName());

			} else {
				// NO : set as p2
				hands.put(hand2.getPlayerName(), hand2);
				players.put(State.p2, hand2.getPlayerName());

				if (playableConfig.get(3).getTeam() == playableConfig.get(1).getTeam()) {
					// is in the same team than p1
					// YES : set as p3
					hands.put(hand3.getPlayerName(), hand3);
					players.put(State.p3, hand3.getPlayerName());

					// Last player set as p4 with p2
					hands.put(hand4.getPlayerName(), hand4);
					players.put(State.p4, hand4.getPlayerName());

				} else {
					// NO : set as p4
					hands.put(hand3.getPlayerName(), hand3);
					players.put(State.p4, hand3.getPlayerName());

					// Last player set as p3 with p1
					hands.put(hand4.getPlayerName(), hand4);
					players.put(State.p3, hand4.getPlayerName());
				}
			}
		}

		/** rules **/
		MsgScopaRules msg = (MsgScopaRules) rules;
		this.rules.put(ScopaRule.napoli, msg.getNapoli());
		this.rules.put(ScopaRule.reverse, msg.getReverse());
		this.rules.put(ScopaRule.scopa, msg.getScopa());

		/** ready to start **/
		state = nextState();
	}

	@Override
	public GameType getGameType() {
		return GameType.SCOPA;
	}

	@Override
	public void checkInitialConfig(List<Config> configs, MsgMasterRule rules) throws IllegalInitialConditionException {

		List<Config> playableConfig = new ArrayList<>(configs.size());
		Map<Integer, Integer> teams = new HashMap<Integer, Integer>();

		// Check config and config number
		for (Config conf : configs) {
			if (conf instanceof EmptyConf || conf instanceof ClosedConf) {
				continue;
			} else {
				playableConfig.add(conf);
				int team = conf.getTeam();
				Integer teamMate = teams.get(team);
				if (teamMate == null) {
					teams.put(team, 1);
				} else {
					teams.put(team, teamMate + 1);
				}
			}
		}

		if (playableConfig.size() < 2 || playableConfig.size() > 4) {
			throw new IllegalInitialConditionException("Too much or too less player: " + playableConfig.size() + ". Need [2,3,4] players");
		}
		// Check team
		if (teams.size() < 2) {
			throw new IllegalInitialConditionException("Need at least 2 teams");
		}

		int p = 0;
		for (Entry<Integer, Integer> players : teams.entrySet()) {
			Logger.debug("Team " + players.getKey() + " haz " + players.getValue() + " players");
			if (p == 0) {
				p = players.getValue();
				continue;
			} else if (p == players.getValue()) {
				continue;
			} else {
				throw new IllegalInitialConditionException("Each team should have same number of players");
			}
		}

		// Check rules
		if (!(rules instanceof MsgScopaRules)) {
			throw new IllegalInitialConditionException("The msgRules should be MsgScopaRules type but was: " + rules.getClass());
		}

		state = nextState();
	}

	@Override
	public void start(ServerApi api) {
		this.api = api;
		this.newGame();
	}

	private MsgBaseConf getMsgGameBaseConf(ScopaHand handy, PlayerName nextPlayer) {
		PlayerName currentPlayer = handy.getPlayerName();
		PlayerName north = null, west = null, east = null;
		if (nPlayer == 2) {
			if (currentPlayer.equals(players.get(State.p1))) {
				north = players.get(State.p2);
			} else {
				north = players.get(State.p1);
			}
		} else {
			if (currentPlayer.equals(players.get(State.p1))) {
				east = players.get(State.p2);
				north = players.get(State.p3);
				west = players.get(State.p4);
			} else if (currentPlayer.equals(players.get(State.p2))) {
				east = players.get(State.p3);
				north = players.get(State.p4);
				west = players.get(State.p1);
			} else if (currentPlayer.equals(players.get(State.p3))) {
				east = players.get(State.p4);
				north = players.get(State.p1);
				west = players.get(State.p2);
			} else {
				east = players.get(State.p1);
				north = players.get(State.p2);
				west = players.get(State.p3);
			}
		}

		return new MsgBaseConf(north, west, east, handy.getCardsInHand(), table.cardsOnTable(), nextPlayer);
	}

	private ScopaHand getHandByPlayerName(PlayerName name) {
		ScopaHand hand = hands.get(name);
		if (hand != null) {
			return hand;
		}

		Logger.debug("Unable to recover the hand of player " + name + ".\n" + "Available hands are: " + Arrays.toString(hands.keySet().toArray()));
		return null;

	}

	private ScopaHand getHandByPlayerState(State playerId) {
		PlayerName playerName = players.get(playerId);
		if (playerName != null) {
			return getHandByPlayerName(playerName);
		}
		Logger.debug("Unable to recover the hand of player id " + playerId + ".\n" + "Available hands are: "
				+ Arrays.toString(players.keySet().toArray()));
		return null;
	}

	private PlayerName getNextPlayer() {
		switch (state) {
		case p1:
		case p2:
		case p3:
		case p4:
			return players.get(state);
		default:
			return SRV_NAME;
		}
	}

	/**
	 * ACTION METHODS
	 */

	/** Create a new table, a new deck and distribute first hand */
	private void newGame() {

		table = ScopaFactory.getNewScopaTable();
		boolean ok = false;
		do {
			deck = ScopaFactory.getNewScopaDeck();
			deck.shuffle();
			ok = table.putInitial(deck.drawInitialCards());

		} while (!ok);

		// Recover nextPlayer before state update
		PlayerName nextPlayer = this.getNextPlayer();

		for (ScopaHand hand : hands.values()) {
			assert hand.isEmpty() : "Hand is not empty ";
			hand.newHand(deck.draw3Cards());
			api.sendMsgTo(hand.getPlayerName(), this.getMsgGameBaseConf(hand, nextPlayer));
		}
		Logger.debug("Donour: " + this.getHandByPlayerState(donour).getPlayerName() + " / ExpectedNextPlayer: " + this.getNextPlayer());
	}

	@Override
	public void receiveMsgPlay(MsgPlay msg) throws MalformedMessageException {

		if (!this.getGameType().equals(msg.getGameType())) {
			throw new MalformedMessageException("Expected " + this.getGameType() + " game type but was: " + msg.getGameType());
		}

		MsgScopa msgScopa = MsgCaster.castMsg(MsgScopa.class, msg);

		switch (msgScopa.getScopaType()) {
		case play:

			MsgScopaPlay msgPlay = MsgCaster.castMsg(MsgScopaPlay.class, msgScopa);
			PlayerName sender = msgPlay.getSenderID();

			if (!sender.equals(this.getNextPlayer())) {
				// Refused because not your time to play
				Logger.debug("Player " + sender + " tried to play at " + this.getNextPlayer() + " turn. Ignoring this msg");
				api.sendMsgTo(sender, new MsgScopaNack(this.getNextPlayer(), "Time to play to " + this.getNextPlayer() + ", not you"));
			} else if (play(sender, msgPlay.getPlayed(), msgPlay.getTaken())) {
				// Msg accepted
				PlayerName nextPlayer = this.getNextPlayer();
				api.sendMsgTo(sender, new MsgScopaAck(nextPlayer));
				api.sendMsgToAllExcept(sender, new MsgScopaPlay(sender, msgPlay.getPlayed(), msgPlay.getTaken(), nextPlayer));

				serverPlay();

			} else if (state.equals(State.unknown)) {
				// Unexpected error
				this.closeGameDueToError("Unauthorized play: " + msgPlay.toString() + "\nwhile on table: "
						+ Arrays.toString(table.cardsOnTable().toArray()));
			} else {
				// Msg refused because is invalid
				String errorReason = "Invalid play, ignoring this msg";
				Logger.debug(errorReason);
				api.sendMsgTo(sender, new MsgScopaNack(this.getNextPlayer(), errorReason));
			}
			break;
		default:
			Logger.debug("Invalid scopaType: " + msgScopa.getScopaType() + ". Expected " + ScopaMsgType.play);
			throw new MalformedMessageException();
			// api.sendMsgTo(msgPlay.getSenderID(), new
			// MsgScopaNack(this.getNextPlayer())); ??
		}
	}

	private boolean play(PlayerName player, ScopaCard playedCard, List<ScopaCard> takenCards) {

		ScopaHand hand = null;
		switch (state) {
		case p1:
		case p2:
		case p3:
		case p4:
			hand = this.getHandByPlayerState(state);
			// Not good player or Selected card not in hand
			if (hand == null || !hand.playCard(playedCard)) {
				closeGameDueToError("Move invalid. " + (hand == null ? "hand null" : hand.toString()));
				return false;
			}
			break;
		default:
			closeGameDueToError("Player " + player.getName() + " played but at " + getNextPlayer().getName() + " to play");
			return false;
		}

		if (state != State.unknown) {
			List<ScopaCard> cs = table.putCard(playedCard, takenCards);
			if (cs == null) {
				closeGameDueToError("Invalid move: " + playedCard + " taking: " + Arrays.toString(takenCards.toArray()) + "\nwith table containing: "
						+ Arrays.toString(table.cardsOnTable().toArray()));
				return false;
			} else if (!cs.isEmpty()) {
				hand.addCardsToHeap(cs);
				lastPlayerToTake = player;
			}

			state = nextState();
			return true;
		}

		// should not goes there but anyway...
		closeGameDueToError("Unknown state");
		return false;
	}

	private void serverPlay() {
		PlayerName nextPlayer = this.getNextPlayer();
		if (SRV_NAME.equals(nextPlayer)) {
			switch (state) {
			case giveNewHand:
				giveNewHands();
				break;
			case takeAll:
				takeAll();
				break;
			case endSet:
				sendScore();
				break;
			default:
				Logger.error("SRV should play but do nothing");
				this.closeGameDueToError("Invalid state " + state + ", SRV should rather do something");
			}
			serverPlay();
		}
	}

	private void takeAll() {

		ScopaHand hand = this.getHandByPlayerName(lastPlayerToTake);
		if (hand == null) {
			closeGameDueToError("Unknown player " + lastPlayerToTake + ", unable to take all cards.");
			return;
		}

		hand.addCardsToHeap(table.takeAll());
		state = nextState();

	}

	private void giveNewHands() {
		for (ScopaHand hand : hands.values()) {
			hand.newHand(deck.draw3Cards());
		}

		state = nextState();
		sendHands();
	}

	private void sendHands() {
		PlayerName nextPlayer = getNextPlayer();

		for (ScopaHand hand : hands.values()) {
			api.sendMsgTo(hand.getPlayerName(), new MsgScopaHand(hand.getCardsInHand(), nextPlayer));
		}
	}

	private boolean sendScore() {
		api.sendMsgToAll(new MsgScopaScore(score));
		return score.checkWinner();
	}

	private void closeGameDueToError(String reason) {
		state = State.unknown;
		Logger.error("Update state to unknown. Reason: " + reason);
		// TODO warn players and close the game
	}

	/**
	 * STATE MACHINE METHODS
	 */

	private State nextState() {
		Logger.debug("State was: " + state);
		switch (state) {
		case notStarted:
			return State.readyToStart;
		case readyToStart:
			return State.p2;
		case p1:
			if (donour.equals(State.p1)) {
				if (this.getHandByPlayerState(State.p1).isEmpty()) {
					if (deck.isEmpty()) {
						return State.takeAll;
					} else {
						return State.giveNewHand;
					}
				} else {
					return State.p2;
				}
			} else {
				return State.p2;
			}
		case p2:
			if (donour.equals(State.p2)) {
				if (this.getHandByPlayerState(State.p2).isEmpty()) {
					if (deck.isEmpty()) {
						return State.takeAll;
					} else {
						return State.giveNewHand;
					}
				} else {
					if (nPlayer > 2) {
						return State.p3;
					} else {
						return State.p1;
					}
				}
			} else {
				if (nPlayer > 2) {
					return State.p3;
				} else {
					return State.p1;
				}
			}
		case p3:
			if (donour.equals(State.p3)) {
				if (this.getHandByPlayerState(State.p3).isEmpty()) {
					if (deck.isEmpty()) {
						return State.takeAll;
					} else {
						return State.giveNewHand;
					}
				} else {
					if (nPlayer > 3) {
						return State.p4;
					} else {
						return State.p1;
					}
				}
			} else {
				if (nPlayer > 3) {
					return State.p4;
				} else {
					return State.p1;
				}
			}
		case p4:
			if (donour.equals(State.p4)) {
				if (this.getHandByPlayerState(State.p4).isEmpty()) {
					if (deck.isEmpty()) {
						return State.takeAll;
					} else {
						return State.giveNewHand;
					}
				} else {
					return State.p1;
				}
			} else {
				return State.p1;
			}

		case giveNewHand:
			if (donour.equals(State.p1)) {
				return State.p2;
			} else if (donour.equals(State.p2)) {
				if (nPlayer > 2) {
					return State.p3;
				} else {
					return State.p1;
				}
			} else if (donour.equals(State.p3)) {
				if (nPlayer > 3) {
					return State.p4;
				} else {
					return State.p1;
				}
			} else if (donour.equals(State.p4)) {
				return State.p1;
			} else {
				throw new IllegalStateException();
			}
		case takeAll:
			return State.endSet;
		case endSet:
			if (score.checkWinner()) {
				return State.endMatch;
			} else {
				return State.readyToStart; // TODO update donour, State =
											// donour+1
			}
		case endMatch:
			return State.endMatch; // TODO might be different
		default:
			return State.unknown;
		}
	}

	private enum State {
		notStarted, readyToStart, p1, p2, p3, p4, giveNewHand, takeAll, endSet, endMatch, unknown
	}

}
