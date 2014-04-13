package scopa.logic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scopa.logic.hand.ScopaHand;
import util.Logger;
import util.PlayerName;

public class ScopaScoreTotal {

	private boolean winner = false;
	private Integer winnerTeam = null;
	private final int nPlayer;

	private final Map<PlayerName, Integer> teamMap; // map PLAYER - TEAM
	private Map<Integer, Integer> teamCurrentScore; // map TEAM - SCORE of this game
	private Map<Integer, Integer> teamGameWonScore; // map TEAM - GAME WON
	private ScopaRules rules;

	public ScopaScoreTotal(Map<PlayerName, Integer> teamMap, ScopaRules rules) {
		this.teamMap = new HashMap<>(teamMap);

		this.rules = rules;

		nPlayer = this.teamMap.size();
		if (nPlayer < 2 || nPlayer > 4) {
			throw new IllegalArgumentException("Too much or too less player: " + nPlayer + ". Need [2,3,4] players");
		}

		teamCurrentScore = new HashMap<>(this.teamMap.size());
		teamGameWonScore = new HashMap<>(this.teamMap.size());
		for (Integer team : this.teamMap.values()) {
			teamCurrentScore.put(team, 0);
			teamGameWonScore.put(team, 0);
		}
	}

	public void markScopa(PlayerName scoringPlayer) {
		if (rules.isRuleScopaEnable()) {
			this.markPoints(scoringPlayer, 1);
		}
	}

	public void markNapoli(ScopaHand hand) {
		if (rules.isRuleNapoliEnable()) {

		}
	}

	private void markPoints(PlayerName scoringPlayer, int point) {
		Integer scoringTeam = teamMap.get(scoringPlayer);
		if (scoringTeam == null) {
			String msg = "Player " + scoringPlayer + " is unknown. Expected player in " + Arrays.toString(teamMap.values().toArray());
			Logger.error(msg);
			throw new IllegalArgumentException(msg);
		}

		teamCurrentScore.put(scoringTeam, teamCurrentScore.get(scoringTeam) + point);
	}

	public void resetMatch() {
		for (Integer team : teamCurrentScore.keySet()) {
			teamCurrentScore.put(team, 0);
		}
		winner = false;
		winnerTeam = null;
	}

	public void markScore(Map<PlayerName, Integer> markedPoints) {
		for (Entry<PlayerName, Integer> entry : markedPoints.entrySet()) {
			markPoints(entry.getKey(), entry.getValue());
		}
	}

	public boolean checkWinner() {
		// TODO rework me
		if (winner) {
			return true;
		}

		int worstScore = Integer.MAX_VALUE;
		int firstBestScore = Integer.MIN_VALUE;
		int secondBestScore = Integer.MIN_VALUE;
		int winningTeam = -1;
		int loosingTeam = -1;
		int teamScore = Integer.MIN_VALUE;
		int teamNumber = -1;

		for (Entry<Integer, Integer> entry : teamCurrentScore.entrySet()) {
			teamScore = entry.getValue();
			teamNumber = entry.getKey();
			if (firstBestScore < teamScore) {
				secondBestScore = firstBestScore;
				firstBestScore = teamScore;
				winningTeam = teamNumber;
			}
			if (worstScore > teamScore) {
				worstScore = teamScore;
				loosingTeam = teamNumber;
			}
		}

		if (firstBestScore > 10 && (secondBestScore + 2) <= firstBestScore) {
			winner = true;
			if (rules.isRuleReverseEnable()) {
				winnerTeam = loosingTeam;
			} else {
				winnerTeam = winningTeam;
			}
			return true;
		}

		return false;
	}

	public Map<Integer, Integer> getCurrentTeamScores() {
		return new HashMap<>(teamCurrentScore);
	}

	public Map<Integer, Integer> getMatchTeamScores() {
		return new HashMap<>(teamGameWonScore);
	}

	public Integer getWinnerTeam() {
		return winnerTeam;
	}

}
