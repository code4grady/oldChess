package game.strategy.dimensionAverage;

import game.control.Move;
import game.exception.ChessBoardException;
import game.exception.ChessMoveEvaluationException;
import game.strategy.StrategyHighValueComparator;
import game.util.MoveHelper;
import game.util.GameConstants;

import java.sql.SQLException;
import java.util.*;

/*
 * Ranking moves from the most valuable opponent available to the least.
 * If no opponents are available, the ranking will be done pseudo-randomly
 * weighted toward lesser value pieces
 */
public class DimensionAverageStrategy
{
	private static MoveHelper moveHelper = new MoveHelper();
	private int dimensions = 3;
	
	private static HashMap<String, Integer> pieceRankMap;
	static
	{
		// define the starting (first dimension) values of the opposing pieces
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
		if(movelist.size() == 0) return ;
		int maxVal = 0;
		for(Move move: movelist)
		{
			HashMap<Integer,MoveValueStore> dimValueMap = new HashMap<Integer,MoveValueStore>();
			MoveValueStore first = new MoveValueStore(1);
			first.add(determineMoveValue(move));
			dimValueMap.put(1, first);
			dimValueMap.put(2, new MoveValueStore(2));
			dimValueMap.put(3, new MoveValueStore(3));
			buildDimensionValues(gameid, dimValueMap, move, 2);
			int strategicVal = getStrategicValue(dimValueMap);
			move.setStrategicValue(strategicVal);
			maxVal = (strategicVal > maxVal)? strategicVal:maxVal;
		}
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
	

	void buildDimensionValues(String gameid, HashMap<Integer,MoveValueStore> dimvalmap, Move moveval, int dim) throws SQLException, ChessMoveEvaluationException, ChessBoardException 
	{
		MoveValueStore mvs = dimvalmap.get(dim);
		ArrayList<Move> moves = moveHelper.getMoves(gameid,moveval.getMatrix(), moveval.getOpponentColor(), moveval.getOpponentDirection(), null);
		for(Move move: moves)
		{
			mvs.add(determineMoveValue(move));
			if(dim < dimensions)
			{
				buildDimensionValues(gameid,dimvalmap,move,(dim +1));
			}
		}
	}
	
	final int determineMoveValue(Move move)
	{
		int val = pieceRankMap.get(move.getNeutralOpponent());
		if("P".equals(move.getNeutralPiece()) && (move.getMovepoint().y == 1 || move.getMovepoint().y == 7))
		{
			val += pieceRankMap.get("Q");
		}
		return val;
	}
	
	int getStrategicValue(HashMap<Integer,MoveValueStore> dimvalmap)
	{
		int val = 0;
		for(int i=1; i<= dimvalmap.size(); i++)
		{
			int dimValue = dimvalmap.get(i).getAverageMoveVal(); 
			if(i == 1)
			{
				dimValue += 5;
			}
			else
			{
				dimValue = dimValue /= (i - 1);
			}
			dimValue *= ((i % 2 == 0)? -1:1);			
			val += dimValue;
		}
		return val;
	}
	
	
	
//------------------------------------------------------------------------------------------
// inner classes
	
	public class MoveValueStore
	{
		int dimension;
		int[] moveVals;
		
		public MoveValueStore(int dims)
		{
			this.dimension = dims;
			this.moveVals = new int[dims];
			for(int i=0; i< this.moveVals.length; i++)
			{
				this.moveVals[i]=0;
			}
		}
		
		public void add(int val)
		{
			for(int i=0; i< this.moveVals.length; i++)
			{
				if(val > this.moveVals[i])
				{
					push(val, i);
					break;
				}
			}
		}
		
		private void push(int val, int location)
		{
			if(val == 0) return;
			int hold = this.moveVals[location];
			this.moveVals[location] = val;
			if((location +1) < this.moveVals.length)
			{
				push(hold, location +1);
			}
		}

		public int getDimension() 
		{
			return dimension;
		}

		public int getAverageMoveVal() 
		{
			int average = 1;
			int size = this.moveVals.length;
			for(int i=0; i< this.moveVals.length; i++)
			{
				int hold = this.moveVals[i];
				if(hold > 0)
				{
					average += hold;
				}
				else
				{
					size = i+1;
					break;
				}
			}
			average /= size;
			
			return average;
		}
	}

}
