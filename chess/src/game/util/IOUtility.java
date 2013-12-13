package game.util;

import game.gamePiece.ChessPiece;

import java.awt.Point;
import java.io.*;

public class IOUtility 
{
	
    public static Object serialCopy(Object o) 
    {
       Object rtn = null;
       try 
       {
           ByteArrayOutputStream bout = new ByteArrayOutputStream();
           ObjectOutputStream oout = new ObjectOutputStream(bout);
           oout.writeObject(o);
           ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
           ObjectInputStream oin = new ObjectInputStream(bin);
           rtn = oin.readObject();
       } 
       catch (IOException e) 
       {
       } 
       catch (ClassNotFoundException e) 
       {
       }
       return  rtn;
   }
    
    
   public static String[][] copyMatrix(String[][] matrix)
   {
	   String[][] copy = new String[8][8];
	   for(int outint = 0; outint < 8; outint++)
	   {
			for(int inint=0; inint<8; inint++)
			{
				copy[inint][outint] = matrix[inint][outint];
			}
		}
	   return copy;
   }
   
   
}
