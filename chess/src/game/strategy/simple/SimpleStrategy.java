package game.strategy.simple;

import game.control.Move;
import game.exception.ChessBoardException;
import game.exception.ChessMoveEvaluationException;
import game.strategy.StrategyHighValueComparator;
import game.util.GameConstants;

import java.sql.SQLException;
import java.util.*;

/*
 * Ranking moves from the most valuable opponent available to the least.
 * If no opponents are available, the ranking will be done pseudo-randomly,
 * weighted toward lesser value pieces
 */
public class SimpleStrategy
{
	
	public static final int dimensions = 3;
	private int completeThreadCount = 0;
	
	static HashMap<String, Integer> pieceRankMap;
	static
	{
		pieceRankMap = new HashMap<String, Integer>();
		pieceRankMap.put(GameConstants.EMPTY_SPACE  , new Integer(0));
		pieceRankMap.put("P"  , new Integer(3));
		pieceRankMap.put("RQ" , new Integer(10));
		pieceRankMap.put("RK" , new Integer(10));
		pieceRankMap.put("KT" , new Integer(20));
		pieceRankMap.put("B"  , new Integer(30));
		pieceRankMap.put("Q"  , new Integer(50));
		pieceRankMap.put("KG" , new Integer(60));
	}
	

	
	public void rankMoves(ArrayList<Move> movelist, String gameid) throws SQLException, ChessMoveEvaluationException, ChessBoardException 
	{
		if(movelist.size() == 0) return;		
		for(Move move: movelist)
		{
			
			new SimpleStrategyThread(gameid,move,this).run();
		}
		
		
//		for(Move move: movelist)
//		{
//			Thread thread = new Thread(new SimpleStrategyThread(move,this));
//			thread.start();
//		}
		// wait for threads to return
//		while(this.completeThreadCount < movelist.size())
//		{
//			try 
//			{
//				Thread.sleep(500);
//			} catch (InterruptedException e) 
//			{
//				e.printStackTrace();
//			}
//		}
		
		Collections.sort(movelist, new StrategyHighValueComparator());
		int maxMoveValue =  movelist.get(0).getStrategicValue();
		for(Iterator<Move> itr = movelist.iterator(); itr.hasNext(); )
		{
			Move move = itr.next();
			if(move.getStrategicValue() < maxMoveValue)
			{
				itr.remove();
			}
		}
		if(movelist.size() > 1) Collections.shuffle(movelist);

	}
	
	
	public final synchronized void countComplete()
	{
		++this.completeThreadCount;
	}

	
}

