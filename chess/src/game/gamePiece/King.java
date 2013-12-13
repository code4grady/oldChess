package game.gamePiece;

import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;

import game.control.ChessController;
import game.control.Move;
import game.dataAccess.DataManager;
import game.dataAccess.DataManagerProxy;
import game.exception.ChessMoveEvaluationException;
import game.util.MoveHelper;
import game.util.GameConstants;
import game.util.IOUtility;


public class King extends KingSimple 
{
	private static MoveHelper moveHelper = new MoveHelper();
	public King(String nameval)
	{
		super(nameval);
	}
	
	@Override
	public void gatherAvailableMoves(String gameid, String[][] piecematrix, Point point, String directionval, String game, ArrayList<Move> movelist) 
	throws SQLException, ChessMoveEvaluationException
	{
		super.gatherAvailableMoves(gameid,piecematrix, point, directionval, game, movelist);
		
	 // is casteling available
		if(ChessController.inCheck(game))
		{
			return;
		}

		DataManager dm = (DataManager) DataManagerProxy.newInstance();
		int kingmoves = dm.getNumberOfMoves(game, this.name );
		// has king moved
		if(kingmoves == 0)
		{
			String color = this.name.substring(0,1);
			String king = color+"KG";
			String Krook = color+"RK";
			String Qrook = color+"RQ";
			
			int kingrookmoves = dm.getNumberOfMoves(game, Krook );
			// has kings rook moved
			if(kingrookmoves == 0)
			{		
				String[][] matrix = IOUtility.copyMatrix(piecematrix);
				// are the connecting spaces empty
				// white moving down
				if(ChessPiece.WHITE.equals(color) && GameConstants.DOWN.equalsIgnoreCase(directionval))
				{
					if(ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[1][0]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[2][0]) &&
						! isMovingThroughCheck(0, 1, 3, matrix, king, color, game, directionval) )
					{
						matrix[0][0]=ChessPiece.EMPTY_SPACE;
						matrix[3][0]=ChessPiece.EMPTY_SPACE;
						matrix[1][0]=king;
						matrix[2][0]=Krook;
						movelist.add(new Move(gameid,directionval,king, new Point(1,0), matrix, null));
					}
				}
				// white moving up
				else if(ChessPiece.WHITE.equals(color) && GameConstants.UP.equalsIgnoreCase(directionval))
				{
					if(ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[5][7]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[6][7]) &&
						! isMovingThroughCheck(7, 4, 6, matrix, king, color, game, directionval) )
					{
						matrix[7][7]=ChessPiece.EMPTY_SPACE;
						matrix[4][7]=ChessPiece.EMPTY_SPACE;
						matrix[6][7]=king;
						matrix[5][7]=Krook;
						movelist.add(new Move(gameid,directionval,king, new Point(6,7), matrix, null));
					}
				}
				// black moving down
				else if(ChessPiece.BLACK.equals(color) && GameConstants.DOWN.equalsIgnoreCase(directionval))
				{
					if(ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[5][0]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[6][0])&&
						! isMovingThroughCheck(0, 4, 6, matrix, king, color, game, directionval) )
					{
						matrix[7][0]=ChessPiece.EMPTY_SPACE;
						matrix[4][0]=ChessPiece.EMPTY_SPACE;
						matrix[6][0]=king;
						matrix[5][0]=Krook;
						movelist.add(new Move(gameid,directionval,king, new Point(6,0),matrix, null));
					}
				}
				// black moving up
				else if(ChessPiece.BLACK.equals(color) && GameConstants.UP.equalsIgnoreCase(directionval))
				{
					if(ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[1][7]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[2][7])&&
						! isMovingThroughCheck(7, 1, 3, matrix, king, color, game, directionval) )
					{
						matrix[0][7]=ChessPiece.EMPTY_SPACE;
						matrix[3][7]=ChessPiece.EMPTY_SPACE;
						matrix[1][7]=king;
						matrix[2][7]=Krook;
						movelist.add(new Move(gameid,directionval,king, new Point(1,7),matrix, null));
					}
				}
			}
			
			
			int queenrookmoves = dm.getNumberOfMoves(game, color+"RQ" );
			// has queens rook moved
			if(queenrookmoves == 0)
			{	
				String[][] matrix = IOUtility.copyMatrix(piecematrix);
				// are the connecting spaces empty
				// white moving down
				if(ChessPiece.WHITE.equalsIgnoreCase(color) && GameConstants.DOWN.equalsIgnoreCase(directionval))
				{
					if(ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[4][0]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[5][0]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[6][0])&&
						! isMovingThroughCheck(0, 3, 5, matrix, king, color, game, directionval) )
					{
						matrix[7][0]=ChessPiece.EMPTY_SPACE;
						matrix[3][0]=ChessPiece.EMPTY_SPACE;
						matrix[5][0]=king;
						matrix[4][0]=Qrook;
						movelist.add(new Move(gameid,directionval,king, new Point(4,0),matrix, null));
					}
				}
				// white moving up
                else if(ChessPiece.WHITE.equalsIgnoreCase(color) && GameConstants.UP.equalsIgnoreCase(directionval))
				{
					if(ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[1][7]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[2][7]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[3][7])&&
						! isMovingThroughCheck(7, 2, 4, matrix, king, color, game, directionval) )
					{
						matrix[0][7]=ChessPiece.EMPTY_SPACE;
						matrix[4][7]=ChessPiece.EMPTY_SPACE;
						matrix[2][7]=king;
						matrix[3][7]=Qrook;
						movelist.add(new Move(gameid,directionval,king, new Point(2,7),matrix, null));
					}
				}
				// black moving down
				else if(ChessPiece.BLACK.equalsIgnoreCase(color) && GameConstants.DOWN.equalsIgnoreCase(directionval))
				{
					if(ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[1][0]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[2][0]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[3][0])&&
						! isMovingThroughCheck(0, 2, 4, matrix, king, color, game, directionval) )
					{
						matrix[0][0]=ChessPiece.EMPTY_SPACE;
						matrix[4][0]=ChessPiece.EMPTY_SPACE;
						matrix[2][0]=king;
						matrix[3][0]=Qrook;
						movelist.add(new Move(gameid,directionval,king, new Point(2,0),matrix, null));
					}
				}
				// black moving up
				else if(ChessPiece.BLACK.equalsIgnoreCase(color) && GameConstants.UP.equalsIgnoreCase(directionval))
				{
					if(ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[4][7]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[5][7]) && ChessPiece.EMPTY_SPACE.equalsIgnoreCase(matrix[6][7])&&
						! isMovingThroughCheck(7, 5, 7, matrix, king, color, game, directionval) )
					{
						matrix[3][7]=ChessPiece.EMPTY_SPACE;
						matrix[7][7]=ChessPiece.EMPTY_SPACE;
						matrix[5][7]=king;
						matrix[4][7]=Qrook;
						movelist.add(new Move(gameid,directionval,king, new Point(4,7),matrix, null));
					}
				}
			}
		}
	}
	
	private boolean isMovingThroughCheck(int row, int start, int stop, String[][] matrix, String king, String color, String gameid, String direction) 
	{
		try
		{
			if((row != 0 && row != 7) || (start > stop))
			{
				throw new Exception("invalid casteling validation parameters: row"+row+", start"+start+", stop"+stop);
			}
			// remove king
			matrix[start][row] = ChessPiece.EMPTY_SPACE;
			while(start < stop)
			{
				++start;
				String[][] tempMatrix = IOUtility.copyMatrix(matrix);
				// put king in new, "in transit", position
				tempMatrix[start][row] = king;
				if(moveHelper.inCheck(gameid,tempMatrix, color, direction, gameid))
				{			
					return true;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	
}
