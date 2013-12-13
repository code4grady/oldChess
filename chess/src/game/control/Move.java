package game.control;

import java.awt.Point;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import game.exception.ChessBoardException;
import game.gamePiece.ChessPiece;
import game.util.GameConstants;

public class Move implements Serializable
{
	private String opponent;
	private String[][] matrix;
	private String movingPiece;
	private Point movepoint;
	private String gameId;
	private String direction;
	private int moveNumber;
	private int strategicValue;
	
	public Move()
	{
	}
	public Move(String gameid, String directionval, String movingpiece, Point point, String[][] matrixval)
	{
		this(gameid, directionval, movingpiece, point, matrixval, null);
	}
	public Move(String gameid, String directionval, String movingpiece, Point point,String[][] matrixval, String opponentval)
	{
		this.gameId = gameid;
		this.direction = directionval;
		this.movingPiece = movingpiece;
		this.matrix = matrixval;
		this.opponent = opponentval;
		this.movepoint = point;
	}
	
	public String getBoardDescription()
	{
		return getBoardDescription(this.matrix);
	}
	public String getColor()
	{
		String firstChar = this.movingPiece.substring(0,1);
		return (firstChar.equals(ChessPiece.WHITE))? ChessPiece.WHITE:ChessPiece.BLACK;
	}
	public String getOpponentColor()
	{
		String firstChar = this.movingPiece.substring(0,1);
		return (firstChar.equals(ChessPiece.WHITE))? ChessPiece.BLACK: ChessPiece.WHITE;
	}
	public String getMovingPiece() {
		return movingPiece;
	}
	// get non-color opponent
	public String getNeutralPiece()
	{
		String neutralPiece = ChessPiece.EMPTY_SPACE;
		if(this.movingPiece != null) neutralPiece = (movingPiece.length() ==1)? movingPiece: movingPiece.substring(1, movingPiece.length());
		return neutralPiece;
	}
	public void setMovingPiece(String movingPiece) {
		this.movingPiece = movingPiece;
	}
	
	public String[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(String[][] matrix) {
		this.matrix = matrix;
	}
	
	// get non-color opponent
	public String getNeutralOpponent()
	{
		String neutralop = ChessPiece.EMPTY_SPACE;
		if(this.opponent != null) neutralop = (opponent.length() ==1)? opponent: opponent.substring(1, opponent.length());
		return neutralop;
	}
	public String getOpponent() {
		return opponent;
	}
	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}
	public Point getMovepoint() {
		return movepoint;
	}
	public void setMovepoint(Point movepoint) {
		this.movepoint = movepoint;
	}
	public void setMovePoint(String movePoint) 
	{
		StringTokenizer tokenizer = new StringTokenizer(movePoint, ",");
		int x = Integer.parseInt(tokenizer.nextToken());
		int y = Integer.parseInt(tokenizer.nextToken());
		this.movepoint = new Point(x,y);
	}

	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	
	public int getMoveNumber() {
		return moveNumber;
	}
	public void setMoveNumber(int moveNumber) {
		this.moveNumber = moveNumber;
	}
	
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getOpponentDirection()
	{
		return (this.direction.equals(GameConstants.UP)) ? GameConstants.DOWN:GameConstants.UP;
	}
	public int getStrategicValue() {
		return strategicValue;
	}
	public void setStrategicValue(int strategyValue) {
		this.strategicValue = strategyValue;
	}
	
	
	
	public static String getBoardDescription(String[][] matrix)
	{
		StringBuffer board = new StringBuffer();
		if(matrix != null)
		{
			for(int outint = 0; outint < 8; outint++)
			{
				for(int inint=0; inint<8; inint++)
				{
					board.append(matrix[inint][outint]+",");
				}
			}
			board.deleteCharAt(board.length() -1);
		}
		return board.toString();
	}
	
	public static String[][] buildMatrix(String boarddescription) throws ChessBoardException
	{
		boarddescription = boarddescription.toUpperCase();
		String[][] matrix = new String[8][8];
		StringTokenizer tokenizer = new StringTokenizer(boarddescription,",");
		if(tokenizer.countTokens() != 64) throw new ChessBoardException("Can not parse chess board description: "+boarddescription);
		for(int outint = 0; outint < 8; outint++)
		{
			for(int inint=0; inint<8; inint++)
			{
				matrix[inint][outint] = tokenizer.nextToken();
			}
		}
		return matrix;
	}
	
	private String fillSpace(int size)
	{
		String space= "";
		int spacecount = 4-size;
		for( ; spacecount>0; spacecount--)
		{
			space += " ";
		}
		return space;
	}
	public String toString()
	{
		StringBuffer board = new StringBuffer();
		board.append(
				"strategic val = "+this.strategicValue+"\n"+
				"piece         = "+this.movingPiece+"\n"+
				"opponent      = "+this.opponent+"\n"+
				"move location = [x="+movepoint.x+",y="+movepoint.y+"]\n"+
				"direction     = "+this.direction+"\n"+
				"game          = "+this.gameId+"\n");
		for(int outint = 0; outint < 8; outint++)
		{
			for(int inint=0; inint<8; inint++)
			{
				board.append(matrix[inint][outint] + fillSpace(matrix[inint][outint].length()));
			}
			board.append("\n");
		}

		return board.toString();
	}

}
