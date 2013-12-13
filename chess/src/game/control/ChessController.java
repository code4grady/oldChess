package game.control;

import game.dataAccess.DataManager;
import game.dataAccess.DataManagerProxy;
import game.dataAccess.GameData;
import game.exception.ChessBoardException;
import game.exception.ChessMoveEvaluationException;
import game.exception.MoveProcessingException;
import game.gamePiece.ChessPiece;
import game.strategy.MoveProcessor;
import game.util.MoveHelper;
import game.util.GameConstants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class ChessController 
{
	private static MoveHelper moveHelper = new MoveHelper();
	private static HashMap<String, String> checkMap = new HashMap<String, String>();

	/**
	 * Receive move (description of chess board). Verify this is a legal move.
	 * Find counter move. Determine any game result (win/loss/stalemate). Return counter move
	 * @param boardinput
	 * @param gameid
	 * @return
	 * @throws SQLException 
	 * @throws ChessBoardException 
	 * @throws ChessMoveEvaluationException 
	 * @throws MoveProcessingException 
	 */
	public synchronized GameTurn makeChessMove(String boardinput, String gameid) throws SQLException, ChessBoardException, ChessMoveEvaluationException, MoveProcessingException 
	{
		boardinput = boardinput.toUpperCase();
		GameTurn turn = new GameTurn(gameid);

		ArrayList<Move> availableMoves = new ArrayList<Move>();
		String[][] oldBoard = null;

		DataManager dm = (DataManager) DataManagerProxy.newInstance();
		
		Move lastMove = dm.getLastMove(gameid);
		if(lastMove == null)
		{
			GameData gd = dm.getGame(gameid); 
			if(gd != null)// no last move means starting a new game
			{
				oldBoard = getOpeningBoard(gd.getColor(), gd.getDirection());
				// not really a move, just holds color for now
				lastMove = new Move(gameid,gd.getDirection(),gd.getColor(), null, null);
			}
			else
			{
				throw new ChessBoardException(""+gameid+" is NOT a valid gameID. ");
			}
		}
		else 
		{
			oldBoard = lastMove.getMatrix();
		}
		// set color and direction for this turn
		String color = lastMove.getOpponentColor();
		String direction = lastMove.getOpponentDirection();

		// determine available moves
		availableMoves = moveHelper.getMoves(gameid,oldBoard, color, direction, gameid);	
		HashMap<String, Move> filteredMoves = moveHelper.filterCheckMoves(gameid,availableMoves, color, direction, gameid);	
		// does the requested move exist in available moves
		Move acceptedMove = filteredMoves.get(boardinput);			
		if(acceptedMove == null)
		{
			throw new ChessBoardException(" The requested move, is not legal");
		}
		// store move
		acceptedMove.setGameId(gameid);
		postMoveProcess(acceptedMove);
		turn.setCurrentMove(acceptedMove);
		dm.persistMove(acceptedMove);
	
		// OUR TURN
		String ourColor = acceptedMove.getOpponentColor();
		String ourDirection = acceptedMove.getOpponentDirection();

		// are we in check
		boolean incheck = moveHelper.inCheck(gameid,acceptedMove.getMatrix(), ourColor, ourDirection, gameid);
		if(incheck)
		{
			checkMap.put(gameid,gameid);
		}
		availableMoves = moveHelper.getMoves(gameid,acceptedMove.getMatrix(), ourColor, ourDirection, gameid);
		moveHelper.filterCheckMoves(gameid,availableMoves, ourColor, ourDirection, gameid);
		if(availableMoves.size() == 0)
		{			
			if(incheck)
			{
				turn.setGameMessage(color+" "+GameTurn.WIN);
			}
			else
			{
				turn.setGameMessage(GameTurn.STALEMATE);
			}
			turn.setWinningColor(color);
			finishGame(turn);
			return turn;
		}
		// pick and store response move
		Move ourMove = MoveProcessor.processMoves(ourColor,availableMoves, gameid);
		postMoveProcess(ourMove);
		turn.setCurrentMove(ourMove);
		dm.persistMove(ourMove);
		checkMap.remove(gameid);
		
		// have we won or caused stalemate
		incheck = moveHelper.inCheck(gameid,ourMove.getMatrix(), color, direction, gameid);
		if(incheck)
		{
			turn.setGameMessage(color+" "+GameTurn.INCHECK);
		}
		availableMoves = moveHelper.getMoves(gameid,ourMove.getMatrix(), color, direction, gameid);		
		moveHelper.filterCheckMoves(gameid,availableMoves, color, direction, gameid);
		if(availableMoves.size() == 0)
		{
			if(incheck)
			{
				turn.setGameMessage(ourColor+" "+GameTurn.WIN);
			}
			else
			{
				turn.setGameMessage(GameTurn.STALEMATE);
			}
			turn.setWinningColor(ourColor);
			finishGame(turn);
			return turn;
		}
		
		// suggest move for computer "self" play
		turn.setSuggestedMove(MoveProcessor.processMoves(color,availableMoves, gameid));

		availableMoves = null;
		filteredMoves = null;

		return turn ;
	}

	/**
	 * 
	 * @param servercolor
	 * @param serverdirection
	 * @return
	 * @throws SQLException
	 */
	public synchronized String setupNewGame(String servercolor, String serverdirection) throws SQLException
	{
		DataManager dm = (DataManager) DataManagerProxy.newInstance();
		String gamepk = dm.startNewGame(servercolor, serverdirection);
		return gamepk;
	}


	/**
	 * called internally if game ends. can be called externally for stalemate (too many moves)
	 * or to surrender
	 * @param turn
	 * @throws SQLException
	 * @throws ChessBoardException
	 */
	public synchronized void finishGame(GameTurn turn) throws SQLException, ChessBoardException
	{		
		turn.setSuggestedMove(null);
		((DataManager) DataManagerProxy.newInstance()).finishGame(turn);
	}
	
	public static boolean inCheck(String gameid)
	{
		return (checkMap.get(gameid)) == null ? false:true;
	}

	
	// any pawn promotion?
	public void postMoveProcess(Move move)
	{
		String pawnName = move.getColor()+"P";
		for(int i=0; i<8; i++)
		{
			if(GameConstants.UP.equals(move.getDirection()) && pawnName.equalsIgnoreCase(move.getMatrix()[i][0]))
			{
				move.getMatrix()[i][0]=move.getColor()+"Q";
				break;
			}
			else if(GameConstants.DOWN.equals(move.getDirection()) && pawnName.equalsIgnoreCase(move.getMatrix()[i][7]))
			{
				move.getMatrix()[i][7]=move.getColor()+"Q";
				break;
			}
		}
	}
	
	public static String[][] getOpeningBoard(String color, String direction)
	{
		// black/down or white/up 
		String[][] bd = new String[8][8];
		bd[0][0]="BRQ";bd[1][0]="BKT";bd[2][0]="BB";bd[3][0]="BQ";bd[4][0]="BKG";bd[5][0]="BB";bd[6][0]="BKT";bd[7][0]="BRK";
		bd[0][1]="BP"; bd[1][1]="BP"; bd[2][1]="BP";bd[3][1]="BP";bd[4][1]="BP"; bd[5][1]="BP";bd[6][1]="BP"; bd[7][1]="BP";
		bd[0][2]="X";  bd[1][2]="X";  bd[2][2]="X" ;bd[3][2]="X"; bd[4][2]="X";  bd[5][2]="X"; bd[6][2]="X";  bd[7][2]="X";
		bd[0][3]="X";  bd[1][3]="X";  bd[2][3]="X"; bd[3][3]="X"; bd[4][3]="X";  bd[5][3]="X"; bd[6][3]="X";  bd[7][3]="X";
		bd[0][4]="X";  bd[1][4]="X";  bd[2][4]="X"; bd[3][4]="X"; bd[4][4]="X";  bd[5][4]="X"; bd[6][4]="X";  bd[7][4]="X";
		bd[0][5]="X";  bd[1][5]="X";  bd[2][5]="X"; bd[3][5]="X"; bd[4][5]="X";  bd[5][5]="X"; bd[6][5]="X";  bd[7][5]="X";
		bd[0][6]="WP"; bd[1][6]="WP"; bd[2][6]="WP";bd[3][6]="WP";bd[4][6]="WP"; bd[5][6]="WP";bd[6][6]="WP"; bd[7][6]="WP";
		bd[0][7]="WRQ";bd[1][7]="WKT";bd[2][7]="WB";bd[3][7]="WQ";bd[4][7]="WKG";bd[5][7]="WB";bd[6][7]="WKT";bd[7][7]="WRK";
		
		// black up or white down
		if((ChessPiece.WHITE.equalsIgnoreCase(color) && GameConstants.DOWN.equalsIgnoreCase(direction)) ||
				ChessPiece.BLACK.equalsIgnoreCase(color) && GameConstants.UP.equalsIgnoreCase(direction))
		{
			bd[0][0]="WRK";bd[1][0]="WKT";bd[2][0]="WB";bd[3][0]="WKG";bd[4][0]="WQ";bd[5][0]="WB";bd[6][0]="WKT";bd[7][0]="WRQ";
			bd[0][1]="WP"; bd[1][1]="WP"; bd[2][1]="WP";bd[3][1]="WP";bd[4][1]="WP"; bd[5][1]="WP";bd[6][1]="WP"; bd[7][1]="WP";
			
			bd[0][6]="BP"; bd[1][6]="BP"; bd[2][6]="BP";bd[3][6]="BP";bd[4][6]="BP"; bd[5][6]="BP";bd[6][6]="BP"; bd[7][6]="BP";
			bd[0][7]="BRK";bd[1][7]="BKT";bd[2][7]="BB";bd[3][7]="BKG";bd[4][7]="BQ";bd[5][7]="BB";bd[6][7]="BKT";bd[7][7]="BRQ";
		}
		
		return bd;
	}

}





