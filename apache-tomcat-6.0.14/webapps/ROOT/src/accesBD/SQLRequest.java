package accesBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exceptions.ConnectionException;
import exceptions.RequestException;

public class SQLRequest 
{
	private String _request;
	private Statement _stmt;
	private ResultSet _rs ;
	private Connection _conn;
	
	/**
	 * 		Cree une nouvelle requete sql a partir d'un String.
	 * @param request String contenant la requete SQL.
	 * @throws ConnectionException 
	 */
	public SQLRequest() throws ConnectionException
	{
		_conn = BDConnexion.getConnexion();
	}
	
	/**
	 * 		Execute la requete SQL et renvoie un ResultSet contenant son resultat.
	 * @return	Un ResultSet correspondant au resultat de la requete.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public ResultSet execute(String request) throws RequestException
	{
		_request = request;
		try {
			_stmt = _conn.createStatement();
			_rs = _stmt.executeQuery(_request);
		} catch (SQLException e) 
		{
			throw new RequestException (e.getMessage()
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		return _rs;
	}
	
	public void commit() throws RequestException
	{
		if(_conn != null)
		{
			try {
				_conn.commit();
			} catch (SQLException e) {
				throw new RequestException (e.getMessage()
						+ "Code Oracle " + e.getErrorCode()
						+ "Message " + e.getMessage());
			}
		}
	}
	
	/**
	 * 		Libere les ressources utilisees par la requete.
	 */
	public void close()
	{
		BDConnexion.FermerTout(_conn, _stmt, _rs);
	}
}