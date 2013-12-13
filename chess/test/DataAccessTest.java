
import junit.framework.TestCase;

import java.sql.*;


public class DataAccessTest extends TestCase 
{

   public void testJDBC() throws Exception 
   {
   	  System.out.println(" starting testJDBC");
   	
	  String move = "'abcd'";
      String gameid = "'01234'";
      
	  Connection conn = null;
	  Statement stmt = null;
      try 
      {
    	 //System.setProperty("jdbc.drivers", "com.mysql.jdbc.Driver");
         //conn = DriverManager.getConnection("jdbc:mysql://localhost/chess","root","admin");
         
         DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
         conn = DriverManager.getConnection("jdbc:mysql://localhost/chess","root","admin");
        
    	 String sql = null;
    	 int rows = 0;
    	 ResultSet rst = null;
    	
    	 
    	 // delete any previous partial tests
    	 try
    	 {
	         stmt = conn.createStatement();
             sql = "delete from GAME_MOVE where move_desc like "+move;
	         stmt.executeUpdate(sql);
	         
             stmt = conn.createStatement();
             sql = "delete from GAME where game_id like "+gameid;
             stmt.executeUpdate(sql);
    	 }
    	 catch(Exception ignore){}
    	 
    	 
    	 // insert something
         stmt = conn.createStatement();
         sql = "insert into GAME values("+gameid+",'W','UP',null,null)";
         rows = stmt.executeUpdate(sql);
         if (rows!= 1)
         {
             throw new Exception ("insert into GAME failed");
         }
    	 stmt = conn.createStatement();
         sql = "insert into GAME_MOVE values("+move+","+gameid+",null,null,null,null,null)";
         rows = stmt.executeUpdate(sql);
         if (rows!= 1)
         {
        	 throw new Exception ("insert into GAME_MOVE failed");
         }
         System.out.println("insert complete");
         
         
         // query something
         stmt = conn.createStatement();
         sql = "select * from GAME_MOVE where move_desc like "+move;
         rst = stmt.executeQuery(sql);
         while(rst.next())
         {
             System.out.println("    MOVE_SUMMARY  found "+ rst.getString("move_desc"));
         }
         System.out.println("query complete");
         
         
         // clean up - remove the stuff you just inserted
         stmt = conn.createStatement();
         sql = "delete from GAME_MOVE where move_desc like "+move;
         rows = stmt.executeUpdate(sql);
         if (rows!= 1)
         {
             throw new Exception ("delete from GAME_MOVE failed");
         }
         stmt = conn.createStatement();
         sql = "delete from GAME where game_id like "+gameid;
         rows = stmt.executeUpdate(sql);
         if (rows!= 1)
         {
             throw new Exception ("delete from GAME failed");
         }
         System.out.println("cleanup complete");
         
      } 
      catch (Exception ex) 
      {
    	 ex.printStackTrace();
         assertTrue(false);
      }
      finally
      {
          if(stmt != null) stmt.close();
          if(conn != null) conn.close();
      }
      assertTrue(true);
   }
   
   
//   public static void main(String[] args)
//  {
//      try
//      {
//          DataAccessTest test = new DataAccessTest();
//          test.testJDBC();
//      }
//      catch(Throwable t)
//      {
//          t.printStackTrace();
//      }
//      
//  }



}
