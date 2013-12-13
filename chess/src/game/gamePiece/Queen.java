package game.gamePiece;

import game.control.Move;
import game.exception.ChessMoveEvaluationException;

import java.awt.Point;
import java.util.ArrayList;

public class Queen extends ChessPiece 
{

	public Queen(String nameval)
	{
		super(nameval);
	}
	
	@Override
	public void gatherAvailableMoves(String gameid, String[][] piecematrix, Point point, String directionval, String game, ArrayList<Move> movelist) throws ChessMoveEvaluationException
	{
		getDiagonalMoves(gameid,directionval,piecematrix, point, movelist);
		getHorizontalMoves(gameid,directionval,piecematrix, point, movelist);
		getVerticalMoves(gameid,directionval,piecematrix, point, movelist);
	}

}
