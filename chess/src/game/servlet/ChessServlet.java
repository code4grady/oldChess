package game.servlet;

import game.control.ChessController;
import game.control.GameTurn;
import game.control.Move;
import game.dataAccess.DataManager;
import game.dataAccess.DataManagerProxy;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.*;
import javax.naming.*;

import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class ChessServlet extends HttpServlet
{
	static HashMap<String, GameTurn> turnMap = new HashMap<String, GameTurn>();
	ChessController chessController = new ChessController();
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		try
		{
			String gameId = "unknown";
			String move = "unknown";
			String responseMove = "unknown";
			
			String moveRequest = request.getParameter("GAMESTATE");
			moveRequest = moveRequest.trim();
		
			if("STARTGAME".equalsIgnoreCase(moveRequest))
			{
				gameId = chessController.setupNewGame("B", "DOWN");				
				move = Move.getBoardDescription(ChessController.getOpeningBoard("B", "DOWN"));
				responseMove = move+"|"+gameId;
			}
			else if(responseMove.indexOf("SELFPLAY") > -1)
			{
				try
				{
					int lastPipeLocation = moveRequest.lastIndexOf('|');
					gameId = moveRequest.substring(lastPipeLocation+1,lastPipeLocation+14);
				}
				catch(Exception ie){}
				if("unknown".equals(gameId))
				{
					gameId = chessController.setupNewGame("W", "UP");				
					move = Move.getBoardDescription(ChessController.getOpeningBoard("W", "UP"));
					
				}
				else
				{
					GameTurn lastTurn = turnMap.get(gameId);
					move = lastTurn.getSuggestedMove().getBoardDescription();
				}
				GameTurn turn = chessController.makeChessMove(move, gameId);
				turnMap.put(gameId, turn);
				responseMove = ""+turn.getCurrentMove().getBoardDescription()+"|"+gameId;
			}
			// normal play
			else
			{
				turnMap.remove(gameId);
				int lastPipeLocation = moveRequest.lastIndexOf('|');
				gameId = moveRequest.substring(lastPipeLocation+1,lastPipeLocation+14);
				move = moveRequest.substring(0,lastPipeLocation);	
				responseMove = move+"|"+gameId;
		    	try
		    	{
		    		GameTurn turn = chessController.makeChessMove(move, gameId);
		    		responseMove = ""+turn.getCurrentMove().getBoardDescription()+"|"+gameId;
		    		if(turn.getGameMessage() != null)
		    		{
		    			responseMove += "|"+turn.getGameMessage();
		    		}
		    	}
		    	catch(Throwable t)
		    	{
		    		t.printStackTrace();	
		    		DataManager dm = (DataManager) DataManagerProxy.newInstance();
		    		Move lastMove = dm.getLastMove(gameId);
		    		if(lastMove != null)
		    		{
		    			responseMove = lastMove.getBoardDescription()+"|"+gameId+"|exception"+t.getMessage();
		    		}
		    		else
		    		{
		    			responseMove = Move.getBoardDescription(ChessController.getOpeningBoard("B", "DOWN"))+"|"+gameId+"|exception"+t.getMessage();
		    		}
		    	}
			}

	    	response.setContentType("text/html");
	    	PrintWriter out = response.getWriter();
			out.println(responseMove);
	    	return;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return;
    }
}