package game.gamePiece;

import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;

import game.control.ChessController;
import game.control.Move;
import game.dataAccess.DataManager;
import game.dataAccess.DataManagerProxy;
import game.exception.ChessMoveEvaluationException;
import game.util.GameConstants;
import game.util.IOUtility;

public class KingSimple extends ChessPiece 
{

	public KingSimple(String nameval)
	{
		super(nameval);
	}
	
	@Override
	public void gatherAvailableMoves(String gameid, String[][] piecematrix, Point point, String directionval, String game, ArrayList<Move> movelist) 
	throws SQLException, ChessMoveEvaluationException
	{
		Point newpoint = new Point(point.x,point.y);
		
		// up
		newpoint.y--;
		moreMoves(gameid,directionval,piecematrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// down
		newpoint.y++;
		moreMoves(gameid,directionval,piecematrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// left
		newpoint.x--;
		moreMoves(gameid,directionval,piecematrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// right
		newpoint.x++;
		moreMoves(gameid,directionval,piecematrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// right/up
		newpoint.x++;
		newpoint.y--;
		moreMoves(gameid,directionval,piecematrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// left/up
		newpoint.x--;
		newpoint.y--;
		moreMoves(gameid,directionval,piecematrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// right/down
		newpoint.x++;
		newpoint.y++;
		moreMoves(gameid,directionval,piecematrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
		// left/down
		newpoint.x--;
		newpoint.y++;
		moreMoves(gameid,directionval,piecematrix, newpoint, point, movelist);
		newpoint = new Point(point.x,point.y);
		
	 
	}
}
