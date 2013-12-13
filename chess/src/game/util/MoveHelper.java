package game.util;

import game.control.Move;
import game.exception.ChessBoardException;
import game.exception.ChessMoveEvaluationException;
import game.gamePiece.Bishop;
import game.gamePiece.ChessPiece;
import game.gamePiece.King;
import game.gamePiece.KingSimple;
import game.gamePiece.Knight;
import game.gamePiece.Pawn;
import game.gamePiece.PawnSimple;
import game.gamePiece.Queen;
import game.gamePiece.Rook;

import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;


//!!!!!!!!!!!!!!!!!!!!
// we are not using static methods, as expected, as some pieces need their own to avoid infinite recursion.
// TODO


public class MoveHelper 
{
	static protected HashMap<String, ChessPiece> pieceMap;

	static
	{
		pieceMap = new HashMap<String, ChessPiece>();
		pieceMap.put("WP"  , new Pawn("WP"));
		pieceMap.put("WRQ" , new Rook("WRQ"));
		pieceMap.put("WRK" , new Rook("WRK"));
		pieceMap.put("WKT" , new Knight("WKT"));
		pieceMap.put("WB"  , new Bishop("WB"));
		pieceMap.put("WQ"  , new Queen("WQ"));
		pieceMap.put("WKG" , new King("WKG"));
		pieceMap.put("BP"  , new Pawn("BP"));
		pieceMap.put("BRQ" , new Rook("BRQ"));
		pieceMap.put("BRK" , new Rook("BRK"));
		pieceMap.put("BKT" , new Knight("BKT"));
		pieceMap.put("BB"  , new Bishop("BB"));
		pieceMap.put("BQ"  , new Queen("BQ") );
		pieceMap.put("BKG" , new King("BKG"));
	}
	
	
	/**
	 * get all possible moves for a particular chess piece
	 * @param matrix
	 * @param color
	 * @param direction
	 * @param game
	 * @return
	 * @throws SQLException
	 * @throws ChessMoveEvaluationException
	 * @throws ChessBoardException
	 */
	public ArrayList<Move> getMoves(String gameid, String[][] matrix, String color, String direction, String game) 
	throws SQLException, ChessMoveEvaluationException, ChessBoardException
	{
		ArrayList<Move> moves = new ArrayList<Move>();
		for(int outint = 0; outint < 8; outint++)
		{
			for(int inint=0; inint<8; inint++)
			{
				if(matrix[inint][outint].startsWith(color))
				{
					ChessPiece piece = pieceMap.get(matrix[inint][outint]);					
					piece.gatherAvailableMoves(gameid,matrix, new Point(inint,outint), direction, game, moves);
				}
			}
		}		
		return moves;
	}
	
	
	
	/**
	 * determine if player is in check
	 * @param matrix
	 * @param color
	 * @param direction
	 * @param game
	 * @return
	 * @throws SQLException
	 * @throws ChessMoveEvaluationException
	 * @throws ChessBoardException
	 */
	public boolean inCheck(String gameid, String[][] matrix, String color, String direction, String game)
		throws SQLException, ChessMoveEvaluationException, ChessBoardException
	{
		String opposingColor = (color.toUpperCase().equals(GameConstants.WHITE)) ? GameConstants.BLACK : GameConstants.WHITE ;
		String opposingDirection = (direction.toUpperCase().equals(GameConstants.UP)) ? GameConstants.DOWN : GameConstants.UP ;
		String inCheckKing = (color.substring(0,1)).toUpperCase()+"KG";
		ArrayList<Move> oppositionMoves = getMoves(gameid,matrix, opposingColor, opposingDirection, game);
		
		for(Move move: oppositionMoves)
		{
			if(inCheckKing.equalsIgnoreCase(move.getOpponent()))
			{		
				return true;
			}
		}
		return false;
	}
	

	
	/**
	 * remove moves that result in check
	 * @param moves
	 * @param color
	 * @param direction
	 * @param game
	 * @return
	 * @throws SQLException
	 * @throws ChessMoveEvaluationException
	 * @throws ChessBoardException
	 */
	public HashMap<String, Move> filterCheckMoves(String gameid,ArrayList<Move> moves, String color, String direction, String game) 
		throws SQLException, ChessMoveEvaluationException, ChessBoardException
	{
		HashMap<String, Move> filteredMoves = new HashMap<String, Move>();
		for(Iterator<Move> itr = moves.iterator(); itr.hasNext(); )
		{
			Move move = itr.next();
			if(inCheck(gameid,move.getMatrix(),color,direction,game))
			{
				itr.remove();
			}
		}
		for(Move move: moves)
		{		
			filteredMoves.put(move.getBoardDescription(), move);
		}	
		return filteredMoves;
	}

}
