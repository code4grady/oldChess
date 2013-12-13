package game.gamePiece;

import java.awt.Point;
import java.util.ArrayList;


import game.control.Move;
import game.exception.ChessMoveEvaluationException;
import game.util.IOUtility;

public class Knight extends ChessPiece 
{

	public Knight(String nameval)
	{
		super(nameval);
	}
	
	@Override
	public void gatherAvailableMoves(String gameid, String[][] piecematrix, Point point, String directionval, String game, ArrayList<Move> movelist) throws ChessMoveEvaluationException
	{
		String[][] matrix = IOUtility.copyMatrix(piecematrix);
		Point newpoint = new Point(point.x,point.y);
		
		// 1 right 2 up
		newpoint.x++;
		newpoint.y-=2;
		moreMoves(gameid,directionval,matrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// 1 right 2 down
		newpoint.x++;
		newpoint.y+=2;
		moreMoves(gameid,directionval,matrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// 1 left 2 up
		newpoint.x--;
		newpoint.y-=2;
		moreMoves(gameid,directionval,matrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// 1 left 2 down
		newpoint.x--;
		newpoint.y+=2;
		moreMoves(gameid,directionval,matrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// 2 right 1 up
		newpoint.x+=2;
		newpoint.y--;
		moreMoves(gameid,directionval,matrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// 2 right 1 down
		newpoint.x+=2;
		newpoint.y++;
		moreMoves(gameid,directionval,matrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// 2 left 1 up
		newpoint.x-=2;
		newpoint.y--;
		moreMoves(gameid,directionval,matrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// 2 left 1 down
		newpoint.x-=2;
		newpoint.y++;
		moreMoves(gameid,directionval,matrix, newpoint, point, movelist);

	}

}
