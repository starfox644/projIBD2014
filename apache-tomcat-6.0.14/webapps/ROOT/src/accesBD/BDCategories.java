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

/**
 * 		Requetes permettant de recuperer des donnees relatives aux categories.
 */
public class BDCategories {

	public BDCategories () {
		
	}
	
	/**
	 * 		Retourne la liste des categories definies dans la bd.
	 * @return Vector<Categorie> contenant toutes les categories.
	 * 
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
	

	/**
	 * 		Renvoie la categorie correspondant au nom passe en parametre.
	 * @param nomC	Nom de la categorie.
	 * @return	Objet categorie correspondant au nom ou null si aucune categorie ne possede ce nom.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static Categorie getCategorie(String nomC) throws RequestException, ConnectionException
	{
		Categorie categorie = null;
		Transaction request = new Transaction();
		categorie = getCategorie(request, nomC);
		request.close();
		return categorie;
	}
	
	/**
	 * 		Renvoie la categorie correspondant au nom passe en parametre en utilisant
	 * 		la transaction en parametre.
	 * @param nomC	Nom de la categorie.
	 * @return	Objet categorie correspondant au nom ou null si aucune categorie ne possede ce nom.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static Categorie getCategorie(Transaction request, String nomC) throws RequestException, ConnectionException
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
