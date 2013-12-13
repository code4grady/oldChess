package game.strategy.historical;

import game.control.Move;
import game.dataAccess.*;
import game.exception.MoveProcessingException;
import game.strategy.StrategyHighValueComparator;
import game.strategy.StrategyLowValueComparator;
import game.util.GameConstants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 * look how the available moves have worked historically (from db). Rank from best to worst based on 
 * what is found. If no matching moves are found, nothing is returned (empty LinkedHashMap).
 */
public class HistoricalStrategy 
{
	public static final String GOODMOVES = "GOODMOVES";
	public static final String BADMOVES = "BADMOVES";
	public static final int GOODTHRESHOLD = 2;
	public static final int BADTHRESHOLD = -2;

	private static HashMap<String, String> colorMap = new HashMap<String,String>();
	
	public HashMap<String, ArrayList<Move>> rankMoves(ArrayList<Move> movelist) throws SQLException, MoveProcessingException 
	{
		HashMap<String, ArrayList<Move>> moveMap = new HashMap<String, ArrayList<Move>>();
		if(movelist.size() == 0) return moveMap;
		
		ArrayList<Move> goodMoves = new ArrayList<Move>();
		ArrayList<Move> badMoves = new ArrayList<Move>();

		DataManager dm = (DataManager) DataManagerProxy.newInstance();
		for(Move move: movelist)
		{
			MoveSummaryData msd = dm.getMoveSummary(move);
			if(msd != null)
			{
				int val = determineMoveValue(msd);
				move.setStrategicValue(val);
				if(val > GOODTHRESHOLD)
				{
					goodMoves.add(move);
				}
				else if(val < BADTHRESHOLD)
				{
					badMoves.add(move);
				}
			}
		}
		Collections.sort(goodMoves, new StrategyHighValueComparator());
		Collections.sort(badMoves, new StrategyLowValueComparator());
		
		moveMap.put(GOODMOVES, goodMoves);
		moveMap.put(BADMOVES,badMoves);
		return moveMap;
	}
	
	private int determineMoveValue(MoveSummaryData msd) throws SQLException, MoveProcessingException
	{
		int moveValue = msd.getWins() - msd.getLosses();
		return moveValue;
	}


}
