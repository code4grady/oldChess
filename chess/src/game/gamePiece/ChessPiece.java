package game.gamePiece;

import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


import game.control.Move;
import game.exception.*;
import game.util.IOUtility;

/*
 * assume: 
 * 0,0 is top left of chess board
 * 7,7 is bottom right of chess board
 */
public abstract class ChessPiece 
{
	public static final String EMPTY_SPACE="X";
	public static final String WHITE = "W";
	public static final String BLACK = "B";
	
	protected String name;

	/*
	 *  WP          white pawn
		WRQ         white rook – queen
		WRK         white rook – king
		WKT         white knight
		WB          white bishop
		WQ          white queen
		WKG         white king
		BP          black pawn
		BRQ         black rook - queen
		BRK         black rook - king
		BKT         black knight
		BB          black bishop
		BQ          black queen
		BKG         black king
	 */
	private static HashMap<String,ChessPiece> ChessPieceMap = new HashMap<String,ChessPiece>();
	static
	{
		// king rook = color+R+K (WRK for example)
		// queen rook = color+R+Q(WRQ for example)
		// populate ChessPieceMap with all pieces white an black
	}
	

	public ChessPiece(String nameval)
	{
		this.name = nameval;
	}
	
	public static ChessPiece getChessPiece(String piecename)
	{
		return ChessPieceMap.get(piecename);
	}
	// ChessPieces are statless
	// from Point move through matrix and see what moves are available
	// The name at piecematrix can determine color of
	// any found piece
	// call ChessController.getChessPiece(String matrixname) to get the ChessPiece object you run into
	public abstract void gatherAvailableMoves(String gameid, String[][] piecematrix, Point point, String directionval, String game, ArrayList<Move> movelist) 
	   throws SQLException, ChessMoveEvaluationException, ChessBoardException;

	
	protected boolean pastEdge(Point p)
	{
		if(p.x <0 || p.x>7 || p.y <0 || p.y>7)
		{
			return true;
		}
		return false;
	}
	
	
	/* return false if we hit the edge of the board or
	 * if we hit another piece
	 * if piece is an opponent store as a valid move
	*/
	protected final boolean moreMoves(String gameid, String directionval, String[][] matrixval, Point movepoint, Point originalpoint, ArrayList<Move> moves) throws ChessMoveEvaluationException
	{
		if(pastEdge(movepoint))
		{
			return false;
		}

		// should be 'this' piece
		String mypiece = matrixval[originalpoint.x][originalpoint.y];
		
		String foundpiece = matrixval[movepoint.x][movepoint.y];
		if(EMPTY_SPACE.equals(foundpiece))
		{
			String[][] matrix = IOUtility.copyMatrix(matrixval);
			// remove this piece from original position
			matrix[originalpoint.x][originalpoint.y] = EMPTY_SPACE;
			// place in new location
			matrix[movepoint.x][movepoint.y] = mypiece;
			// add to move array 
			moves.add(new Move(gameid,directionval,mypiece, new Point(movepoint.x, movepoint.y), matrix));
			return true;
		}
		// is piece differnet color
		else if(! foundpiece.startsWith(""+mypiece.substring(0,1)))
		{
			String[][] matrix = IOUtility.copyMatrix(matrixval);
			//remove this piece from original position
			matrix[originalpoint.x][originalpoint.y] = EMPTY_SPACE;
			// place in new location
			matrix[movepoint.x][movepoint.y] = mypiece;
			// add to move array 
			moves.add(new Move(gameid,directionval, mypiece, new Point(movepoint.x, movepoint.y), matrix,foundpiece));
		}
		return false;
	}
	
	
	
	protected final void getVerticalMoves(String gameid, String directionval, String[][] matrix, Point originalpoint, ArrayList<Move> moves) throws ChessMoveEvaluationException
	{
		Point newpoint = new Point(originalpoint.x,originalpoint.y);
		
		// up
		newpoint.y--;
		while(moreMoves(gameid, directionval, matrix, newpoint, originalpoint, moves))
		{
			newpoint.y--;
		}
		
		// down
		newpoint = new Point(originalpoint.x,originalpoint.y);
		newpoint.y++;
		while(moreMoves(gameid, directionval,matrix, newpoint, originalpoint, moves))
		{
			newpoint.y++;
		}
	}
	
	
	
	protected final void getHorizontalMoves(String gameid, String directionval,String[][] matrix, Point originalpoint, ArrayList<Move> moves) throws ChessMoveEvaluationException
	{
		Point newpoint = new Point(originalpoint.x,originalpoint.y);
		
		// right
		newpoint.x++;
		while(moreMoves(gameid,directionval,matrix, newpoint, originalpoint, moves))
		{
			newpoint.x++;
		}
		
		// left
		newpoint = new Point(originalpoint.x,originalpoint.y);
		newpoint.x--;
		while(moreMoves(gameid,directionval,matrix, newpoint, originalpoint, moves))
		{
			newpoint.x--;
		}
	}
	
	protected final void getDiagonalMoves(String gameid, String directionval, String[][] matrix, Point originalpoint, ArrayList<Move> moves) throws ChessMoveEvaluationException
	{
		Point newpoint = new Point(originalpoint.x,originalpoint.y);

		// right/up
		newpoint.x++;
		newpoint.y--;
		while(moreMoves(gameid,directionval,matrix, newpoint, originalpoint, moves))
		{
			newpoint.x++;
			newpoint.y--;
		}
		
		// left/up
		newpoint = new Point(originalpoint.x,originalpoint.y);
		newpoint.x--;
		newpoint.y--;
		while(moreMoves(gameid,directionval,matrix, newpoint, originalpoint, moves))
		{
			newpoint.x--;
			newpoint.y--;
		}
		
		// right/down
		newpoint = new Point(originalpoint.x,originalpoint.y);
		newpoint.x++;
		newpoint.y++;
		while(moreMoves(gameid,directionval,matrix, newpoint, originalpoint, moves))
		{
			newpoint.x++;
			newpoint.y++;
		}
		
		// left/down
		newpoint = new Point(originalpoint.x,originalpoint.y);
		newpoint.x--;
		newpoint.y++;
		while(moreMoves(gameid,directionval,matrix, newpoint, originalpoint, moves))
		{
			newpoint.x--;
			newpoint.y++;
		}
	}
	
	public String toString()
	{
		return this.name;
	}
	
}




