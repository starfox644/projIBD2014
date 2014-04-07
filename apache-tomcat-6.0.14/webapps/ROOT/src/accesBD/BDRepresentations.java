package accesBD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import modele.Representation;
import utils.Utilitaires;
import exceptions.ConnectionException;
import exceptions.RequestException;

/**
 * 		Requetes permettant de recuperer des informations sur les representations
 * 		ou d'en ajouter.
 */
public class BDRepresentations 
{
	
	/**
	 * 		Retourne la liste de toutes les representations, chacune contenant la date, le nom du spectacle et son numero.
	 * @return Vector<Representation> contenant toutes les representations
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static Vector<Representation> getRepresentations () throws ConnectionException, RequestException
	{
		Vector<Representation> res = new Vector<Representation>();
		String str = "select nomS, dateRep, numS " +
				"from LesRepresentations " +
				"natural join LesSpectacles order by dateRep";
		Transaction request = new Transaction();
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
	 * @param numS Numero du spectacle.
	 * @return Vector<String> contenant les dates de la representation.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
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
		Transaction request = new Transaction();
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
	 * 		Verifie si une certaine representation est programmee a une certaine date
	 * 		a la granularite de l'heure.
	 * @param num	Numero du spectacle.
	 * @param date 	Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param heure Heure du spectacle.
	 * @return	true si la representation spectacle est programme a cette date, false sinon.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static boolean existeDateRep (int num, String date, int heure) throws RequestException, ConnectionException
	{
		boolean res = false;
		Transaction request = new Transaction();
		try
		{
			res = existeDateRep(request, num, date, heure);
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
	 * 		Verifie si une certaine representation est programmee a une certaine date
	 * 		a la granularite de l'heure, en utilisant une transaction cree.
	 * @param request 	Transaction a utiliser.
	 * @param num		Numero du spectacle.
	 * @param date 		Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param heure 	Heure du spectacle.
	 * @return	true si la representation spectacle est programme a cette date, false sinon.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 */
	public static boolean existeDateRep (Transaction request, int num, String date, int heure) throws RequestException
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
	 * @param num 		Numero du spectacle. 
	 * @param date 		Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param heure 	Heure de la representation.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static void addRepresentation (int num , String date, int heure) throws RequestException, ConnectionException 
	{
		String str = "INSERT INTO LesRepresentations VALUES ("+num+", to_date( '"+date+" "+heure+"', 'DD/MM/YY HH24'))";
		Transaction request = new Transaction();
		request.execute(str);
		request.commit();
		request.close();
	}
}
