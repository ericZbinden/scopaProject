package scopa.com;

import scopa.logic.ScopaGame;
import scopa.logic.ScopaScore;

public class MsgScopaScore extends MsgScopa {

	private int scoreT1;
	
	private int scoreT2;

	private int scoreT3;

	private int scoreT4;
	
	private boolean finish;

	
	public MsgScopaScore(int scoreT1, int scoreT2, int scoreT3, int scoreT4, boolean finish) {
		super(ScopaMsgType.score, ScopaGame.SRV_NAME);
		this.scoreT1=scoreT1;
		this.scoreT2=scoreT2;
		this.scoreT3=scoreT3;
		this.scoreT4=scoreT4;
		this.finish=finish;
	}
	
	public MsgScopaScore(ScopaScore score){
		super(ScopaMsgType.score, ScopaGame.SRV_NAME);
		//TODO complete me
		//this.scoreT1=scoreT1;
		//this.scoreT2=scoreT2;
		//this.scoreT3=scoreT3;
		//this.scoreT4=scoreT4;
		this.finish= score.checkWinner();
		
	}


	public int getScoreT1() {
		return scoreT1;
	}


	public int getScoreT2() {
		return scoreT2;
	}


	public int getScoreT3() {
		return scoreT3;
	}


	public int getScoreT4() {
		return scoreT4;
	}
	
	public boolean isFinished(){
		return finish;
	}

}
