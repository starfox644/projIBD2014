package accesBD;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.servlet.ServletOutputStream;

import utils.ErrorLog;

import modele.Categorie;
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
		Transaction request = new Transaction();
		try
		{
			res = getPlacesDispo(request, numS, date, heure);
		}
		catch (ConnectionException e)
		{
			request.close();
			throw e;
		}
		catch (RequestException e)
		{
			request.close();
			throw e;
		}
		request.close();
		return res;
	}

	public static Vector<Place> getPlacesDispo (Transaction request, int numS, String date, int heure) throws RequestException, ConnectionException 
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

	public static Vector<Place> getPlacesDispo(Transaction request, int numS, String date, int heure, Categorie c) throws RequestException, ConnectionException 
	{
		String cat = c.getCategorie();
		Vector<Place> res = new Vector<Place>();
		String str = " select noPlace, noRang, numZ" +
					  " from " + 
						" (select noPlace, noRang, numZ" +
						"  from LesPlaces " +
						"  where numZ in (select numZ" +
						"				 from LesZones" +
						"				 where nomC='"+ cat +"'))"+
						" natural join " +
						"   ((select noPlace, noRang" +
						"	from LesPlaces)" +
						"	minus" +
						"	(select noPlace, noRang from LesTickets " +
						"	where dateRep = to_date('"+date+" "+ heure + "', 'DD/MM/YYYY HH24') and numS = " + numS + "))" +
						" order by numZ, noRang, noPlace";
						//" order by noPlace, noRang, numZ";
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

		Transaction request = new Transaction();
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
		Transaction request = new Transaction();
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
		Transaction request = new Transaction();
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

	public static boolean checkAjoutPanier(int numS, String dateS, int heureS, int nbPlaces, Categorie categorie) 
			throws ConnectionException, RequestException, ReservationException
	{
		Transaction request = new Transaction();
		ErrorLog log = null;
		nbPlaces = 10;
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
			// on verifie qu'il reste des places disponibles pour 
			// cette representation
			placesDispo = BDPlaces.getPlacesDispo(request, numS, dateS, heureS, categorie);
			if (placesDispo.isEmpty() || placesDispo.size() < nbPlaces)
			{
				request.close();
				throw new ReservationException("Il n'y a plus de place disponible pour cette representation.");
			}
			else
			{
					Vector<Place> places = placesSucc(placesDispo, nbPlaces);
					if(places.isEmpty())
					{
						request.close();
						throw new ReservationException("Il n'y a pas assez de places disponibles pour cette representation.");
					}
				/*for (int i = 0 ; i < placesDispo.size() ; i++)
				{
					System.out.println("numZ : "+ placesDispo.get(i).getNumZ() + 
								    " , noRang : " + placesDispo.get(i).getNoRang() +
								    " , noPlace : "+ placesDispo.get(i).getNoPlace());
				}
				if (places.isEmpty())
				{
					System.out.println(" Les places succ sont ::: Y en A PAS MOUAH AHAHAH");
				}
				else
				{
					System.out.println(" Les places succ sont ::: ");
					for (int i  = 0 ; i < places.size() ; i++)
					{
						System.out.println("numZ : "+ places.get(i).getNumZ() + 
							    " , noRang : " + places.get(i).getNoRang() +
							    " , noPlace : "+ places.get(i).getNoPlace());
					}
				}*/
				
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
	
	public static Vector<Place> placesSucc(Vector<Place> placesDispo, int nbPlaces)
	{
		Vector<Place> places = new Vector<Place>();
		boolean trouve = false;
		int i = 0;
		int j = 0; 
		if ((placesDispo.size()-nbPlaces) < 0)
			return null;
		while (i < (placesDispo.size()-nbPlaces) && !trouve)
		{
			j = i;
			// verification q'ils ait le meme numero de zone
			while( j < nbPlaces && (placesDispo.get(j).getNumZ() == placesDispo.get(j+1).getNumZ()))
			{
				j++;
			}
			// meme zone, on continue la verif
			if( j == nbPlaces )
			{
				System.out.println(" i = " + i + "  meme numZ");
				j = i;
				// verification q'ils ait le meme numero de rang
				while( j < nbPlaces && (placesDispo.get(j).getNoRang() == placesDispo.get(j+1).getNoRang()))
				{
					j++;
				}
				// meme noRang, on continue la verif
				if( j == nbPlaces )
				{
					System.out.println(" i = " + i + "  meme rang");
					j = i;
					// verification q'ils ait des numeros de places successifs
					while( j < nbPlaces && (placesDispo.get(j).getNoPlace() == (placesDispo.get(j+1).getNoPlace()-1)))
					{
						j++;
					}
					// meme noRang, on continue la verif
					if( j == nbPlaces )
					{
						System.out.println(" i = " + i + "  noRange + 1");
						trouve = true;
						for (int z = i ; z < i + nbPlaces ; z++)
						{
							places.add(placesDispo.get(z));
							System.out.println("numZ : "+ placesDispo.get(z).getNumZ() + 
								    " , noRang : " + placesDispo.get(z).getNoRang() +
								    " , noPlace : "+ placesDispo.get(z).getNoPlace());
						}
						System.out.println("**********************");
					}
				}
			}
			i++;
		}
		return places;
	}
}
