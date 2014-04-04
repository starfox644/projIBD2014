package accesBD;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import utils.ErrorLog;

import modele.Place;
import exceptions.ConnectionException;
import exceptions.RequestException;
import exceptions.ReservationException;

public class BDPlaces 
{
	/**
	 * Renvoie la liste des places disponibles pour la representation du spectacle
	 * de numero numS prevu a la date passee en parametre.
	 * @param date date de la representation
	 * @param numS numero du spectacle
	 * @return Vector<Place> contenant les places disponibles pour cette representation.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static Vector<Place> getPlacesDispo (int numS, String date, int heure) throws RequestException, ConnectionException 
	{
		Vector<Place> res = new Vector<Place>();
		SQLRequest request = new SQLRequest();
		try
		{
			res = getPlacesDispo(request, numS, date, heure);
		}
		catch(Exception e)
		{
			request.close();
			throw e;
		}
		request.close();
		return res;
	}

	public static Vector<Place> getPlacesDispo (SQLRequest request, int numS, String date, int heure) throws RequestException, ConnectionException 
	{
		Vector<Place> res = new Vector<Place>();
		String str = "select noPlace, noRang, numZ" +
				" from LesPlaces " +
				" natural join" +
				" (select noPlace, noRang" +
				"	from LesPlaces" +
				"	minus" +
				"	select noPlace, noRang from LesTickets where dateRep = to_date('"+date+" "+ heure + "', 'DD/MM/YYYY HH24') and numS = " + numS + ") order by noRang";

		ResultSet rs = request.execute(str);
		try {
			while (rs.next()) {
				res.addElement(new Place (rs.getInt(1), rs.getInt(2), rs.getInt(3)));
			}

		} catch (SQLException e) {
			throw new RequestException (" Erreur dans getPlacesDispo : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		return res;
	}

	/**
	 * Renvoie le nombre de places occupees pour la representation du spectacle
	 * de numero numS prevu a la date passee en parametre.
	 * @param date date de la representation
	 * @param numS numero du spectacle
	 * @return Le nombre de places occupees pour cette representation
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static int getNbPlacesOccupees (int numS, String date, int heure) throws ConnectionException, RequestException
	{
		int nbPlaces = 0;
		String str = "select count(noPlace) " +
				"from LesTickets " +
				"where dateRep = to_date('"+date+" "+heure + "' , 'DD/MM/YYYY HH24') and numS = " + numS;

		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try {
			while (rs.next()) {
				nbPlaces = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new RequestException (" Erreur dans getNbPlacesOccupees : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return nbPlaces;
	}


	/**
	 * Renvoie le nombre de places de la salle de theatre.
	 * @return le nombre de places que contient le theatre.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static int getNbPlacesTotales ()	throws ConnectionException, RequestException
	{

		Vector<Integer> res = new Vector<Integer>();
		String str = "select count(noPlace) " +
				"from LesPlaces ";
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try {
			while (rs.next()) {
				res.addElement(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new RequestException (" Erreur dans getNbPlacesTotales : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return res.get(0);
	}

	public static boolean reserverPlace(int numS, String dateRep, String dateRes, int heureS, int numZ, int noPlace, int noRang)
			throws RequestException, ConnectionException 
			{
		SQLRequest request = new SQLRequest();
		ResultSet rs;
		int i = 0;

		String verifReq = "select count(noSerie)" +
				"from LesTickets " +
				"where numS = " + numS +
				"and dateRep = to_date('"+dateRep+" "+heureS + "' , 'DD/MM/YYYY HH24')" +
				"and noPlace = " + noPlace +
				"and noRang = " + noRang;
		rs = request.execute(verifReq);

		try {
			if(rs.next() && rs.getInt(1) != 0)
			{
				request.close();
				return false;
			}
		} catch (SQLException e) 
		{
			throw new RequestException (" Erreur dans la verification de place : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		// recuperation du numero de ticket max 
		String strMax = "select max(noSerie) " +
				"from LesTickets ";
		rs = request.execute(strMax);
		int noSerie = 0;
		try {
			if ( rs.next() )
			{
				noSerie = rs.getInt(1)+1;
			}
		} catch (SQLException e) {
			throw new RequestException (" Erreur dans la recuperation du max noSerie : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}

		// ajout du ticket 
		//Ticket t = new Ticket(noSerie, numS, dateRep + " " + heureS, p.getNoPlace(), p.getNoRang(), dateRes, 66);
		//out.println("<br> creation ticket ok. Le ticket est : " + t + "<br>");

		String strTicket = "INSERT INTO LesTickets " +
				"VALUES( " + noSerie + ", " + numS + ", "	+ "to_date('" + dateRep + " " + heureS  + "', 'DD/MM/YY HH24') , " 
				+ noPlace + ", "  + noRang + ", to_date('" + dateRes + "', 'DD/MM/YY HH24') , " + 
				"66 )";
		rs = request.execute(strTicket);
		request.commit();
		request.close();
		return true;
			}

	public static boolean validDateRep(String dateS, int heureS) throws ParseException
	{
		boolean valid = true;
		// on verifie que la date de la representation est valide
		String dateH = dateS + " " + heureS;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH");
		Date dateRep;

		dateRep = formatter.parse(dateH);
		Calendar today = Calendar.getInstance();

		Calendar date = Calendar.getInstance();
		date.setTime(dateRep);
		today.add(Calendar.HOUR, 1);
		int month = today.get(Calendar.MONTH)+1;
		String dateToday = new String (today.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + today.get(Calendar.YEAR)
				+ " " + today.get(Calendar.HOUR_OF_DAY));

		// date inferieure a celle d'aujourd'hui
		if (date.compareTo(today) < 0)
		{
			valid = false;
		}

		return valid;
	}

	public static boolean checkAjoutPanier(int numS, String dateS, int heureS, int nbPlaces) throws ConnectionException, RequestException, ReservationException
	{
		SQLRequest request = new SQLRequest();
		ErrorLog log = null;
		
		try {
			log = new ErrorLog();
		} catch (IOException e) {}

		String nomS = BDSpectacles.getNomSpectacle(request, numS);
		// on verifie que le numero de spectacle existe
		if (nomS == null)
		{
			request.close();
			throw new ReservationException("Cette repr&eacute;sentation n'existe plus.");
		}
		try {
			if(!validDateRep(dateS, heureS))
			{
				request.close();
				throw new ReservationException("Il est trop tard pour r&eacute;server cette repr&eacute;sentation.");
			}
		} catch (ParseException e) 
		{
			if(log != null)
			{
				log.writeException(e);
			}
			request.close();
			throw new ReservationException("Erreur interne du serveur. Impossible de r&eacute;server cette place.");
		}
		if (BDRepresentations.existeDateRep (request, numS,dateS, heureS))
		{
			//out.println("<br> Cette representation existe <br>");
			Vector<Integer> result = new Vector<Integer>();
			Vector<Place> placesDispo = new Vector<Place>();
			Place p = null;
			int i = 0;
			// on verifie qu'il reste des places disponibles pour 
			// cette representation
			placesDispo = BDPlaces.getPlacesDispo(request, numS, dateS, heureS);
			// plus de place
			if (placesDispo.isEmpty())
			{
				request.close();
				throw new ReservationException("Il n'y a plus de place disponible pour cette representation.");
			}
		}
		else
		{
			request.close();
			throw new ReservationException("Cette repr&eacute;sentation n'existe plus.");
		}
		request.close();
		return true;
	}
}
