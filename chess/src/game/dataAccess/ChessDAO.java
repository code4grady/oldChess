package game.dataAccess;

import game.control.ChessController;
import game.control.GameTurn;
import game.control.Move;
import game.exception.ChessBoardException;
import game.gamePiece.ChessPiece;
import game.util.GameConstants;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/*
 * DO NOT ACCESS THIS CLASS DIRECTLY
 * access through DataManagerProxy.
 * 
 * The current thought is to reuse the connection for the duration of the game - 
 * I am only closing the connection at finish game. The proxy will validate the connection before
 * attempting any Connection related tasks
 */
public class ChessDAO implements DataManager
{
	private Connection con;
	private PreparedStatement persitMovePST = null;
	private PreparedStatement lastMovePST = null;
	// reduce visibility - USE DataManagerProxy
	ChessDAO()
	{
	}

	public void validateConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		try 
		{
			if(con == null || con.isClosed())
			{
				createConnection();
			}
			else// make certain we have db connectivity
			{
		         Statement sttemp = con.createStatement();
		         sttemp.executeQuery("select * from TEST_ME");
		         sttemp.close();
			}
		} 
		catch (SQLException se) 
		{
				createConnection();
		}
	}
	private void createConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		// the .war file doesnt like this methond of connection creation
//		System.setProperty("jdbc.drivers", "com.mysql.jdbc.Driver");
//		con = DriverManager.getConnection("jdbc:mysql://localhost/chess","root","admin");

		DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
        con = DriverManager.getConnection("jdbc:mysql://localhost/chess","root","admin");
        
        // create PreparedStatements			
		persitMovePST = con.prepareStatement("insert into GAME_MOVE (move_desc,game_id,piece_name,piece_direction,opponent_piece,Move_number,move_point) values(?,?,?,?,?,?,?)");
		String sql =
			"SELECT * "+
			"FROM  GAME_MOVE "+
			"WHERE game_id like ? and move_number= ?";
		lastMovePST = con.prepareStatement(sql);
	}
	void closeConnection()
	{
		try 
		{
			con.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * 	game_id	 		VARCHAR(50) 	NOT NULL 	PRIMARY KEY,
		color 			VARCHAR(5),
		direction		VARCHAR(5),
		time 			TIMESTAMP,
		result 			VARCHAR(10)
	 */
	public String startNewGame(String servercolor, String serverdirection) throws SQLException
	{
		//cleanupOldGames();
		cleanupUnFinishedGames();
		
		String gamepk = ""+GregorianCalendar.getInstance().getTimeInMillis();
        String sql = "insert into GAME (game_id,color,direction,time,result) values(?,?,?,?,?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString	 (1, gamepk);
        pstmt.setString	 (2, servercolor);
        pstmt.setString	 (3, serverdirection);
        pstmt.setTimestamp(4, new java.sql.Timestamp(GregorianCalendar.getInstance().getTimeInMillis())); 
        pstmt.setString	 (5, null);
        pstmt.executeUpdate();
        pstmt.close();
        
		return gamepk;
	}

	/*
	 * table GAME_MOVE
		move_desc		char(255) 	NOT NULL,
		game_id	 		VARCHAR(50) NOT NULL,
		piece_name 		VARCHAR(15),
		move_number 	INT,
		move_point		VARCHAR(5),
	 */
	public void persistMove(Move move) throws SQLException
	{
        int movenumber = (getLastMoveNumber(move.getGameId()))+1;
        persitMovePST.setString(1, move.getBoardDescription());
        persitMovePST.setString(2, move.getGameId());
        persitMovePST.setString(3, move.getMovingPiece());
        persitMovePST.setString(4, move.getDirection());
        persitMovePST.setString(5, move.getOpponent());
        persitMovePST.setInt   (6, movenumber); 
        persitMovePST.setString(7, ""+move.getMovepoint().x+","+move.getMovepoint().y);
        persitMovePST.executeUpdate();
        
        if("KG".equalsIgnoreCase(move.getNeutralOpponent()))
        {
        	System.out.println("\n\n KILLED THE KING"+move);
        	throw new SQLException(" the king is dead");
        }
	}
	public Move getLastMove(String gameid) throws SQLException, ChessBoardException
	{
//		Statement stmt = con.createStatement();
//		String sql =
//			"SELECT * "+
//			"FROM  GAME_MOVE "+
//			"WHERE game_id like'"+gameid+"' and move_number= "+lastmovenumber;
		
		int lastmovenumber = getLastMoveNumber(gameid);
		lastMovePST.setString(1,gameid);
		lastMovePST.setInt(2, lastmovenumber);
		Move move = null;

        ResultSet rst = lastMovePST.executeQuery();
        while(rst.next())
        {
        	move = new Move();
        	move.setMatrix(Move.buildMatrix(rst.getString("move_desc")));
        	move.setGameId(rst.getString("game_id"));
       	 	move.setMovingPiece(rst.getString("piece_name"));
       	 	move.setDirection(rst.getString("piece_direction"));
       	 	move.setOpponent(rst.getString("opponent_piece"));
	       	move.setMoveNumber(rst.getInt("move_number"));
	       	move.setMovePoint(rst.getString("move_point"));
        }
		return move;
	}

	public int getNumberOfMoves(String gameid, String piece) throws SQLException
	{
		int movecount = 0;
		Statement stmt = con.createStatement();
		String sql =
			"SELECT COUNT(*) "+
			"FROM  GAME_MOVE "+
			"WHERE game_id like'"+gameid+"' and piece_name like '"+piece+"'";
		
		ResultSet rst = stmt.executeQuery(sql);
		while(rst.next())
        {
       	 	movecount = rst.getInt(1);
        }
        stmt.close();
		
		return movecount;
	}
	
	
/*
 * 	move			char(255) 	NOT NULL 	PRIMARY KEY,
	wins 		INT,
	losses 		INT,
	stalemates		INT,
	sum_move_number 	INT,
	sum_move_total 		INT
 */
	public MoveSummaryData getMoveSummary(Move move) throws SQLException
	{
		String cleanMoveDescription = cleanMoveDescription(move);
		return getMoveSummary(cleanMoveDescription);
	}
	MoveSummaryData getMoveSummary(String cleanMoveDescription) throws SQLException
	{
		MoveSummaryData moveSummary = null;
		Statement stmt = con.createStatement();
		String sql =
			"SELECT * "+
			"FROM  MOVE_SUMMARY "+
			"WHERE move like'"+cleanMoveDescription+"'";
	
        ResultSet rst = stmt.executeQuery(sql);
        while(rst.next())
        {
        	moveSummary = new MoveSummaryData();
        	moveSummary.setMoveSummaryDescription(cleanMoveDescription);
        	moveSummary.setWins(rst.getInt("wins"));
        	moveSummary.setLosses(rst.getInt("losses"));
        	moveSummary.setStalemates(rst.getInt("stalemates"));
        	moveSummary.setSumMoveNumber(rst.getInt("sum_move_number"));
        	moveSummary.setSumMoveTotal(rst.getInt("sum_move_total"));
        }
        stmt.close();

		return moveSummary;
	}
	
	/*
	 * 	table GAME
		game_id	 		VARCHAR(50) 	NOT NULL 	PRIMARY KEY,
		color 			VARCHAR(5),
		direction		VARCHAR(5),
		time 			TIMESTAMP,
		result 			VARCHAR(10)(non-Javadoc)
	 * 
	 */
	public GameData getGame(String gameid) throws SQLException
	{
		GameData game = null;
		Statement stmt = con.createStatement();
		String sql =
			"SELECT * "+
			"FROM  GAME "+
			"WHERE game_id like'"+gameid+"'";
		
        ResultSet rst = stmt.executeQuery(sql);
        while(rst.next())
        {
        	game = new GameData();
        	game.setGameId(rst.getString("game_id"));
        	game.setColor(rst.getString("color"));
        	game.setDirection(rst.getString("direction"));
        	game.setTime(rst.getTimestamp("time"));
        	game.setResult(rst.getString("result"));
        }
     
        stmt.close();
		return game;
	}
	
	
	public void finishGame(GameTurn turn) throws SQLException, ChessBoardException
	{
		GameData gameData = getGame(turn.getGameId());
		String gameResult = turn.getGameMessage();
		String winningColor = turn.getWinningColor();
		
		if(turn.getGameMessage().indexOf(GameTurn.STALEMATE)>-1)
		{
			winningColor = GameTurn.STALEMATE;
		}
		else if(! turn.getWinningColor().equals(gameData.getColor()))
		{
			gameResult = GameTurn.LOSE;
		}
		updateGameResult(turn.getGameId(), gameResult);
		sumarizeMoves(turn.getGameId(), winningColor, gameData);

		cleanupDBResources();
	}

	
	private void sumarizeMoves(String gameid, String winningcolor, GameData gamedata) throws SQLException, ChessBoardException
	{
		ArrayList<Move> moves = getAllGameMoves(gameid);
		for(Move move: moves)
		{
			String cleanDescription = cleanMoveDescription(move);
			MoveSummaryData summaryData = getMoveSummary(cleanDescription);
			if(summaryData == null)
			{
				insertMoveSummary(move, cleanDescription, winningcolor, gamedata);
			}
			else
			{
				updateMoveSummary(summaryData, move, winningcolor, gamedata);
			}
		}
		
	}
	public ArrayList<Move> getAllGameMoves(String gameid) throws SQLException, ChessBoardException
	{
		ArrayList<Move> moves = new ArrayList<Move>();
		Statement stmt = con.createStatement();
		String sql =
			"SELECT * "+
			"FROM  GAME_MOVE "+
			"WHERE game_id like'"+gameid+"'";
		
        ResultSet rst = stmt.executeQuery(sql);
        while(rst.next())
        {
        	Move move = new Move();
        	move.setMatrix(Move.buildMatrix(rst.getString("move_desc")));
        	move.setGameId(rst.getString("game_id"));
       	 	move.setMovingPiece(rst.getString("piece_name"));
       	 	move.setDirection(rst.getString("piece_direction"));
       	 	move.setOpponent(rst.getString("opponent_piece"));
	       	move.setMoveNumber(rst.getInt("move_number"));
	       	move.setMovePoint(rst.getString("move_point"));
	       	moves.add(move);
        }
        stmt.close();
		return moves;
	}
	
	
	private String cleanMoveDescription(Move move)
	{
		String moveDesc = getOrientedBoardDescription(move);
		moveDesc = moveDesc.replaceAll("X", "");
		return moveDesc;
	}

	// MOVE_SUMMARY.move is always defined from white point of view - starting at Kings Rook (starting position)
	// and moving down/right or up/left
	private String getOrientedBoardDescription(Move move)
	{
		String moveDesc = "";
		if(ChessPiece.WHITE.equalsIgnoreCase(move.getColor()) && GameConstants.DOWN.equalsIgnoreCase(move.getDirection()) ||
		   ChessPiece.BLACK.equalsIgnoreCase(move.getColor()) && GameConstants.UP.equalsIgnoreCase(move.getDirection())	)
		{
			moveDesc = move.getBoardDescription();
		}
		else // white moving up or black moving down
		{
			StringBuffer board = new StringBuffer();
			String[][] matrix = move.getMatrix();
			for(int outint = 7; outint > -1; outint--)
			{
				for(int inint=7; inint > -1; inint--)
				{
					board.append(matrix[inint][outint]+",");
				}
			}
			board.deleteCharAt(board.length() -1);
			moveDesc = board.toString();
		}
		return moveDesc;
	}
	
	/*
	 * 
	move			char(255) 	NOT NULL 	PRIMARY KEY,
	white_wins 		INT,
	black_wins 		INT,
	stalemates		INT,
	sum_move_number 	INT,
	sum_move_total 		INT
	 */
	private void insertMoveSummary(Move move, String cleandesc, String winningcolor, GameData gamedata) throws SQLException
	{
		String sql = "insert into MOVE_SUMMARY "+
		"(move,wins,losses,stalemates,sum_move_number,sum_move_total) "+
			"values(?,?,?,?,?,?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, cleandesc);
        int wins = 0;
        int losses = 0;
        int stalemates = 0;
		if(GameTurn.STALEMATE.equals(winningcolor))
		{
			stalemates =1;
		}
		else if(winningcolor.equals(move.getColor()) && winningcolor.equals(ChessPiece.WHITE))
		{
			wins = 1;
		}
		else
		{
			losses = 1;
		}
		pstmt.setInt(2, wins);
        pstmt.setInt(3, losses);
        pstmt.setInt(4, stalemates);
        pstmt.setInt(5, move.getMoveNumber());
        pstmt.setInt(6, getLastMoveNumber(gamedata.getGameId()));
        pstmt.executeUpdate();
        pstmt.close();
	}
	
	private void updateMoveSummary(MoveSummaryData summarydata, Move move, String winningcolor, GameData gamedata) throws SQLException
	{
		String sql = "update MOVE_SUMMARY "+
		"SET wins = ?," +
			"losses = ?," +
			"stalemates = ?," +
			"sum_move_number = ?," +
			"sum_move_total = ? " +
		"WHERE move like '"+summarydata.getMoveSummaryDescription()+"'";
        PreparedStatement pstmt = con.prepareStatement(sql);
        
        int wins = summarydata.getWins();
        int losses = summarydata.getLosses();
        int stalemates = summarydata.getStalemates();
        int sumMoveNumber = (summarydata.getSumMoveNumber() + move.getMoveNumber());
        int sumMoveTotal = (summarydata.getSumMoveTotal() + getLastMoveNumber(gamedata.getGameId()));
		if(GameTurn.STALEMATE.equals(winningcolor))
		{
			stalemates++;
		}
		else if(winningcolor.equals(move.getColor()) && winningcolor.equals(ChessPiece.WHITE))
		{
			wins++;
		}
		else
		{
			losses++;
		}
		pstmt.setInt(1, wins);
        pstmt.setInt(2, losses);
        pstmt.setInt(3, stalemates);
		pstmt.setInt(4, sumMoveNumber);
        pstmt.setInt(5, sumMoveTotal);
        pstmt.executeUpdate();
        pstmt.close();       
	}
	
	private int getLastMoveNumber(String gameid) throws SQLException
	{
		Statement stmt = con.createStatement();
		String sql = "(SELECT MAX(move_number) FROM GAME_MOVE WHERE GAME_ID LIKE'"+gameid+"')";
        ResultSet rst = stmt.executeQuery(sql);
        rst.next();
        int movecount = rst.getInt(1);
        stmt.close();
	    return movecount;
	}
	
	private void updateGameResult(String gameid, String gameresult) throws SQLException
	{
		Statement stmt = con.createStatement();
		String sql =
			"UPDATE game "+
			"SET RESULT = '"+gameresult+"' "+
			"WHERE game_id like'"+gameid+"'";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	private void deleteMoves(String gameid) throws SQLException
	{
		Statement stmt = con.createStatement();
		String sql =
			"DELETE FROM GAME_Move "+
			"WHERE game_id like '"+gameid+"'";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	private void deleteGame(String gameid) throws SQLException
	{
		Statement stmt = con.createStatement();
		String sql =
			"DELETE FROM GAME "+
			"WHERE game_id like '"+gameid+"'";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	

	private void cleanupOldGames() throws SQLException
	{
		Statement stmt = con.createStatement();
		String sql = "SELECT game_id "+
					 "FROM game "+
					 "WHERE time < (NOW() - INTERVAL 3 DAY)";
		ResultSet rst = stmt.executeQuery(sql);
		ArrayList<String> games = new ArrayList<String>();
        while(rst.next())
        {
        	games.add(rst.getString("game_id"));
        }
        stmt.close();
        for(String gameid: games)
        {
        	deleteMoves(gameid);
        	deleteGame(gameid);
        }
	}
	
	private void cleanupUnFinishedGames() throws SQLException
	{
		Statement stmt = con.createStatement();
		String sql = "SELECT game_id "+
					 "FROM game "+
					 "WHERE time < (NOW() - INTERVAL 1 DAY) "+
					 "AND result = null ";
		ResultSet rst = stmt.executeQuery(sql);
		ArrayList<String> games = new ArrayList<String>();
        while(rst.next())
        {
        	games.add(rst.getString("game_id"));
        }
        stmt.close();
        for(String gameid: games)
        {
        	deleteMoves(gameid);
        	deleteGame(gameid);
        }
	}
	
	private void cleanupDBResources() throws SQLException
	{
		persitMovePST.close();
		lastMovePST.close();
		closeConnection();
	}
	
	public static void main(String[] args)
	{
		try
		{
			ChessDAO dao = new ChessDAO();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
}
