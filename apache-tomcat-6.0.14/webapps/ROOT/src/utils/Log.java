package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 		Permet de gerer un fichier de log.
 */
public class Log 
{
	private final String filePath;
	
	/**
	 * 		Cree un fichier de log d'un certain type, place au debut du nom du fichier.
	 * @param type	Type de fichier de log, place au debut du nom de fichier.
	 */
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
	
	/**
	 * 		Ouvre le fichier de log ou le cree dans le dossier des
	 * 		fichiers de log s'il n'existe pas.
	 * @return	Fichier de log ouvert.
	 * @throws IOException	Si le fichier n'a pas pu etre ouvert.
	 */
	private File openFile() throws IOException
	{
		File f = new File(filePath);
		if(!f.exists())
		{
			f.createNewFile();
		}
		return f;
	}
	
	/**
	 * 		Ecrit un message dans le fichier de log.
	 * @param s		Message a ecrire dans le fichier.
	 */
	public void write(String s)
	{
		String hourStr;
		int hour;
		int minute;
		int second;
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
