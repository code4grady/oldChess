package game.strategy;

import game.control.Move;
import game.dataAccess.DataManager;
import game.dataAccess.DataManagerProxy;
import game.exception.ChessBoardException;
import game.exception.ChessMoveEvaluationException;
import game.exception.MoveProcessingException;
import game.strategy.dimensionAverage.DimensionAverageStrategy;
import game.strategy.historical.HistoricalStrategy;
import game.strategy.simple.SimpleStrategy;
import game.util.GameConstants;
import game.util.IOUtility;

import java.sql.SQLException;
import java.util.*;


// run available moves through various strategy objects to find the best - or at least a good - move
public class MoveProcessor 
{
	private static int historicalThreashold = 20;
	private static DimensionAverageStrategy DAS = new DimensionAverageStrategy();
	private static SimpleStrategy SS = new SimpleStrategy();
	
	public static Move processMoves(String color, ArrayList<Move> movelist, String gameid) throws SQLException, MoveProcessingException, ChessMoveEvaluationException, ChessBoardException
	{
		String time = ""+GregorianCalendar.getInstance().getTimeInMillis();
		int oneDigitRandom = Integer.parseInt(time.substring(time.length() -1));	

		if(GameConstants.BLACK.equals(color))
		{
			DAS.rankMoves(movelist, gameid);
			Move move = movelist.get(0);
			return move;
		
			
//			// most of the time consider historical values.
//			if(oneDigitRandom > 2)
//			{
//
//				HashMap<String, ArrayList<Move>> historicalMoveMap = new HistoricalStrategy().rankMoves(((ArrayList<Move>) IOUtility.serialCopy(movelist)));
//				ArrayList<Move> goodMoves = historicalMoveMap.get(HistoricalStrategy.GOODMOVES);
//				// if there is a good, historical, move make it.
//				if(goodMoves.size() > 0)
//				{
//					Move tempMove = goodMoves.get(0);
//					if(tempMove != null && (tempMove.getStrategicValue() > historicalThreashold))
//					{
////System.out.println(" found historical move, value of "+tempMove.getStrategicValue())	;			
//						return tempMove;
//					}
//				}
//				
//				
//				// No moves historicaly good enough. Remove any bad ones and use a basic strategy 
//				// to determine next move
//				HashMap<String,Move> originalMoves = new HashMap<String,Move>();
//				for(Move move: movelist)
//				{
//					originalMoves.put(move.getBoardDescription(),move);
//				}
//				ArrayList<Move> badMoves = historicalMoveMap.get(HistoricalStrategy.BADMOVES);
//				for(Move move: badMoves)
//				{
//					if((move.getStrategicValue()  < -(historicalThreashold)) && originalMoves.size() > 1)
//					{
////System.out.println(" removing bad move of "+move.getStrategicValue());
//						originalMoves.remove(move.getBoardDescription());
//					}
//				}
//				movelist = new ArrayList<Move>();
//				for(Iterator<String> iterator = originalMoves.keySet().iterator(); iterator.hasNext();)
//				{
//					movelist.add(originalMoves.get(iterator.next()));
//				}
//			}
//			
//			if(oneDigitRandom< 5)
//			{		
//				new SimpleStrategy().rankMoves(movelist, gameid);
//			}
//			else
//			{	
//				new DimensionAverageStrategy().rankMoves(movelist, gameid);
//			}
//			Move move = movelist.get(0);
//			return move;
		}
		else
		{
			SS.rankMoves(movelist, gameid);
			Move move = movelist.get(0);
			return move;
		}
	}
	
}
