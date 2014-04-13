package scopa.logic;

import util.PlayerName;

public class ScopaSetResult {

	PlayerName player;
	int team;

	int cards;
	int gold;
	int seven;
	int sevenOfGold;
	int scopa;
	int napoli;
	int previous;

	int points;
	int total;

	public ScopaSetResult(PlayerName player, int team, int cards, int gold, int seven, int sevenOfGold, int scopa, int napoli, int previous,
			int points, int total) {
		this.player = player;
		this.team = team;
		this.cards = cards;
		this.gold = gold;
		this.seven = seven;
		this.sevenOfGold = sevenOfGold;
		this.scopa = scopa;
		this.napoli = napoli;
		this.previous = previous;
		this.points = points;
		this.total = total;
	}

	public PlayerName getPlayer() {
		return player;
	}

	public void setPlayer(PlayerName player) {
		this.player = player;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public int getCards() {
		return cards;
	}

	public int getGold() {
		return gold;
	}

	public int getSeven() {
		return seven;
	}

	public int getSevenOfGold() {
		return sevenOfGold;
	}

	public int getScopa() {
		return scopa;
	}

	public int getNapoli() {
		return napoli;
	}

	public int getPrevious() {
		return previous;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
