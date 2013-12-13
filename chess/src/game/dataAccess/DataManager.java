package game.dataAccess;

import game.control.GameTurn;
import game.control.Move;
import game.exception.ChessBoardException;

import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DataManager 
{
	public String startNewGame(String servercolor, String serverdirection) throws SQLException;
	public void persistMove(Move gameMove) throws SQLException;
	public Move getLastMove(String gameid) throws SQLException, ChessBoardException;
	public int getNumberOfMoves(String gameid, String piece) throws SQLException;
	public MoveSummaryData getMoveSummary(Move move) throws SQLException;
	public GameData getGame(String gameid) throws SQLException;
	public void finishGame(GameTurn turn) throws SQLException, ChessBoardException;
	
	public ArrayList<Move> getAllGameMoves(String gameid) throws SQLException, ChessBoardException;
	
}
