package accesBD;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import utils.ErrorLog;
import utils.Utilitaires;
import exceptions.*;
import modele.*;

public class BDRequests 
{

	public BDRequests () 
	{

	}

	/**
	 * Retourne la liste des catégories définies dans la base de donnees
	 * @param Utilisateur
	 * @return Vector<Categorie> liste des representations
	 * @throws CategorieException erreur lors de l'acces a la base
	 * @throws ConnectionException cas d'erreur de connexion 
	 * @throws RequestException 
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
				res.addElement(new Representation (rs.getString(1), Utilitaires.printDate(rs.getString(2)), rs.getInt(3)));
			}
		}
		catch(SQLException e)
		{
			throw new RequestException (e.getMessage()
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		request.close();
		return res;
	}

	/**
	 * Ajoute une representation du spectacle a la date et l'heure passee en parametre 
	 * @param user
	 * @param num numero du spectacle
	 * @param date date de la representation 
	 * @param heure	heure de la representation 
	 * @throws RequestException erreur lors de l'acces a la base
	 * @throws ConnectionException en cas de probleme de connexion 
	 */
	public static Vector<String> getSpectacleRepresentations (String numS)
			throws RequestException, ConnectionException {
		Vector<String> res = new Vector<String>();
		String str = "select dateRep from LesRepresentations where numS = " + numS + " order by dateRep";
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try {		
			while (rs.next()) {
				res.addElement(rs.getString(1));
			}
		} catch (SQLException e) {
			throw new RequestException (" Problème dans l'interrogation des représentations.."
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		request.close();
		return res;
	}

	/**
	 * 		Permet de recuperer le nom d'un spectacle a partir de son numero.
	 * @param user
	 * @param numS
	 * @return Le nom du spectacle associe au numero, ou null s'il n'y en a pas d'associe au numero.
	 * @throws RequestException
	 * @throws ConnectionException
	 */
	public static String getNomSpectacle(String numS) throws RequestException, ConnectionException
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
			throw new RequestException (" Problème dans l'interrogation des représentations.."
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		request.close();
		return nom;
	}

	public static void addRepresentation (int num , String date, String heure) throws RequestException, ConnectionException 
	{
		String str = "INSERT INTO LesRepresentations VALUES ("+num+", to_date( '"+date+" "+heure+"', 'DD/MM/YY HH24'))";
		SQLRequest request = new SQLRequest();
		request.execute(str);
		request.commit();
		request.close();
	}

	/**
	 * Retourne la liste des spectacles
	 * @param user
	 * @return liste des spectacles 
	 * @throws RequestException erreur lors de l'acces a la base
	 * @throws ConnectionException erreur de connexion 
	 */
	public static Vector<Spectacle> getSpectacles () throws RequestException, ConnectionException 
	{
		Vector<Spectacle> res = new Vector<Spectacle>();
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute("select * from LesSpectacles");
		try {
			while (rs.next()) {
				res.addElement(new Spectacle (rs.getInt(1), rs.getString(2)));
			}
		} catch (SQLException e) {
			throw new RequestException (" Erreur dans l'interrogation des représentations : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return res;
	}


	/**
	 * Renvoie vrai si le spectacle dont le numero est passe en argument existe, faux sinon
	 * @param user
	 * @param numS numero du spectacle 
	 * @return vrai si le spectacle identifie pas numS existe, faux sinon
	 * @throws RequestException erreur lors de l'acces a la base	
	 * @throws ConnectionException erreur de connexion 
	 */
	public static boolean isInSpectacles (int numS) throws RequestException, ConnectionException 
	{
		Vector<String> list = new Vector<String>();
		boolean res = false;
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute("select * from LesSpectacles");

		try {
			while (rs.next()) {
				list.addElement(rs.getString(1));
			}
			res = !list.isEmpty();

		} catch (SQLException e) {
			throw new RequestException (" Erreur dans l'interrogation des représentations : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return res;
	}

	/**
	 * Renvoie vrai si le spectacle identifie par num est programme a la date passee en parametre, faux sinon
	 * @param user
	 * @param num	numero du spectacle 
	 * @param date	date de la representation 
	 * @return	vrai si le spectacle est programme a cette date, faux sinon
	 * @throws RequestException erreur de connexion 
	 * @throws ConnectionException	erreur lors de l'interogation de la base (format date)
	 */
	public static boolean existeDateRep (int num, String date) throws RequestException, ConnectionException
	{
		Vector<String> list = new Vector<String>();
		boolean res = false;
		String str = "select numS, dateRep from LesRepresentations " +
				"  where numS=" + num + " and dateRep = to_date( '"+date+"' , 'DD/MM/YY')";
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try
		{
			while (rs.next()) 
			{
				list.addElement(rs.getString(1));
			}
			res = !list.isEmpty();
		} catch(SQLException e)
		{
			throw new RequestException(e.getMessage());
		}
		request.close();
		return res;
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
	public static Vector<Place> getPlacesDispo (String date, String numS) throws RequestException, ConnectionException 
	{
		Vector<Place> res = new Vector<Place>();
		String str = "select noPlace, noRang, numZ" +
				" from LesPlaces " +
				" natural join" +
				" (select noPlace, noRang" +
				"	from LesPlaces" +
				"	minus" +
				"	select noPlace, noRang from LesTickets where dateRep = to_date('"+date+"' , 'DD/MM/YY') and numS = " + numS + ") order by noRang";

		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try {
			while (rs.next()) {
				res.addElement(new Place (rs.getInt(1), rs.getInt(2), rs.getInt(3)));
			}

		} catch (SQLException e) {
			throw new RequestException (" Erreur dans l'interrogation des places : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return res;
	}

	/**
	 * Renvoie le nombre de places occupees pour la representation du spectacle
	 * de numero numS prevu a la date passee en parametre
	 * @param user
	 * @param date date de la representation
	 * @param numS numero du spectacle
	 * @return le nombre de places occupees pour cette representation
	 * @throws RequestException 
	 * @throws ConnectionException
	 */
	public static int getNbPlacesOccupees (Utilisateur user, String date, String numS) throws ConnectionException, RequestException
	{
		Vector<Integer> res = new Vector<Integer>();

		String str = "select count(noPlace) " +
				"from LesTickets " +
				"where dateRep = to_date('"+date+"' , 'DD/MM/YY') and numS = " + numS;

		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try {
			while (rs.next()) {
				res.addElement(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new RequestException (" Erreur dans l'interrogation des places : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		request.close();
		return res.get(0);
	}


	/**
	 * Renvoie le nombre de places de la salle de theatre
	 * @param user
	 * @return le nombre de places que contient le theatre
	 * @throws ConnectionException
	 * @throws RequestException
	 */
	public static int getNbPlacesTotales (Utilisateur user)	throws ConnectionException, RequestException
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
			throw new RequestException (" Erreur dans l'interrogation des places : \n"
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
