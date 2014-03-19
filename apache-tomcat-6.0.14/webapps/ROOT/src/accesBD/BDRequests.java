package accesBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import exceptions.CategorieException;
import exceptions.ExceptionConnexion;

import modele.Representation;
import modele.Utilisateur;

public class BDRequests {

	public BDRequests () {
		
	}
	/**
	 * retourne la liste des catégories définies dans la bd
	 * @param Utilisateur
	 * @return Vector<Categorie>
	 * @throws CategorieException
	 * @throws ExceptionConnexion
	 */
	public static Vector<Representation> getRepresentations (Utilisateur user)
	throws CategorieException, ExceptionConnexion {
		Vector<Representation> res = new Vector<Representation>();
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());
		
		requete = "select nomS, dateRep, numS from LesRepresentations natural join LesSpectacles order by dateRep";
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			while (rs.next()) {
				System.out.println(rs.getString(1));
				res.addElement(new Representation (rs.getString(1), rs.getString(2), rs.getInt(3)));
			}
		} catch (SQLException e) {
			throw new CategorieException (" Problème dans l'interrogation des représentations.."
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return res;
	}
	
	public static void addRepresentation (Utilisateur user, int num , String date, String heure)
			throws CategorieException, ExceptionConnexion {
				//Vector<Representation> res = new Vector<Representation>();
				String requete ;
				Statement stmt ;
				ResultSet rs ;
				Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());
				
				
				try {
					stmt = conn.createStatement();
					// construction de la requete a partir des paramtres
					//requete = "INSERT INTO LesRepresentations VALUES (";
					//requete += "'" + num + "'" + ",to_date('" + date + "', 'MM/DD/YY'));";//+ " " + heure + ");";
					
					requete = "INSERT INTO LesRepresentations VALUES (102, to_date('06/12/1990', 'MM/DD/YY'));";//+ " " + heure + ");";
					
					rs = stmt.executeQuery(requete);
					/*while (rs.next()) {
						System.out.println(rs.getString(1));
						res.addElement(new Representation (rs.getString(1), rs.getString(2), rs.getInt(3)));
					}*/
					// ajout dans la table des representations 
					
				} catch (SQLException e) {
					throw new CategorieException (" Problème dans l'interrogation des représentations.."
							+ "Code Oracle " + e.getErrorCode()
							+ "Message " + e.getMessage());
				}
				BDConnexion.FermerTout(conn, stmt, rs);
				//return res;
			}
}
