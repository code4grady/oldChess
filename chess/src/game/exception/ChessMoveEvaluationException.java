package game.exception;

import java.awt.Point;

public class ChessMoveEvaluationException extends Exception 
{
	public String[][] matrix; 
	public Point point;
	
	public ChessMoveEvaluationException(String message, String[][] matrix, Point point)
	{
		super(message);
		this.matrix = matrix;
		this.point = point;
	}
}
