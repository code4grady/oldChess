package game.control;


public class GameTurn 
{
	public static final String WIN = "WIN";
	public static final String LOSE = "LOSE";
	public static final String STALEMATE = "STALEMATE";
	public static final String INCHECK = "In Check";

	private String gameMessage;
	private String gameId;
	private String winningColor;
	private Move currentMove;
	private Move suggestedMove;
	
	public GameTurn(String gameid)
	{
		this.gameId = gameid;
	}
	public String getGameMessage() {
		return gameMessage;
	}
	public void setGameMessage(String val) {
		this.gameMessage = val;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String game) {
		this.gameId = game;
	}

	public String getWinningColor() {
		return winningColor;
	}
	public void setWinningColor(String winningColor) {
		this.winningColor = winningColor;
	}
	public Move getCurrentMove() {
		return currentMove;
	}

	public void setCurrentMove(Move move) {
		this.currentMove = move;
	}

	public Move getSuggestedMove() {
		return suggestedMove;
	}

	public void setSuggestedMove(Move suggestedMove) {
		this.suggestedMove = suggestedMove;
	}
	
}
