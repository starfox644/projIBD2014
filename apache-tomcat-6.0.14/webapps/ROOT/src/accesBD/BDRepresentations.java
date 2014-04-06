package accesBD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import modele.Representation;
import utils.Utilitaires;
import exceptions.ConnectionException;
import exceptions.RequestException;

public class BDRepresentations 
{
	
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
				res.addElement(new Representation (rs.getString(1), Utilitaires.printDate(rs.getString(2)), rs.getInt(3)));
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
		SQLRequest request = new SQLRequest();
		try
		{
			res = existeDateRep(request, num, date, heure);
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
	
	public static boolean existeDateRep (SQLRequest request, int num, String date, int heure) throws RequestException, ConnectionException
	{
		boolean res = false;
		String str = "select numS, dateRep from LesRepresentations " +
				"  where numS=" + num + " and dateRep = to_date( '"+date+" " + heure + "' , 'DD/MM/YY HH24')";
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
		return res;
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
}
