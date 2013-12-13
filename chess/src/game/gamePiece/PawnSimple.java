package game.gamePiece;

import game.control.ChessController;
import game.control.Move;
import game.dataAccess.DataManager;
import game.dataAccess.DataManagerProxy;
import game.exception.ChessBoardException;
import game.exception.ChessMoveEvaluationException;
import game.util.GameConstants;
import game.util.IOUtility;

import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;


public class PawnSimple extends ChessPiece 
{

	public PawnSimple(String nameval)
	{
		super(nameval);
	}
	
	@Override
	public void gatherAvailableMoves(String gameid, String[][] piecematrix, Point point, String directionval, String game, ArrayList<Move> movelist)
		throws SQLException, ChessMoveEvaluationException, ChessBoardException
	{
		int dirmod = ((directionval.equalsIgnoreCase(GameConstants.UP))? -1:1);
		int ypos = point.y + dirmod;
		
		// necessary check for future move evaluations
		if(ypos < 0 || ypos>7)
		{
			return;
		}
		
		if(ChessPiece.EMPTY_SPACE.equalsIgnoreCase(piecematrix[point.x][ypos]))
		{
			String[][] matrix = IOUtility.copyMatrix(piecematrix);
			matrix[point.x][point.y]=ChessPiece.EMPTY_SPACE;
			matrix[point.x][ypos]=this.name;
			movelist.add(new Move(gameid,directionval,this.name, new Point(point.x, ypos), matrix));
		}

		// attack
		attack(gameid,directionval,point, new Point(point.x-1, ypos), piecematrix, movelist);
		attack(gameid,directionval,point, new Point(point.x+1, ypos), piecematrix, movelist);

		// direction specifc stuff
		if(directionval.equalsIgnoreCase(GameConstants.UP))
		{
			// move double
			if(	point.y == 6 && 
				ChessPiece.EMPTY_SPACE.equalsIgnoreCase(piecematrix[point.x][point.y-1]) &&
				ChessPiece.EMPTY_SPACE.equalsIgnoreCase(piecematrix[point.x][point.y-2]))
			{
				String[][] matrix = IOUtility.copyMatrix(piecematrix);
				matrix[point.x][point.y]=ChessPiece.EMPTY_SPACE;
				matrix[point.x][point.y-2]=this.name;
				movelist.add(new Move(gameid,directionval,this.name, new Point(point.x, point.y-2), matrix));
			}
			
			// en passant?
			if(point.y == 3)
			{
				checkEnPassent(-1,directionval,piecematrix, point, new Point(point.x-1,3), game, movelist);
				checkEnPassent(-1,directionval,piecematrix, point, new Point(point.x+1,3), game, movelist);
			}
		}
		
		else // going down
		{
			// move double
			if(	point.y == 1 && 
				ChessPiece.EMPTY_SPACE.equalsIgnoreCase(piecematrix[point.x][point.y+1]) &&
				ChessPiece.EMPTY_SPACE.equalsIgnoreCase(piecematrix[point.x][point.y+2]))
			{
				String[][] matrix = IOUtility.copyMatrix(piecematrix);
				matrix[point.x][point.y]=ChessPiece.EMPTY_SPACE;
				matrix[point.x][point.y+2]=this.name;
				movelist.add(new Move(gameid,directionval,this.name, new Point(point.x, point.y+2),matrix));
			}
			
			// en passant?
			if(point.y == 4)
			{
				checkEnPassent(1,directionval,piecematrix, point, new Point(point.x-1,4), game, movelist);
				checkEnPassent(1,directionval,piecematrix, point, new Point(point.x+1,4), game, movelist);
			}
		}
	}
	
	private void attack(String gameid, String directionval,Point currentpos, Point attackingpos, String[][] piecematrix, ArrayList<Move> movelist)
	{
		if(attackingpos.x >7 || attackingpos.x <0 || attackingpos.y >7 || attackingpos.y<0) return;
		
		String[][] matrix = IOUtility.copyMatrix(piecematrix);
		String opponent = matrix[attackingpos.x][attackingpos.y];
		if((! ChessPiece.EMPTY_SPACE.equalsIgnoreCase(opponent)) && (! opponent.startsWith(this.name.substring(0,1))))
		{
			matrix[currentpos.x][currentpos.y]=ChessPiece.EMPTY_SPACE;
			matrix[attackingpos.x][attackingpos.y]=this.name;
            movelist.add(new Move(gameid,directionval,this.name, new Point(attackingpos.x,attackingpos.y), matrix, opponent));
		}
	}
	
	public void checkEnPassent(int dirmod,String directionval,String[][] piecematrix, Point originalpoint, Point movepoint, String game, ArrayList<Move> movelist) throws SQLException, ChessBoardException
	{
	}
	

}
