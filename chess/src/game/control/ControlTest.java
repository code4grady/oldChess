package game.control;


public class ControlTest 
{
	public static void main(String[] args)
	{
		try
		{
			ChessController chessControl = new ChessController();
			
			
			String[][] bd = new String[8][8];
			bd[0][0]="BRQ";bd[1][0]="BKT";bd[2][0]="BB";bd[3][0]="BQ";bd[4][0]="BKG";bd[5][0]="BB";bd[6][0]="BKT";bd[7][0]="BRK";
			bd[0][1]="BP"; bd[1][1]="X"; bd[2][1]="BP";bd[3][1]="BP";bd[4][1]="BP"; bd[5][1]="BP";bd[6][1]="BP"; bd[7][1]="BP";
			bd[0][2]="X";  bd[1][2]="BP";  bd[2][2]="X" ;bd[3][2]="X"; bd[4][2]="X";  bd[5][2]="X"; bd[6][2]="X";  bd[7][2]="X";
			bd[0][3]="X";  bd[1][3]="X";  bd[2][3]="X"; bd[3][3]="X"; bd[4][3]="X";  bd[5][3]="X"; bd[6][3]="X";  bd[7][3]="X";
			bd[0][4]="X";  bd[1][4]="X";  bd[2][4]="X"; bd[3][4]="X"; bd[4][4]="X";  bd[5][4]="X"; bd[6][4]="X";  bd[7][4]="X";
			bd[0][5]="X";  bd[1][5]="X";  bd[2][5]="X"; bd[3][5]="X"; bd[4][5]="X";  bd[5][5]="X"; bd[6][5]="X";  bd[7][5]="X";
			bd[0][6]="WP"; bd[1][6]="WP"; bd[2][6]="WP";bd[3][6]="WP";bd[4][6]="WP"; bd[5][6]="WP";bd[6][6]="WP"; bd[7][6]="WP";
			bd[0][7]="WRQ";bd[1][7]="WKT";bd[2][7]="WB";bd[3][7]="WQ";bd[4][7]="WKG";bd[5][7]="WB";bd[6][7]="WKT";bd[7][7]="WRK";

			for(int i=0; i< 10; i++)
			{
				String gameid = chessControl.setupNewGame("W", "UP");
				String boarddesc = Move.getBoardDescription(bd);
				GameTurn turn = chessControl.makeChessMove(boarddesc, gameid);
				int turns = 0;
				try
				{
					while(true)
					{
						if(turn.getSuggestedMove() != null)
						{
							System.out.print(turn.getSuggestedMove().toString()+"\n");
							String board = turn.getSuggestedMove().getBoardDescription();
							turn = chessControl.makeChessMove(board, gameid);
						}
						if(turn.getGameMessage() != null && turn.getGameMessage().indexOf(GameTurn.INCHECK) == -1)
						{
							break;
						}
						if(turns++ > 40 || turn.getSuggestedMove() == null) 
						{
							turn.setGameMessage(GameTurn.STALEMATE);
							chessControl.finishGame(turn);
							break ;
						}
					}	
				}catch(Exception e)
				{
					System.out.println(""+turn.getSuggestedMove());
					e.printStackTrace();
				}
				System.out.println(""+i+" status = "+turn.getGameMessage());
				
			}

		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	private static String fillSpace(int size)
	{
		String space= "";
		for( int spacecount = 4-size; spacecount>0; spacecount--)
		{
			space += " ";
		}
		return space;
	}

}
