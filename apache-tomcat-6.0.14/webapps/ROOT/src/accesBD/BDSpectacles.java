package accesBD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import modele.Spectacle;
import exceptions.ConnectionException;
import exceptions.RequestException;

public class BDSpectacles
{
	/**
	 * Retourne la liste de tous les spectacles.
	 * @return Vector<Spectacle> contenant tous les spectacles.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static Vector<Spectacle> getSpectacles () throws RequestException, ConnectionException 
	{
		Vector<Spectacle> res = new Vector<Spectacle>();
		Transaction request = new Transaction();
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
		Transaction request = new Transaction();
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
	 * 		Permet de recuperer le nom d'un spectacle a partir de son numero.
	 * @param numS Numero du spectacle dont on veut recuperer le nom.
	 * @return String contenant le nom du spectacle associe au numero, ou null s'il n'y en a pas d'associe au numero.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static String getNomSpectacle(int numS) throws RequestException, ConnectionException
	{
		String nom;
		Transaction request = new Transaction();
		try{
			nom = getNomSpectacle(request, numS);
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
		return nom;
	}
	
	/**
	 * 		Permet de recuperer le nom d'un spectacle a partir de son numero.
	 * @param numS Numero du spectacle dont on veut recuperer le nom.
	 * @return String contenant le nom du spectacle associe au numero, ou null s'il n'y en a pas d'associe au numero.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static String getNomSpectacle(Transaction request, int numS) throws RequestException, ConnectionException
	{
		String nom = null;
		String str = "select nomS from LesSpectacles where " + numS + "=numS";
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
		return nom;
	}
}
