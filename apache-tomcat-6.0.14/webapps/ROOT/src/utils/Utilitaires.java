package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Vector;

import jus.util.IO;

import accesBD.BDCategories;
import accesBD.BDConnexion;

import modele.Utilisateur;
import modele.Categorie;
import exceptions.ExceptionUtilisateur;
import exceptions.ConnectionException;
import exceptions.CategorieException;


/**
 * les operations de l'application
 * 
 * @author fauvet
 * 
 */
public class Utilitaires {

	public final static String dateFormat = "dd/MM/yyyy";
	
	public Utilitaires() {
	}

	/**
	 * Affiche les categories du theatre avec pour chacune son prix
	 * 
	 * @param user
	 *            l'utilisateur identifie
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public static void AfficherCategories() throws IOException {
		Vector<Categorie> res = new Vector<Categorie>();
		try {
			IO.afficherln("===================");
			IO.afficherln("Listes des categories tarifaires");
			res = BDCategories.getCategorie();
			if (res.isEmpty()) {
				IO.afficherln(" Liste vide ");
			} else {
				for (int i = 0; i < res.size(); i++) {
					IO.afficherln(res.elementAt(i).getCategorie() + " (prix : "
							+ res.elementAt(i).getPrix() + ")");
				}
			}
			IO.afficherln("===================");
		} catch (CategorieException e) {
			IO.afficherln(" Erreur dans l'affichage des categories : "
					+ e.getMessage());
		} catch (ConnectionException e) {
			IO.afficherln(" Erreur dans l'affichage des categories : "
					+ e.getMessage());
		}

	}

	/**
	 * effectue la connexion pour l'utilisateur
	 * 
	 * @return l'oid de l'objet utilisateur
	 * @throws ExceptionUtilisateur
	 */
	public static Utilisateur Identification() throws ConnectionException,
			ExceptionUtilisateur, IOException {
		Utilisateur user = null;
		String login;
		String passwd;
		// lecture des parametres de connexion dans connection.conf
		Properties p = new Properties();
		InputStream is = null;
		is = new FileInputStream(utils.Constantes.getConfigPath());
		p.load(is);
		login = p.getProperty("user");
		passwd = p.getProperty("mdp");
		/*if (login == null || login.equals("MYUSERNAME")) {
			UserNamePasswordDialog login_dialog = new UserNamePasswordDialog(
					new Frame(""));
			login_dialog.setVisible(true);
			login = login_dialog.getUid();
			passwd = login_dialog.getPwd();
		}*/
		/* test de la connexion */
		System.out.println("avant connexion");
		Connection conn = BDConnexion.getConnexion();
		if (conn != null) {
			IO.afficherln("Connexion reussie...");
			System.out.println("connexion ok");
			BDConnexion.FermerTout(conn, null, null);
			user = new Utilisateur(login, passwd);
		} else {
			System.out.println("connexion impossible");
			throw new ConnectionException("Connexion impossible\n");
		}
		return user;
	}
	
	public static String printMonth(int m)
	{
		String month;
		switch (m)
		{
			case 1 : 
				month = "Janvier";
				break;
			case 2 : 
				month = "Fevrier";
				break;
			case 3 : 
				month = "Mars";
				break;
			case 4 : 
				month = "Avril";
				break;
			case 5 : 
				month = "Mai";
				break;
			case 6 : 
				month = "Juin";
				break;	
			case 7 : 
				month = "Juillet";
				break;
			case 8 : 
				month = "Aout";
				break;
			case 9 : 
				month = "Septembre";
				break;
			case 10 : 
				month = "Octobre";
				break;
			case 11 : 
				month = "Novembre";
				break;
			case 12 : 
				month = "Decembre";
				break;
			default : 
				month = "";
				break;
		}
		return month;
		
	}
	
	public static String printDate(String date)
	{
		String[] d = date.split(" ");
		String[] ymd = d[0].split("-");
		String[] h = d[1].split(":");
		String newdate = ymd[2] + " "+ printMonth(Integer.parseInt(ymd[1]))+ " " + ymd[0] + " a " + h[0] + "H";
		return newdate;
	}
	
	public static boolean validDateFormat(String date)
	{
		boolean res = true;
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		try
		{
			formatter.parse(date);
		}
		catch(ParseException e)
		{
			res = false;
		}
		return res;
	}
	
	public static boolean validIntegerFormat(String integer)
	{
		boolean res = true;
		try
		{
			Integer.parseInt(integer);
		}
		catch(NumberFormatException e)
		{
			res = false;
		}
		return res;
	}
}
