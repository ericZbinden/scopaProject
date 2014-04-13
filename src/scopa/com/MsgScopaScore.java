package scopa.com;

import java.util.HashMap;
import java.util.Map;

import scopa.logic.ScopaGame;
import scopa.logic.ScopaScoreTotal;
import scopa.logic.ScopaSetResult;

public class MsgScopaScore extends MsgScopa {

	private Map<Integer, ScopaSetResult> scores;
	private boolean finish;

	public MsgScopaScore(ScopaScoreTotal scores) {
		super(ScopaMsgType.score, ScopaGame.SRV_NAME);
		//TODO implement me
	}

	public boolean isFinished() {
		return finish;
	}

	public Map<Integer, ScopaSetResult> getResults() {
		return new HashMap<>(scores);
	}

	public ScopaSetResult getTeamResult(int teamNumber) {
		return scores.get(teamNumber);
	}

}
