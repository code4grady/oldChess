package game.strategy;

import game.control.Move;

import java.util.Comparator;

//higher values go first
public class StrategyHighValueComparator implements Comparator<Move>  
{
	public int compare(Move move1, Move move2) 
	{
		return move2.getStrategicValue() - move1.getStrategicValue();
	}
}