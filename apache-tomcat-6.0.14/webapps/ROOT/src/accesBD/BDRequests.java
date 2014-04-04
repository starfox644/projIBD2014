package accesBD;
import java.sql.*;
import java.util.Vector;

import utils.Utilitaires;
import exceptions.*;
import modele.*;

public class BDRequests 
{

	public BDRequests () 
	{

	}

	 /**
	 * 		Retourne la liste de toutes les representations, chacune contenant la date, le nom du spectacle et son numero.
	 * @return Vector<Representation> contenant toutes les representations
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static Vector<Representation> getRepresentations () throws ConnectionException, RequestException
	{
		Vector<Representation> res = new Vector<Representation>();
		String str = "select nomS, dateRep, numS " +
				"from LesRepresentations " +
				"natural join LesSpectacles order by dateRep";
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try
		{
			while (rs.next()) {
				res.addElement(new Representation (rs.getString(1), rs.getString(2), rs.getInt(3)));
			}
		}
		catch(SQLException e)
		{
			throw new RequestException ("Erreur dans getRepresentations \n"
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		request.close();
		return res;
	}

	/**
	 * 		Retourne les dates de representations d'un spectacle identifie par son numero.
	 * @param num String contenant le numero du spectacle.
	 * @return Vector<String> contenant les dates de la representation.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static Vector<Representation> getSpectacleRepresentations (int numS) throws RequestException, ConnectionException 
	{
		Vector<Representation> res = new Vector<Representation>();
		String str = "select nomS, dateRep, numS " +
				"from LesRepresentations " +
				"natural join LesSpectacles " +
				"where numS = " + numS
				+ "order by dateRep DESC";
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try {		
			while (rs.next()) {
				res.addElement(new Representation (rs.getString(1), rs.getString(2), rs.getInt(3)));
			}
		} catch (SQLException e) {
			throw new RequestException ("Erreur dans getSpectacleRepresentations \n"
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		request.close();
		return res;
	}

	/**
	 * 		Permet de recuperer le nom d'un spectacle a partir de son numero.
	 * @param numS Numero du spectacle dont on veut recuperer le nom.
	 * @return String contenant le nom du spectacle associe au numero, ou null s'il n'y en a pas d'associe au numero.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static String getNomSpectacle(int numS) throws RequestException, ConnectionException
	{
		String nom = null;
		String str = "select nomS from LesSpectacles where " + numS + "=numS";
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try {
			if(rs.next()) 
			{
				nom = rs.getString(1);
			}
		} catch (SQLException e) {
			throw new RequestException ("Erreur dans getNomSpectacle \n"
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		request.close();
		return nom;
	}

	/**
	 * 		Ajoute une nouvelle representation pour un spectacle.
	 * @param num Numero du spectacle.
	 * @param date Date de la representation.
	 * @param heure Heure de la representation.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static void addRepresentation (int num , String date, int heure) throws RequestException, ConnectionException 
	{
		String str = "INSERT INTO LesRepresentations VALUES ("+num+", to_date( '"+date+" "+heure+"', 'DD/MM/YY HH24'))";
		SQLRequest request = new SQLRequest();
		request.execute(str);
		request.commit();
		request.close();
	}

	/**
	 * Retourne la liste de tous les spectacles.
	 * @return Vector<Spectacle> contenant tous les spectacles.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static Vector<Spectacle> getSpectacles () throws RequestException, ConnectionException 
	{
		Vector<Spectacle> res = new Vector<Spectacle>();
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute("select * from LesSpectacles order by nomS");
		try {
			while (rs.next()) {
				res.addElement(new Spectacle (rs.getInt(1), rs.getString(2)));
			}
		} catch (SQLException e) {
			throw new RequestException (" Erreur dans getSpectacles : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return res;
	}


	/**
	 * Renvoie true si le spectacle dont le numero est passe en argument existe, false sinon
	 * @param numS numero du spectacle 
	 * @return true si le spectacle identifie pas numS existe, false sinon
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static boolean isInSpectacles (int numS) throws RequestException, ConnectionException 
	{
		boolean res = false;
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute("select nomS from LesSpectacles where " + numS + "=numS");

		try {
			res = rs.next();
		} catch (SQLException e) {
			throw new RequestException ("Erreur dans isInSpectacles : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return res;
	}

	/**
	 * Renvoie true si le spectacle identifie par num est programme a la date passee en parametre, false sinon
	 * @param num	numero du spectacle 
	 * @param date	date de la representation 
	 * @return	true si le spectacle est programme a cette date, false sinon
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static boolean existeDateRep (int num, String date, int heure) throws RequestException, ConnectionException
	{
		boolean res = false;
		String str = "select numS, dateRep from LesRepresentations " +
				"  where numS=" + num + " and dateRep = to_date( '"+date+" " + heure + "' , 'DD/MM/YY HH24')";
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try
		{
			res = rs.next();
		} catch(SQLException e)
		{
			throw new RequestException("Erreur dans existeDateRep : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return res;
	}

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
		String str = "select noPlace, noRang, numZ" +
				" from LesPlaces " +
				" natural join" +
				" (select noPlace, noRang" +
				"	from LesPlaces" +
				"	minus" +
				"	select noPlace, noRang from LesTickets where dateRep = to_date('"+date+" "+ heure + "', 'DD/MM/YYYY HH24') and numS = " + numS + ") order by noRang";

		SQLRequest request = new SQLRequest();
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
		request.close();
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

	
	/**
	 * Renvoie la liste des places disponibles pour la representation du spectacle
	 * de numero numS prevu a la date passee en parametre
	 * @param user
	 * @param date date de la representation
	 * @param numS numero du spectacle
	 * @return retour la liste des  places disponibles pour cette representatoion
	 * @throws RequestException
	 * @throws ConnectionException
	 */
	/*public static Place reserverPlace(String date, String numS, int numZ)
		throws RequestException, ConnectionException 
	{
		Vector<Place> res = new Vector<Place>();
		String strPlaces ;
		String strMaxSerie ;
		//String strDateRep ;
		Place placeRes = null;
		
		SQLRequest request = new SQLRequest();
		
		// on verifie que le debut de la representation est bien dans au moins une heure
		strPlaces =	" (select noPlace, noRang, numZ" +
				"	from LesPlaces " +
				"	where numZ=" + numZ + 
				"	minus" +
				"	select noPlace, noRang from LesTickets where dateRep = to_date('"+date+"' , 'DD/MM/YY') and numS = " + numS + ")" +
				" order by noRang";
		

		ResultSet rs = request.execute(strPlaces);
		try
		{
			while (rs.next()) {
				res.addElement(new Place (rs.getInt(1), rs.getInt(2), rs.getInt(3)));
			}
			
			// insertion des LesTickets
			
			String strRemove =	" insert into  ";
			
			// s'il reste des place disponibles
			if (!res.isEmpty())
			{
				
				placeRes = res.get(0);
				
			}

		} catch (SQLException e) {
			throw new RequestException (" Erreur dans l'interrogation des places : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return res;
	}*/
}
