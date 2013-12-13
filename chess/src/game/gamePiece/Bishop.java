package game.gamePiece;

import java.awt.Point;
import java.util.ArrayList;

import game.control.Move;
import game.exception.ChessMoveEvaluationException;

public class Bishop extends ChessPiece 
{

	public Bishop(String nameval)
	{
		super(nameval);
	}
	
	@Override
	public void gatherAvailableMoves(String gameid, String[][] piecematrix, Point point, String directionval, String game, ArrayList<Move> movelist) throws ChessMoveEvaluationException
	{
		getDiagonalMoves(gameid,directionval,piecematrix, point, movelist);
	}

}
