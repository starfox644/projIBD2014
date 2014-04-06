package accesBD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import modele.Representation;
import utils.Utilitaires;
import exceptions.ConnectionException;
import exceptions.RequestException;

public class BDLogin 
{
	public static boolean isLoginAccepted(String login, String passwd) throws ConnectionException, RequestException
	{
		boolean res = false;
		String str = 
				"select count(login) " +
				"from LesLogins " +
				"where login = '" + login + "' " +
				"and passwd = '" + passwd + "'";
				
		SQLRequest request = new SQLRequest();
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
	
	public static boolean addLogin(String login, String passwd) throws ConnectionException, RequestException
	{
		boolean present = false;
		String str = 
				"select count(login) " +
				"from LesLogins " +
				"where login = '" + login + "' ";
				
		SQLRequest request = new SQLRequest();
		ResultSet rs = request.execute(str);
		try
		{
			if (rs.next()) {
				if(rs.getInt(1) == 1)
				{
					present = true;
				}
				else
				{
					present = false;
				}
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
	
	public static boolean isLoginPresent(SQLRequest request, String login) throws RequestException
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
