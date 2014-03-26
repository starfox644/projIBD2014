package accesBD;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import javax.servlet.ServletOutputStream;

import utils.ErrorLog;
import utils.Utilitaires;

import exceptions.CategorieException;
import exceptions.ConnectionException;
import exceptions.ExceptionUtilisateur;
import exceptions.RequestException;

/*import modele.Representation;
import modele.Utilisateur;*/
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
	public static Vector<Representation> getRepresentations ()
			throws ConnectionException, RequestException
			{
		try {
			ErrorLog errorLog = new ErrorLog();
		} catch (IOException e2) 
		{
			throw new ConnectionException(e2.getMessage());
		}
		Vector<Representation> res = new Vector<Representation>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion();

		requete = "select nomS, dateRep, numS from LesRepresentations natural join LesSpectacles order by dateRep";

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			while (rs.next()) {
				res.addElement(new Representation (rs.getString(1), Utilitaires.printDate(rs.getString(2)), rs.getInt(3)));
			}
		} catch (SQLException e) {
			throw new RequestException (" Problème dans l'interrogation des représentations.."
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
	 * @throws ConnectionException en cas de probleme de connexion 
	 */
	public static Vector<String> getSpectacleRepresentations (String numS)
			throws RequestException, ConnectionException {
		Vector<String> res = new Vector<String>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion();

		requete = "select dateRep from LesRepresentations where numS = " + numS + " order by dateRep";

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			while (rs.next()) {
				System.out.println(rs.getString(1));
				res.addElement(rs.getString(1));
			}
		} catch (SQLException e) {
			throw new RequestException (" Problème dans l'interrogation des représentations.."
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
	 * @throws ConnectionException
	 */
	public static String getNomSpectacle(String numS) throws RequestException, ConnectionException
	{
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		String nom = null;
		Connection conn = BDConnexion.getConnexion();

		requete = "select nomS from LesSpectacles where " + numS + "=numS";

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			if(rs.next()) 
			{
				nom = rs.getString(1);
			}
		} catch (SQLException e) {
			throw new RequestException (" Problème dans l'interrogation des représentations.."
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return nom;
	}

	public static void addRepresentation (int num , String date, String heure)
			throws RequestException, ConnectionException 
			{
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion();

		try {
			stmt = conn.createStatement();
			requete = "INSERT INTO LesRepresentations VALUES ("+num+", to_date( '"+date+" "+heure+"', 'DD/MM/YY HH24'))";
			rs = stmt.executeQuery(requete);
			conn.commit();
		} catch (SQLException e) {
			throw new RequestException (" Erreur dans l'interrogation des représentations : \n"
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
	 * @throws ConnectionException erreur de connexion 
	 */
	public static Vector<Spectacle> getSpectacles ()
			throws RequestException, ConnectionException 
			{
		Vector<Spectacle> res = new Vector<Spectacle>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion();

		try {
			stmt = conn.createStatement();
			requete = "select * from LesSpectacles";
			rs = stmt.executeQuery(requete);
			conn.commit();

			while (rs.next()) {
				res.addElement(new Spectacle (rs.getInt(1), rs.getString(2)));
			}

		} catch (SQLException e) {
			throw new RequestException (" Erreur dans l'interrogation des représentations : \n"
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
	 * @throws ConnectionException erreur de connexion 
	 */
	public static boolean isInSpectacles (int numS)
			throws RequestException, ConnectionException 
			{
		Vector<String> list = new Vector<String>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		boolean res = false;
		Connection conn = BDConnexion.getConnexion();


		try {
			stmt = conn.createStatement();
			requete = "select nomS from LesSpectacles where numS=" + numS;

			rs = stmt.executeQuery(requete);
			while (rs.next()) {
				list.addElement(rs.getString(1));
			}
			res = !list.isEmpty();

		} catch (SQLException e) {
			throw new RequestException (" Erreur dans l'interrogation des représentations : \n"
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
	 * @throws ConnectionException erreur de connexion 
	 * @throws SQLException	erreur lors de l'interogation de la base (format date)
	 */
	public static boolean existeDateRep (int num, String date)
			throws RequestException, ConnectionException
			{
		Vector<String> list = new Vector<String>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		boolean res = false;
		Connection conn;
		conn = BDConnexion.getConnexion();
		try
		{
			stmt = conn.createStatement();
			requete = "select numS, dateRep from LesRepresentations " +
					"  where numS=" + num + " and dateRep = to_date( '"+date+"' , 'DD/MM/YY')";
	
			rs = stmt.executeQuery(requete);
			while (rs.next()) {
				list.addElement(rs.getString(1));
			}
			res = !list.isEmpty();
		} catch(SQLException e)
		{
			throw new RequestException(e.getMessage());
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
			}

	/**
	 *   Requete de places libres
	 *   
	 *   
	 */
	public static Vector<Integer> getPlacesDispo (String date, String numS)
			throws RequestException, ConnectionException 
			{
		Vector<Integer> res = new Vector<Integer>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion();

		try {
			stmt = conn.createStatement();
			requete = "select noPlace, noRang" +
					"	from LesPlaces" +
					"	minus" +
					"	select noPlace, noRang" +
					"	from LesTickets" +
					"	where dateRep = to_date('" + date + "', 'DD-MM-YY')" +
					"	and numS = " + numS;
			rs = stmt.executeQuery(requete);
			conn.commit();

			while (rs.next()) {
				//res.addElement(new Spectacle (rs.getInt(1), rs.getString(2)));
			}

		} catch (SQLException e) {
			throw new RequestException (" Erreur dans l'interrogation des places : \n"
					+ "Code Oracle : " + e.getErrorCode() + "\n"
					+ "Message : " + e.getMessage() + "\n");
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
			}


}
