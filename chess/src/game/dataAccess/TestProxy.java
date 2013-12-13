package game.dataAccess;

import game.control.ChessController;
import game.control.Move;

import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class TestProxy 
{
	
	public static void main(String[] args)
	{
System.out.println(" staring");
		try
		{
			DataManager dm = (DataManager) DataManagerProxy.newInstance();
			ArrayList<Move> moves = dm.getAllGameMoves("1193258785041");
			for(Move move: moves)
			{
				System.out.println(move.toString());
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
System.out.println(" done");

		
	}
		
		

	
}
