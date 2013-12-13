package game.dataAccess;

import java.lang.reflect.*;


public class DataManagerProxy implements InvocationHandler 
{
	private static DataManagerProxy proxy;
    private static ChessDAO dao = new ChessDAO();

    public static synchronized Object newInstance() 
    {
    	if(proxy == null)
    	{
    		proxy = new DataManagerProxy();
    	}
		return java.lang.reflect.Proxy.newProxyInstance(
			dao.getClass().getClassLoader(),
			dao.getClass().getInterfaces(),
			proxy);
    }

    private DataManagerProxy() 
    {
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
    {
        Object result;
		try 
		{
		    dao.validateConnection();
		    result = m.invoke(dao, args);  
		} 
		catch (InvocationTargetException e) 
		{
			throw e.getTargetException();
	    } 
		catch (Exception e) 
		{
		    throw new RuntimeException("unexpected invocation exception: " +e.getMessage());
		} 
		finally 
		{
//		    System.out.println("after method " + m.getName());
		}
		return result;
    }

}
