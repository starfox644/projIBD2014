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
	private LinkedList<Statement> _stmt;
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
		else
		{
			_rs = new LinkedList<ResultSet>();
			_stmt = new LinkedList<Statement>();
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
		Statement stmt;
		_request = request;
		try {
			System.out.println("execute ; conn = " + _conn);
			stmt = _conn.createStatement();
			System.out.println("execute ; stmt = " + _stmt);
			rs = stmt.executeQuery(_request);
			System.out.println("execute ; rs = " + rs);
			_rs.add(rs);
			_stmt.add(stmt);
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
	 * 		les ResultSet retournes et les Statement utilises.
	 */
	public void close()
	{
		for(ResultSet rs : _rs)
		{
			try {
				if(rs != null)
				{
					rs.close();
				}
			} catch (SQLException e) 
			{}
		}
		for(Statement stmt : _stmt)
		{
			try {
				if(stmt != null)
				{
					stmt.close();
				}
			} catch (SQLException e) 
			{}
		}
		if(_conn != null)
		{
			try {
				_conn.close();
			} catch (SQLException e) 
			{}
		}
	}
}
