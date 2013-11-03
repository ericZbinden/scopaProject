package scopa.logic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import util.Logger;

public class ScopaScore {

	private boolean winner = false;
	private int winnerTeam = -1;
	private final int nPlayer;
	
	private final Map<String,Integer> teamMap;		// map PLAYER - TEAM
	private Map<Integer,Integer> teamCurrentScore;	// map TEAM - SCORE of this game
	private Map<Integer,Integer> teamGameWonScore;	// map TEAM - GAME WON
	private Map<ScopaRule, Boolean> rules;

	
	public ScopaScore(Map<String, Integer> teamMap, boolean reverse){
		this.teamMap = new HashMap<>(teamMap);
		
		this.rules = new HashMap<>(0);
		rules.put(ScopaRule.reverse, new Boolean(reverse));
			
		nPlayer = this.teamMap.size();
		if(nPlayer < 2 || nPlayer > 4)
			throw new IllegalArgumentException("Too much or too less player: "+nPlayer+". Need [2,3,4] players");
		
		teamCurrentScore = new HashMap<>(this.teamMap.size());
		teamGameWonScore = new HashMap<>(this.teamMap.size());
		for(Integer team : this.teamMap.values()){
			teamCurrentScore.put(team, 0);
			teamGameWonScore.put(team, 0);
		}
	}
	
	public boolean isRuleReverseEnable(){
		return isRuleEnable(ScopaRule.reverse);
	}
	
	private boolean isRuleEnable(ScopaRule rule){
		if(rules.containsKey(rule))
			return rules.get(rule);
		
		return false;
	}
	
	public void resetMatch(){
		for(Integer team : teamCurrentScore.keySet()){
			teamCurrentScore.put(team, 0);
		}
		winner = false;
		winnerTeam = -1;
	}
	
	public void resetMatch(boolean reverseRule){
		this.rules.put(ScopaRule.reverse, new Boolean(reverseRule));
		resetMatch();
	}
	
	public boolean markScore(Map<String,Integer> markedPoints){
		for(Entry<String,Integer> entry : markedPoints.entrySet()){
			Integer team = teamMap.get(entry.getKey());
			
			if(team != null){
				teamCurrentScore.put(team, teamCurrentScore.get(team)+entry.getValue());
			} else {
				String msg = "Player "+entry.getKey()+" is unknown. Expected player in "+Arrays.toString(teamMap.values().toArray());
				Logger.error(msg);
				throw new IllegalArgumentException(msg);
			}		
		}
		
		return checkWinner();
	}
	
	public boolean checkWinner(){
		if(winner) return true;
		
		int worstScore = Integer.MAX_VALUE;
		int firstBestScore = Integer.MIN_VALUE;
		int secondBestScore = Integer.MIN_VALUE;
		int winningTeam = -1;	
		int loosingTeam = -1;
		int teamScore = Integer.MIN_VALUE;
		int teamNumber = -1;
		
		for(Entry<Integer,Integer> entry : teamCurrentScore.entrySet()){
			teamScore = entry.getValue();
			teamNumber = entry.getKey();
			if(firstBestScore < teamScore){
				secondBestScore = firstBestScore;
				firstBestScore = teamScore;
				winningTeam = teamNumber;
			}
			if(worstScore > teamScore){
				worstScore = teamScore;
				loosingTeam = teamNumber;
			}
		}
		
		if(firstBestScore > 10 && (secondBestScore+2)<=firstBestScore){
			winner = true;
			if(this.isRuleReverseEnable()){
				winnerTeam = loosingTeam;
			} else {
				winnerTeam = winningTeam;
			}
			return true;
		}
		
		return false;
	}
	
	
	public Map<Integer,Integer> getCurrentTeamScores(){
		return new HashMap<>(teamCurrentScore);
	}
	
	public Map<Integer,Integer> getMatchTeamScores(){
		return new HashMap<>(teamGameWonScore);
	}
	
	public int getWinnerTeam(){
		return winnerTeam;
	}
	
	
}
