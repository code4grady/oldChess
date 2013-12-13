package game.strategy.simple;

import java.util.ArrayList;
import java.util.HashMap;
import game.control.Move;
import game.util.MoveHelper;


public class SimpleStrategyThread implements Runnable
{
	private static MoveHelper moveHelper = new MoveHelper();
	private Move move;
	private SimpleStrategy simpleStrategy;
	private String gameId;
	
	SimpleStrategyThread(String gameid, Move val, SimpleStrategy simpstrat)
	{
		this.gameId = gameid;
		this.move = val;
		this.simpleStrategy = simpstrat;
	}
	
	public void run() 
	{
		HashMap<Integer,Integer> dimValueMap = new HashMap<Integer,Integer>();
		dimValueMap.put(1,determineMoveValue(move));
		buildDimensionValues(gameId,dimValueMap, move, 2);
		int strategicVal = getStrategicValue(dimValueMap);
		move.setStrategicValue(strategicVal);
		this.simpleStrategy.countComplete();
	}
	
	final void buildDimensionValues(String gameid, HashMap<Integer,Integer> dimvalmap, Move moveval, int dim) 
	{
		try
		{
			int dimMax = 0;
			try{dimMax = dimvalmap.get(dim);}catch(Exception e){}
			ArrayList<Move> moves = moveHelper.getMoves(gameid,moveval.getMatrix(), moveval.getOpponentColor(), moveval.getOpponentDirection(), null);
			for(Move move: moves)
			{
				int moveVal = determineMoveValue(move);
				dimMax = (moveVal > dimMax)? moveVal:dimMax;
				if(dim < SimpleStrategy.dimensions)
				{
					buildDimensionValues(gameid,dimvalmap,move,(dim +1));
				}
			}
			dimvalmap.put(dim,dimMax);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	final int determineMoveValue(Move move)
	{
		int val = SimpleStrategy.pieceRankMap.get(move.getNeutralOpponent());
		if("P".equals(move.getNeutralPiece()) && (move.getMovepoint().y == 1 || move.getMovepoint().y == 7))
		{
			val += SimpleStrategy.pieceRankMap.get("Q");
		}
		return val;
	}

	final int getStrategicValue(HashMap<Integer,Integer> dimvalmap)
	{
		int val = 0;
		for(int i=1; i<= dimvalmap.size(); i++)
		{
			int dimValue = dimvalmap.get(i); 
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

}
