package game.strategy;

import game.control.Move;

import java.util.Comparator;

// lower values go first
public class StrategyLowValueComparator implements Comparator<Move>  
{
	public int compare(Move move1, Move move2) 
	{
		return move1.getStrategicValue() - move2.getStrategicValue();
	}
}