package game.dataAccess;

import java.lang.reflect.Field;

public class DataObject 
{

	public String toString()
	{
		String output = this.getClass().getName();
		try
		{
			Field[] fields = this.getClass().getDeclaredFields();
			for(int i=0; i<fields.length; i++)
			{
				output += "\n "+fields[i].getName()+": "+fields[i].get(this);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return output;
	}
}
