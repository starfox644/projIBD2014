package utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * 		Methodes utilisees dans toute l'application.
 */
public class Utilitaires {

	
	public Utilitaires() {
	}
	
	/**
	 * 		Renvoie le mois correspondant a un numero de mois.
	 * @param m		Numero du mois.
	 * @return	String contenant le mois correspondant au numero.
	 */
	public static String printMonth(int m)
	{
		String month;
		switch (m)
		{
			case 1 : 
				month = "Janvier";
				break;
			case 2 : 
				month = "Fevrier";
				break;
			case 3 : 
				month = "Mars";
				break;
			case 4 : 
				month = "Avril";
				break;
			case 5 : 
				month = "Mai";
				break;
			case 6 : 
				month = "Juin";
				break;	
			case 7 : 
				month = "Juillet";
				break;
			case 8 : 
				month = "Aout";
				break;
			case 9 : 
				month = "Septembre";
				break;
			case 10 : 
				month = "Octobre";
				break;
			case 11 : 
				month = "Novembre";
				break;
			case 12 : 
				month = "Decembre";
				break;
			default : 
				month = "";
				break;
		}
		return month;
		
	}
	
	/**
	 * 		Convertit une date renvoyee par une requete SDL (au format Oracle) 
	 * 		en date affichable a l'utilisateur.
	 * @param date	Date au format Oracle.
	 * @return	String contenant une date affichable a l'utilisateur.
	 */
	public static String printDate(String date)
	{
		String[] d = date.split(" ");
		String[] ymd = d[0].split("-");
		String[] h = d[1].split(":");
		String newdate = ymd[2] + " "+ printMonth(Integer.parseInt(ymd[1]))+ " " + ymd[0] + " a " + h[0] + "H";
		return newdate;
	}
	 
	/**
	 * 		Indique si une date est dans le format defini par Constantes.dateFormat.
	 * @param date String contenant la date.
	 * @return	true si la date correspond au format, false sinon.
	 */
	public static boolean validDateFormat(String date)
	{
		boolean res = false;
		SimpleDateFormat formatter = new SimpleDateFormat(Constantes.dateFormat);
		try
		{
			formatter.parse(date);
			String[] d= date.split("/");
			// verification du jour 
			if (Integer.parseInt(d[0]) > 0 && Integer.parseInt(d[0]) < 32)
			{
				// verification du mois
				if (Integer.parseInt(d[1]) > 0 && Integer.parseInt(d[1]) < 13)
				{
					res = true;
				}
			}
		}
		catch(ParseException e)
		{
			res = false;
		}
		return res;
	}
	
	/**
	 * 		Indique si une String peut etre convertie en entier.
	 * @param integer String contenant un entier.
	 * @return	True si integer contient un entier, false sinon.
	 */
	public static boolean validIntegerFormat(String integer)
	{
		boolean res = true;
		try
		{
			Integer.parseInt(integer);
		}
		catch(NumberFormatException e)
		{
			res = false;
		}
		return res;
	}
	
	/**
	 * 		Indique si une String contient une heure, c'est a dire un entier
	 * 		compris entre 0 et 23.
	 * @param integer String contenant une heure.
	 * @return	True si integer contient une heure, false sinon.
	 */
	public static boolean validHourFormat(String integer)
	{
		boolean valid = true;
		int hour = -1;
		try
		{
			hour = Integer.parseInt(integer);
		}
		catch(NumberFormatException e)
		{
			valid = false;
		}
		if(valid)
		{
			valid = hour >= 0 && hour <= 23;
		}
		return valid;
	}
}
