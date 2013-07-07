package scopa.logic;

import java.util.ArrayList;
import java.util.List;

public class ScopaScore {

	private int nPlayer = 2;
	private boolean winner = false;
	private int winnerP = 0;
	
	private int currentScoreP1 = 0;
	private int winP1 = 0;
	
	private int currentScoreP2 = 0;
	private int winP2 = 0;
	
	private int currentScoreP3 = 0;
	private int winP3 = 0;
	
	private int currentScoreP4 = 0;
	private int winP4 = 0;
	
//	private int currentScoreP5 = 0;
//	private int winP5 = 0;
//	
//	private int currentScoreP6 = 0;
//	private int winP6 = 0;
	
	public ScopaScore(int nPlayer){
		this.nPlayer = nPlayer;
		
		if(nPlayer < 2 || nPlayer > 4)
			throw new RuntimeException("Too much or too less player: "+nPlayer+". Need [2,3,4] players");
	}
	
	public void resteMatch(){
		if(winner){
			switch(winnerP){
			case 1:
				winP1 ++;
				break;
			case 2:
				winP2++;
				break;
			case 3:
				winP3++;
				break;
			case 4:
				winP4++;
				break;
//			case 5:
//				winP5++;
//				break;		
//			case 6:
//				winP6++;
//				break;
			default: 
			}
		}		
		winner = false;
		winnerP = 0;
		
		 currentScoreP1 = 0;
		 currentScoreP2 = 0;
		 currentScoreP3 = 0;
		 currentScoreP4 = 0;
//		 currentScoreP5 = 0;
//		 currentScoreP6 = 0;
	}
	
	public boolean markScore(int p1, int p2, int p3, int p4/*, int p5, int p6*/){
		currentScoreP1 += p1;
		currentScoreP2 += p2;
		currentScoreP3 += p3;
		currentScoreP4 += p4;
//		currentScoreP5 += p5;
//		currentScoreP6 += p6;

		return checkWinner();
	}
	
//	public boolean markScore(int p1, int p2, int p3, int p4){
//		return markScore(p1,p2,p3,p4,0,0);
//	}
	
	public boolean markScore(int p1, int p2, int p3){
		return markScore(p1,p2,p3,0);
	}
	
	public boolean markScore(int p1, int p2){
		return markScore(p1,p2,0);
	}
	
	public boolean checkWinner(){
		if(winner) return true;
		
		int score1 = currentScoreP1;
		int higher1 = 1;		
		int score2 = 0;
		
		if(score1 < currentScoreP2){
			score2 = score1;
			score1 = currentScoreP2;
			higher1 = 2;
		}
		
		if(nPlayer > 2 && score1 < currentScoreP3){
			score2 = score1;
			score1 = currentScoreP3;
			higher1 = 3;
		}
		
		if(nPlayer > 3 && score1 < currentScoreP4){
			score2 = score1;
			score1 = currentScoreP4;
			higher1 = 4;
		}
		
//		if(nPlayer > 4 && score1 < currentScoreP5){
//			score2 = score1;
//			score1 = currentScoreP5;
//			higher1 = 5;
//		}
//		
//		if(nPlayer > 4 && score1 < currentScoreP6){
//			score2 = score1;
//			score1 = currentScoreP6;
//			higher1 = 6;
//		}
		
		if(score1 > 10 && (score2+2)<score1){
			winner = true;
			winnerP = higher1;
			return true;
		}
		
		return false;
	}
	
	public int getWinner(){
		if(winner) return winnerP;
		else return 0;
	}
	
	
	
	public List<Integer> getSetScores(){
		List<Integer> score = new ArrayList<Integer>();
		switch(nPlayer){
//		case 6:
//			score.add(0, currentScoreP6);
//			score.add(0, currentScoreP5);
		case 4:
			score.add(0, currentScoreP4);
		case 3:
			score.add(0, currentScoreP3);
		case 2:
			score.add(0, currentScoreP2);
			score.add(0, currentScoreP1);
		default:
		}
		
		return score;
	}
	
	public List<Integer> getMatchScores(){
		List<Integer> score = new ArrayList<Integer>();
		switch(nPlayer){
//		case 6:
//			score.add(0, winP6);
//			score.add(0, winP5);
		case 4:
			score.add(0, winP4);
		case 3:
			score.add(0, winP3);
		case 2:
			score.add(0, winP2);
			score.add(0, winP1);
		default:
		}
		
		return score;
	}
	
	public String toString(){
		return "ScopaScore Sheet:\n\tNumber of player: "+nPlayer+
				"\n\tP1\tP2\tP3\tP4"+//\tP5\tP6"+
				"\nPoints:\t"+currentScoreP1+"\t"+currentScoreP2+"\t"+currentScoreP3+"\t"+currentScoreP4+//"\t"+currentScoreP5+"\t"+currentScoreP6+
				"\nMatch:\t"+winP1+"\t"+winP2+"\t"+winP3+"\t"+winP4+"\t"//+winP5+"\t"+winP6;
				;
	}
	
	
}
