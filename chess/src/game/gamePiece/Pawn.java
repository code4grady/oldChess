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


public class Pawn extends PawnSimple 
{

	public Pawn(String nameval)
	{
		super(nameval);
	}
	
	
	public void checkEnPassent(int dirmod,String directionval,String[][] piecematrix, Point originalpoint, Point movepoint, String gameid, ArrayList<Move> movelist) throws SQLException, ChessBoardException
	{
		if(movepoint.x > 7 || movepoint.x <0 || movepoint.y >7 || movepoint.y <0) return;
		String[][] matrix = IOUtility.copyMatrix(piecematrix);
		
		String piecename = matrix[movepoint.x][movepoint.y];
		if((! piecename.startsWith(this.name.substring(0,1)) 	&&
			 piecename.length() == 2							&&
			 piecename.endsWith("P")))
		{
			DataManager dm = (DataManager) DataManagerProxy.newInstance();
			if(dm.getNumberOfMoves(gameid, piecename) == 1)
			{
				Move lastMove = dm.getLastMove(gameid);	
				if(lastMove.getMovepoint().equals(movepoint))
				{
					// move piece
					matrix[originalpoint.x][originalpoint.y]=ChessPiece.EMPTY_SPACE;
					matrix[movepoint.x][(movepoint.y+dirmod)]=this.name;
					movelist.add(new Move(gameid,directionval,this.name, new Point(movepoint.x,movepoint.y), matrix,piecename));
				}
			}
			
		}
	
	}
	

}
