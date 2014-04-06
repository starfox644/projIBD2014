package accesBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import exceptions.ConnectionException;
import exceptions.RequestException;

public class Transaction 
{
	private String _request;
	private Statement _stmt;
	private LinkedList<ResultSet> _rs;
	private Connection _conn;
	
	/**
	 * 		Cree un nouvel objet permettant d'effectuer des requetes SQL.
	 * @throws ConnectionException 
	 */
	public Transaction() throws ConnectionException
	{
		_conn = BDConnexion.getConnexion();
		if(_conn == null)
		{
			throw new ConnectionException("Unable to connect to the database.");
		}
	}
	
	/**
	 * 		Execute la requete SQL et renvoie un ResultSet contenant son resultat.
	 * @param String requete a executer
	 * @return	Un ResultSet correspondant au resultat de la requete.
	 * @throws RequestException		Si une erreur dans la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public ResultSet execute(String request) throws RequestException
	{
		ResultSet rs;
		_request = request;
		try {
			_stmt = _conn.createStatement();
			rs = _stmt.executeQuery(_request);
			_rs.add(rs);
		} catch (SQLException e) 
		{
			throw new RequestException ("Erreur d'execution de la requete : \n"
					+ request + "\n"
					+ "Code Oracle : " + e.getErrorCode()
					+ "\nMessage : " + e.getMessage());
		}
		return rs;
	}
	
	/**
	 * 		Valide la transaction associee a la connexion.
	 * @throws RequestException
	 */
	public void commit() throws RequestException
	{
		if(_conn != null)
		{
			try {
				_conn.commit();
			} catch (SQLException e) {
				throw new RequestException (e.getMessage()
						+ "Code Oracle : " + e.getErrorCode()
						+ "\nMessage : " + e.getMessage());
			}
		}
	}
	
	/**
	 * 		Annule la transaction associee a la connexion.
	 * @throws RequestException
	 */
	public void rollback() throws RequestException
	{

		if(_conn != null)
		{
			try {
				_conn.rollback();
			} catch (SQLException e) {
				throw new RequestException (e.getMessage()
						+ "Code Oracle : " + e.getErrorCode()
						+ "\nMessage : " + e.getMessage());
			}
		}
	}
	
	/**
	 * 		Libere les ressources utilisees pour les requetes effectuees, y compris
	 * 		les ResultSet retournes.
	 */
	public void close()
	{
		for(ResultSet rs : _rs)
		{
			try {
				rs.close();
			} catch (SQLException e) 
			{}
		}
		BDConnexion.FermerTout(_conn, _stmt, null);
	}
}
