package accesBD;

import java.sql.ResultSet;
import java.sql.SQLException;

import exceptions.ConnectionException;
import exceptions.RequestException;

/**
 * 		Requetes traitant des informations relatives a la gestion de l'identification
 * 		des utilisateurs.
 */
public class BDLogin 
{
	
	/**
	 * 		Permet de verifier si le couple login / mot de passe est present dans la base
	 * 		pour identifier l'utilisateur.
	 * @param login		Nom de l'utilisateur.
	 * @param passwd	Mot de passe de l'utilisateur.
	 * @return			true si le couple est connu et l'utilisateur peut etre identifie, false sinon.
	 * @throws ConnectionException
	 * @throws RequestException
	 */
	public static boolean isLoginAccepted(String login, String passwd) throws ConnectionException, RequestException
	{
		boolean res = false;
		String str = 
				"select count(login) " +
				"from LesLogins " +
				"where login = '" + login + "' " +
				"and passwd = '" + passwd + "'";
				
		Transaction request = new Transaction();
		ResultSet rs = request.execute(str);
		try
		{
			if (rs.next()) {
				if(rs.getInt(1) == 1)
				{
					res = true;
				}
				else
				{
					res = false;
				}
			}
		}
		catch(SQLException e)
		{
			throw new RequestException ("Erreur dans isLoginAccepted \n"
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		request.close();
		return res;
	}
	
	/**
	 * 		Ajoute un nouveau couple login / mot de passe dans la base pour la creation
	 * 		d'un nouvel utilisateur.
	 * @param login		Nom de l'utilisateur a ajouter.
	 * @param passwd	Mot de passe a associer au nom de l'utilisateur.
	 * @return	false si le nom d'utilisateur est deja present dans la base et
	 * 			n'est pas ajoute, true sinon
	 * @throws ConnectionException
	 * @throws RequestException
	 */
	public static boolean addLogin(String login, String passwd) throws ConnectionException, RequestException
	{
		boolean present = false;
		String str = 
				"select count(login) " +
				"from LesLogins " +
				"where login = '" + login + "' ";
				
		Transaction request = new Transaction();
		ResultSet rs = request.execute(str);
		try
		{
			if (rs.next()) {
				present =(rs.getInt(1) == 1);
			}
			if(!present)
			{
				str = "insert into LesLogins values ('" + login + "', '" + passwd + "')";
				request.execute(str);
				request.commit();
			}
		}
		catch(SQLException e)
		{
			throw new RequestException ("Erreur dans isLoginAccepted \n"
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		request.close();
		return !present;
	}
	
	/**
	 * 		Indique si un nom d'utilisateur est present dans la base en utilisant la transaction
	 * 		passee en parametre.
	 * @param request	Transaction utilisee.
	 * @param login		Nom de l'utilisateur a rechercher.
	 * @return			true si le nom d'utilisateur est present, false sinon.
	 * @throws RequestException
	 */
	public static boolean isLoginPresent(Transaction request, String login) throws RequestException
	{
		boolean present = false;
		String checkReq = 
				"select count(login) " +
				"from LesLogins " +
				"where login = '" + login + "' ";
		ResultSet rs = request.execute(checkReq);
		try
		{
			if (rs.next()) {
				present = (rs.getInt(1) == 1);
			}
		}
		catch(SQLException e)
		{
			throw new RequestException ("Erreur dans isLoginPresent \n"
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		return present;
	}
}
