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
	
	public static Vector<String> getSpectacleRepresentations (Utilisateur user, String numS)
			throws CategorieException, ExceptionConnexion {
				Vector<String> res = new Vector<String>();
				String requete ;
				Statement stmt ;
				ResultSet rs ;
				Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());
				
				requete = "select dateRep from LesRepresentations where numS = " + numS + " order by dateRep";
				
				try {
					stmt = conn.createStatement();
					rs = stmt.executeQuery(requete);
					while (rs.next()) {
						System.out.println(rs.getString(1));
						res.addElement(rs.getString(1));
					}
				} catch (SQLException e) {
					throw new CategorieException (" Problème dans l'interrogation des représentations.."
							+ "Code Oracle " + e.getErrorCode()
							+ "Message " + e.getMessage());
				}
				BDConnexion.FermerTout(conn, stmt, rs);
				return res;
			}
	
	/**
	 * 		Permet de recuperer le nom d'un spectacle a partir de son numero.
	 * @param user
	 * @param numS
	 * @return Le nom du spectacle associe au numero, ou null s'il n'y en a pas d'associe au numero.
	 * @throws CategorieException
	 * @throws ExceptionConnexion
	 */
	public static String getNomSpectacle(Utilisateur user, String numS) throws CategorieException, ExceptionConnexion
	{
		String requete ;
		Statement stmt ;
		ResultSet rs ;
		String nom = null;
		Connection conn = BDConnexion.getConnexion(user.getLogin(), user.getmdp());
		
		requete = "select nomS from LesSpectacles where " + numS + "=numS";
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			if(rs.next()) 
			{
				nom = rs.getString(1);
			}
		} catch (SQLException e) {
			throw new CategorieException (" Problème dans l'interrogation des représentations.."
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		BDConnexion.FermerTout(conn, stmt, rs);
		return nom;
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
					
					//requete = "INSERT INTO LesRepresentations VALUES (102, to_date('06/12/1990', 'MM/DD/YY'));";//+ " " + heure + ");";
					requete = "INSERT INTO LesRepresentations VALUES (102, to_date('06/12/90', 'DD/MM/YY'))";
					rs = stmt.executeQuery(requete);
					conn.commit();
					/*while (rs.next()) {
						System.out.println(rs.getString(1));
						res.addElement(new Representation (rs.getString(1), rs.getString(2), rs.getInt(3)));
					}*/
					// ajout dans la table des representations 
					
				} catch (SQLException e) {
					throw new CategorieException (" Erreur dans l'interrogation des représentations : \n"
							+ "Code Oracle : " + e.getErrorCode() + "\n"
							+ "Message : " + e.getMessage() + "\n");
				}
				BDConnexion.FermerTout(conn, stmt, rs);
				//return res;
			}
}
