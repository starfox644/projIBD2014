package accesBD;

import java.sql.ResultSet;
import java.sql.SQLException;

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
