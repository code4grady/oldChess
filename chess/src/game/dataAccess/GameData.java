package game.dataAccess;

import java.sql.Timestamp;

/*
 *  table GAME
	game_id	 		VARCHAR(50) 	NOT NULL 	PRIMARY KEY,
	color 			VARCHAR(5),
	direction		VARCHAR(5),
	time 			TIMESTAMP,
	result 			VARCHAR(10)
 */
public class GameData extends DataObject
{
	String gameId;
	String color;
	String direction;
	Timestamp time;
	String result;
	

	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	
	
}
