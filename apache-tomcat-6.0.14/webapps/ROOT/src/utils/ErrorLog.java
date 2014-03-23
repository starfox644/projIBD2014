package utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ErrorLog extends Log
{
	public ErrorLog() throws IOException 
	{
		super("error");
	}
	
	public void writeException(Exception e)
	{
		String exRep = "Exception name : " + e.getClass() + "\n";
		exRep += "Message : \n " + e.getMessage() + "\n"; 
		write(exRep);
	}
	
}
