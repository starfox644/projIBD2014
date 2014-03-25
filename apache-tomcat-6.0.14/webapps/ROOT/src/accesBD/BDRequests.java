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
	 * Retourne la liste des catégories définies dans la base de donnees
	 * @param Utilisateur
	 * @return Vector<Categorie> liste des representations
	 * @throws CategorieException erreur lors de l'acces a la base
	 * @throws ExceptionConnexion cas d'erreur de connexion 
	 */
	public static Vector<Representation> getRepresentations (Utilisateur user)
			throws ExceptionConnexion, CategorieException
			{
		Vector<Representation> res = new Vector<Representation>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());

		requete = "select nomS, dateRep, numS from LesRepresentations natural join LesSpectacles order by dateRep";

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			while (rs.next()) {
				res.addElement(new Representation (rs.getString(1), Utilitaires.printDate(rs.getString(2)), rs.getInt(3)));
			}
		} catch (SQLException e) {
			throw new CategorieException (" Problème dans l'interrogation des représentations.."
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
			}

	/**
	 * Ajoute une representation du spectacle a la date et l'heure passee en parametre 
	 * @param user
	 * @param num numero du spectacle
	 * @param date date de la representation 
	 * @param heure	heure de la representation 
	 * @throws CategorieException erreur lors de l'acces a la base
	 * @throws ExceptionConnexion en cas de probleme de connexion 
	 */
	public static Vector<String> getSpectacleRepresentations (Utilisateur user, String numS)
			throws CategorieException, ExceptionConnexion {
		Vector<String> res = new Vector<String>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());

		requete = "select dateRep from LesRepresentations where numS = " + numS + " order by dateRep";

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			while (rs.next()) {
				System.out.println(rs.getString(1));
				res.addElement(rs.getString(1));
			}
		} catch (SQLException e) {
			throw new CategorieException (" Problème dans l'interrogation des représentations.."
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
	}

	/**
	 * 		Permet de recuperer le nom d'un spectacle a partir de son numero.
	 * @param user
	 * @param numS
	 * @return Le nom du spectacle associe au numero, ou null s'il n'y en a pas d'associe au numero.
	 * @throws CategorieException
	 * @throws ExceptionConnexion
	 */
	public static String getNomSpectacle(Utilisateur user, String numS) throws CategorieException, ExceptionConnexion
	{
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		String nom = null;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());

		requete = "select nomS from LesSpectacles where " + numS + "=numS";

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			if(rs.next()) 
			{
				nom = rs.getString(1);
			}
		} catch (SQLException e) {
			throw new CategorieException (" Problème dans l'interrogation des représentations.."
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return nom;
	}

	public static void addRepresentation (Utilisateur user, int num , String date, String heure)
			throws CategorieException, ExceptionConnexion 
			{
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());

		try {
			stmt = conn.createStatement();
			requete = "INSERT INTO LesRepresentations VALUES ("+num+", to_date( '"+date+" "+heure+"', 'DD/MM/YY HH24'))";
			rs = stmt.executeQuery(requete);
			conn.commit();
		} catch (SQLException e) {
			throw new CategorieException (" Erreur dans l'interrogation des représentations : \n"
					+ "num =" + num
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		BDConnexion.FermerTout(conn, stmt, rs);
			}

	/**
	 * Retourne la liste des spectacles
	 * @param user
	 * @return liste des spectacles 
	 * @throws CategorieException erreur lors de l'acces a la base
	 * @throws ExceptionConnexion erreur de connexion 
	 */
	public static Vector<Spectacle> getSpectacles (Utilisateur user)
			throws CategorieException, ExceptionConnexion 
			{
		Vector<Spectacle> res = new Vector<Spectacle>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());

		try {
			stmt = conn.createStatement();
			requete = "select * from LesSpectacles";
			rs = stmt.executeQuery(requete);
			conn.commit();

			while (rs.next()) {
				res.addElement(new Spectacle (rs.getInt(1), rs.getString(2)));
			}

		} catch (SQLException e) {
			throw new CategorieException (" Erreur dans l'interrogation des représentations : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
			}


	/**
	 * Renvoie vrai si le spectacle dont le numero est passe en argument existe, faux sinon
	 * @param user
	 * @param numS numero du spectacle 
	 * @return vrai si le spectacle identifie pas numS existe, faux sinon
	 * @throws CategorieException erreur lors de l'acces a la base	
	 * @throws ExceptionConnexion erreur de connexion 
	 */
	public static boolean isInSpectacles (Utilisateur user, int numS)
			throws CategorieException, ExceptionConnexion 
			{
		Vector<String> list = new Vector<String>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		boolean res = false;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());


		try {
			stmt = conn.createStatement();
			requete = "select nomS from LesSpectacles where numS=" + numS;

			rs = stmt.executeQuery(requete);
			while (rs.next()) {
				list.addElement(rs.getString(1));
			}
			res = !list.isEmpty();

		} catch (SQLException e) {
			throw new CategorieException (" Erreur dans l'interrogation des représentations : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
			}

	/**
	 * Renvoie vrai si le spectacle identifie par num est programme a la date passee en parametre, faux sinon
	 * @param user
	 * @param num	numero du spectacle 
	 * @param date	date de la representation 
	 * @return	vrai si le spectacle est programme a cette date, faux sinon
	 * @throws ExceptionConnexion erreur de connexion 
	 * @throws SQLException	erreur lors de l'interogation de la base (format date)
	 */
	public static boolean existeDateRep (Utilisateur user, int num, String date)
			throws ExceptionConnexion, SQLException 
			{
		Vector<String> list = new Vector<String>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		boolean res = false;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());

		stmt = conn.createStatement();
		requete = "select numS, dateRep from LesRepresentations " +
				"  where numS=" + num + " and dateRep = to_date( '"+date+"' , 'DD/MM/YY')";

		rs = stmt.executeQuery(requete);
		while (rs.next()) {
			list.addElement(rs.getString(1));
		}
		res = !list.isEmpty();
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
			}

	/**
	 * Renvoie la liste des places disponibles pour la representation du spectacle
	 * de numero numS prevu a la date passee en parametre
	 * @param user
	 * @param date date de la representation
	 * @param numS numero du spectacle
	 * @return retour la liste des  places disponibles pour cette representatoion
	 * @throws CategorieException
	 * @throws ExceptionConnexion
	 */
	public static Vector<Place> getPlacesDispo (Utilisateur user, String date, String numS)
			throws CategorieException, ExceptionConnexion 
	{
		Vector<Place> res = new Vector<Place>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());

		try {
			stmt = conn.createStatement();
			requete =
					"select noPlace, noRang, numZ" +
					" from LesPlaces " +
					" natural join" +
					" (select noPlace, noRang" +
					"	from LesPlaces" +
					"	minus" +
					"	select noPlace, noRang from LesTickets where dateRep = to_date('"+date+"' , 'DD/MM/YY') and numS = " + numS + ") order by noRang";
			rs = stmt.executeQuery(requete);
			conn.commit();

			while (rs.next()) {
				res.addElement(new Place (rs.getInt(1), rs.getInt(2), rs.getInt(3)));
			}

		} catch (SQLException e) {
			throw new CategorieException (" Erreur dans l'interrogation des places : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
	}
	
	/**
	 * Renvoie le nombre de places occupees pour la representation du spectacle
	 * de numero numS prevu a la date passee en parametre
	 * @param user
	 * @param date date de la representation
	 * @param numS numero du spectacle
	 * @return le nombre de places occupees pour cette representation
	 * @throws CategorieException 
	 * @throws ExceptionConnexion
	 * @throws SQLException
	 */
	public static int getNbPlacesOccupees (Utilisateur user, String date, String numS) 
			throws CategorieException, ExceptionConnexion, SQLException
	{
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Vector<Integer> res = new Vector<Integer>();
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());

		try {
			stmt = conn.createStatement();
			requete = "select count(noPlace) " +
					  "from LesTickets " +
					  "where dateRep = to_date('"+date+"' , 'DD/MM/YY') and numS = " + numS;
		rs = stmt.executeQuery(requete);
			conn.commit();
			while (rs.next()) {
				res.addElement(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new CategorieException (" Erreur dans l'interrogation des places : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res.get(0);
	}
	
	
	/**
	 * Renvoie le nombre de places de la salle de theatre
	 * @param user
	 * @return le nombre de places que contient le theatre
	 * @throws ExceptionConnexion
	 * @throws SQLException
	 */
	public static int getNbPlacesTotales (Utilisateur user)
			throws ExceptionConnexion, SQLException 
	{
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Vector<Integer> res = new Vector<Integer>();
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());

		try {
			stmt = conn.createStatement();
			requete = "select count(noPlace) " +
					  "from LesPlaces ";
		rs = stmt.executeQuery(requete);
			conn.commit();
			while (rs.next()) {
				res.addElement(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new CategorieException (" Erreur dans l'interrogation des places : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res.get(0);
	}

}
