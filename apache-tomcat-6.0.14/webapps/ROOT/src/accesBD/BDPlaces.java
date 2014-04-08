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

import modele.Categorie;
import modele.Place;
import exceptions.ConnectionException;
import exceptions.RequestException;
import exceptions.ReservationException;

/**
 * 		Requetes permettant d'acceder aux places du theatre,
 * 		de gerer les places disponibles, de verifier qu'une commande est realisable
 * 		et d'effectuer des reservations.
 */
public class BDPlaces 
{
	/**
	 * 		Renvoie la liste des places disponibles pour la representation du spectacle
	 * 		de numero numS prevu a la date passee en parametre.
	 * 
	 * @param numS Numero du spectacle.
	 * @param date Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param heure Heure du spectacle.
	 * @return Vector<Place> contenant les places disponibles pour cette representation.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
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
		catch (RequestException e)
		{
			request.close();
			throw e;
		}
		request.close();
		return res;
	}

	/**
	 * 		Renvoie la liste des places disponibles pour la representation du spectacle
	 * 		de numero numS prevu a la date passee en parametre, en utilisant une transaction
	 * 		deja cree.
	 * 
	 * @param request Transaction a utiliser pour effectuer la requete.
	 * @param numS Numero du spectacle.
	 * @param date Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param heure Heure du spectacle.
	 * @return Vector<Place> contenant les places disponibles pour cette representation.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 */
	public static Vector<Place> getPlacesDispo (Transaction request, int numS, String date, int heure) throws RequestException
	{
		Vector<Place> res = new Vector<Place>();
		String str = "select noPlace, noRang, numZ" +
				" from LesPlaces " +
				" natural join" +
				" (select noPlace, noRang" +
				"	from LesPlaces" +
				"	minus" +
				"	select noPlace, noRang from LesTickets where dateRep = to_date('"+date+" "+ heure + "', 'DD/MM/YYYY HH24') and numS = " + numS + ") " +
				" order by numZ, noRang, noPlace";

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
	 * 		Renvoie la liste des places disponibles dans une certaine categorie
	 * 		 pour la representation du spectacle de numero numS prevu a la date 
	 * 		passee en parametre, en utilisant une transaction deja cree.
	 * 
	 * @param date Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param numS Numero du spectacle.
	 * @param heure Heure du spectacle.
	 * @param categorie Categorie ou le nombre de places doit etre verifie.
	 * @return Vector<Place> contenant les places disponibles pour cette representation.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 */
	public static Vector<Place> getPlacesDispo(Transaction request, int numS, String date, int heure, Categorie categorie) throws RequestException 
	{
		String cat = categorie.getNom();
		Vector<Place> res = new Vector<Place>();
		String str =
		" select noPlace, noRang, numZ" +
		" from LesPlaces" +
		" where (noPlace, noRang) in" +
		"    ((select noPlace, noRang "+
		"    from LesPlaces" +
		"    where numZ in" +
		"    	(select numZ" +
		"		from LesZones" +
		"		where nomC='" + cat + "'))" +
		" minus" +
		"	(select noPlace, noRang from LesTickets " +
		"	where dateRep = to_date('" +date+ " " + heure + "', 'DD/MM/YYYY HH24')  and numS= " + numS + ")) " +
		" order by numZ, noRang, noPlace" ;
		
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
	 * 		Renvoie le nombre de places occupees pour la representation du spectacle
	 * 		de numero numS prevu a la date passee en parametre.
	 * @param date Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param numS Numero du spectacle.
	 * @param heure Heure du spectacle.
	 * @return Le nombre de places occupees pour cette representation
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
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
	 * 		Renvoie le nombre de places de la salle de theatre.
	 * @return le nombre de places que contient le theatre.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
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
	
	public static boolean reserverPlace(Transaction request, int numS, 
			String dateRep, String dateRes, int heureS, int numZ, int noPlace, int noRang)
			throws RequestException, ConnectionException 
	{
		ResultSet rs;

		String verifReq = "select count(noSerie)" +
				" from LesTickets " +
				" where numS = " + numS +
				" and dateRep = to_date('"+dateRep+" "+heureS + "' , 'DD/MM/YYYY HH24')" +
				" and noPlace = " + noPlace +
				" and noRang = " + noRang;
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
				+ noPlace + ", "  + noRang + ", to_date('" + dateRes + "', 'DD/MM/YY HH24'))";
		rs = request.execute(strTicket);
		//request.commit();
		//request.close();
		return true;
			}


	/**
	 * 		Methode utilisee en interne pour verifier la validite d'une commande.
	 * 			<br>
	 * 		Indique si la date et l'heure de representation passes en parametre sont
	 * 		valides pour effectuer une reservation.
	 * 			<br>
	 * 		Un couple date / heure de representation est valide jusqu'a heure - 1,
	 * 		elle est donc invalide si la date actuelle + 1 heure n'est pas
	 * 		inferieure a la date de representation.
	 * 
	 * @param dateS Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param heureS Heure du spectacle.
	 * @return		True si la date de representation est valide, false sinon.
	 * 
	 * @throws ParseException	Si la date donnee en parametre n'est pas au bon format.
	 */
	private static boolean validDateRep(String dateS, int heureS) throws ParseException
	{
		boolean valid = true;
		// on verifie que la date de la representation est valide
		String dateH = dateS + " " + heureS;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH");
		Date dateRep;

		dateRep = formatter.parse(dateH);
		Calendar today = Calendar.getInstance();

		// recuperation de date / heure actuels
		Calendar date = Calendar.getInstance();
		date.setTime(dateRep);
		// ajout d'une heure a l'heure actuelle pour la comparaison
		today.add(Calendar.HOUR, 1);

		// date inferieure a celle d'aujourd'hui + 1, invalide
		if (date.compareTo(today) < 0)
		{
			valid = false;
		}

		return valid;
	}


	/**
	 * 			Verifie si une commande peut etre ajoutee au panier de l'utilisateur, c'est a dire
	 * 		qu'elle serait valide pour etre reservee a l'instant de l'ajout.
	 * 			<br>
	 * 
	 * 			L'existence de la representation a l'heure demandee est verifiee.
	 * 			<br>
	 * 			Un couple date / heure de representation est valide jusqu'a heure - 1,
	 * 		elle est donc invalide si la date actuelle + 1 heure n'est pas
	 * 		inferieure a la date de representation.
	 * 			<br>
	 * 
	 * 			Le nombre de places est valide si autant de places contigues peuvent etre
	 * 		trouvees dans la categorie demandee.
	 * 			<br>
	 * 
	 * 			
	 * @param dateS Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param numS Numero du spectacle.
	 * @param heureS	Heure du spectacle.
	 * @param nbPlaces	Nombre de places de la commandes.
	 * @param categorie	Categorie dans laquelle les places doivent etre placees.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 * @throws ReservationException		
	 * 					Si l'ajout au panier est impossible.
	 * 					Contient un message affichable a l'utilisateur indiquant
	 * 					pourquoi il est impossible d'ajouter la place.
	 */
	public static void checkAjoutPanier(int numS, String dateS, int heureS, int nbPlaces, Categorie categorie) 
			throws ConnectionException, RequestException, ReservationException
			{
		Transaction request = new Transaction();
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
			// verification de la validite de la date pour la reservation
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
		if (BDRepresentations.existeDateRep (request, numS, dateS, heureS))
		{
			Vector<Place> placesDispo = new Vector<Place>();
			if(categorie != null)
			{
				// recuperation de la liste de toutes les places disponibles pour la representation
				// dans la categorie demandee
				placesDispo = BDPlaces.getPlacesDispo(request, numS, dateS, heureS, categorie);
				// on verifie qu'il reste au moins une place disponible a la representation demandee
				if (placesDispo.isEmpty() || placesDispo.size() < nbPlaces)
				{
					request.close();
					throw new ReservationException("Il n'y a plus de place disponible pour cette representation.");
				}
				else
				{
					// on verifie qu'il reste des places disponibles pour 
					// cette representation dans la categorie demandee
					Vector<Place> places = placesSucc(placesDispo, nbPlaces);
					if(places.isEmpty())
					{
						request.close();
						throw new ReservationException("Il n'y a pas assez de places disponibles pour cette representation.");
					}
				}
			}
			else
			{
				placesDispo = BDPlaces.getPlacesDispo(request, numS, dateS, heureS);
				if (placesDispo.isEmpty() || placesDispo.size() < nbPlaces)
				{
					request.close();
					throw new ReservationException("Il n'y a plus de place disponible pour cette representation.");
				}
			}
		}
		else
		{
			request.close();
			throw new ReservationException("Cette repr&eacute;sentation n'existe plus.");
		}
		request.close();
			}

	/**
	 * 		Retourne la liste des places successives trouvees correspondant au nombre
	 * 		de places demandees a partir d'une liste de places, si autant de places
	 * 		contigues peuvent etre trouvees.
	 * @param placesDispo	Liste de places a verifier.
	 * @param nbPlaces		Nombre de places demandees.
	 * @return				Vector<Place> contenant nbPlaces contigues ou vide si cela est impossible.
	 */
	public static Vector<Place> placesSucc(Vector<Place> placesDispo, int nbPlaces)
	{
		Vector<Place> places = new Vector<Place>();
		boolean trouve = false;
		int i = 0;
		int j = 0; 
		// si le nombre de places libres n'atteint meme pas le nombre de places demandees
		// inutile de faire plus de verifications
		if ((placesDispo.size()-nbPlaces) < 0)
			return places;
			// parcours des places par groupe de nbPlaces consecutives
		while (i < (placesDispo.size()-nbPlaces+1) && !trouve)
		{
			j = i;
			// on verifie que les places ont le meme numero de zone
			while( j < (i+nbPlaces-1) && (placesDispo.get(j).getNumZ() == placesDispo.get(j+1).getNumZ()))
			{
				j++;
			}
			// meme zone, on continue la verification
			if( j == (i+nbPlaces-1) )
			{
				j = i;
				// verification de l'egalite du numero de rang
				while( j < (i+nbPlaces-1) && (placesDispo.get(j).getNoRang() == placesDispo.get(j+1).getNoRang()))
				{
					j++;
				}
				// meme numero de rang, on continue la verification
				if( j == (i+nbPlaces-1) )
				{
					j = i;
					// verification des numeros de places successifs
					while( j < (i+nbPlaces-1) && (placesDispo.get(j).getNoPlace() == (placesDispo.get(j+1).getNoPlace()-1)))
					{
						j++;
					}
					// nbPlaces consecutives trouvees au meme rang et meme zone, ok
					if( j == (i+nbPlaces-1) )
					{
						trouve = true;
						// ajout des places trouvees au resultat
						for (int z = i ; z < i + nbPlaces ; z++)
						{
							places.add(placesDispo.get(z));
						}
					}
				}
			}
			i++;
		}
		return places;
	}
}
