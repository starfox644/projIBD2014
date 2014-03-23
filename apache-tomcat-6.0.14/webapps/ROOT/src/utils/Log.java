package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Log 
{
	private final String filePath;
	
	public Log(String type)
	{
		String dir = Constantes.getLogPath();
		int year;
		int month;
		int day;
		if(dir.charAt(dir.length()-1) != '/')
		{
			dir += '/';
		}
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		String dateRep = year + "-" + month + "-" + day;
		filePath = dir + type + "." + dateRep + ".log";
	}
	
	private File openFile() throws IOException
	{
		File f = new File(filePath);
		if(!f.exists())
		{
			f.createNewFile();
		}
		return f;
	}
	
	public void write(String s)
	{
		String hourStr;
		int hour;
		int minute;
		int second;
		Date d;
		try
		{
			File f = openFile();
			FileWriter writer = new FileWriter(f, true);
			Calendar c = new GregorianCalendar();
			c.setTime(new Date());
			hour = c.get(Calendar.HOUR_OF_DAY);
			minute = c.get(Calendar.MINUTE);
			second = c.get(Calendar.SECOND);
			hourStr = hour + ":" + minute + ":" + second;
			writer.write(hourStr + "\n");
			writer.write(s);
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
	}
}
