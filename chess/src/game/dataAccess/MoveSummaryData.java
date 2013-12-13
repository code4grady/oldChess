package game.dataAccess;

/*
	move			char(255) 	NOT NULL 	PRIMARY KEY,
	white_wins 		INT,
	black_wins 		INT,
	stalemates		INT,
	sum_move_number 	INT,
	sum_move_total 		INT
 */

public class MoveSummaryData extends DataObject
{
	String moveSummaryDescription;
	int wins;
	int losses;
	int stalemates;
	int sumMoveNumber;
	int sumMoveTotal;
	
	
	public String getMoveSummaryDescription() {
		return moveSummaryDescription;
	}
	public void setMoveSummaryDescription(String moveSummaryDescription) {
		this.moveSummaryDescription = moveSummaryDescription;
	}

	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getLosses() {
		return losses;
	}
	public void setLosses(int losses) {
		this.losses = losses;
	}
	public int getStalemates() {
		return stalemates;
	}
	public void setStalemates(int stalemates) {
		this.stalemates = stalemates;
	}
	public int getSumMoveNumber() {
		return sumMoveNumber;
	}
	public void setSumMoveNumber(int sumMoveNumber) {
		this.sumMoveNumber = sumMoveNumber;
	}
	public int getSumMoveTotal() {
		return sumMoveTotal;
	}
	public void setSumMoveTotal(int sumMoveTotal) {
		this.sumMoveTotal = sumMoveTotal;
	}

	
}
