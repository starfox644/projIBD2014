package accesBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import exceptions.CategorieException;
import exceptions.ConnectionException;
import exceptions.RequestException;

import modele.Categorie;
import modele.Utilisateur;

public class BDCategories {

	public BDCategories () {
		
	}
	/**
	 * retourne la liste des catégories définies dans la bd
	 * @param Utilisateur
	 * @return Vector<Categorie>
	 * @throws CategorieException
	 * @throws ConnectionException
	 */
	public static Vector<Categorie> getCategories ()
	throws CategorieException, ConnectionException {
		Vector<Categorie> res = new Vector<Categorie>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion();
		
		requete = "select nomc, prix from LesCategories order by nomc";
		System.out.println(requete);
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			while (rs.next()) {
				System.out.println(rs.getString(1));
				res.addElement(new Categorie (rs.getString(1), rs.getFloat(2)));
			}
		} catch (SQLException e) {
			throw new CategorieException (" Problème dans l'interrogation des catégories.."
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
	}
	

	public static Categorie getCategorie(String nomC) throws RequestException, ConnectionException
	{
		Categorie categorie = null;
		SQLRequest request = new SQLRequest();
		categorie = getCategorie(request, nomC);
		request.close();
		return categorie;
	}
	
	public static Categorie getCategorie(SQLRequest request, String nomC) throws RequestException, ConnectionException
	{
		Categorie categorie = null;
		String str = "select nomC, prix from LesCategories where nomC = '" + nomC + "'";
		ResultSet rs = request.execute(str);
		try {
			if(rs.next()) 
			{
				categorie = new Categorie(rs.getString(1), rs.getFloat(2));
			}
		} catch (SQLException e) {
			throw new RequestException ("Erreur dans getCategorie \n"
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		return categorie;
	}
}
