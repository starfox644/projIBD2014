package application;

import java.sql.Connection;
import java.util.Vector;

import utils.Constantes;
import utils.Utilitaires;



import jus.util.IO;
import modele.Utilisateur;
import modele.Categorie;

import exceptions.ExceptionTheatre;

/**
 * Point d'entree de l'application
 * @author fauvet
 *
 */
public class Theatre {

	/**
	 * Construit le menu
	 * @return liste des entrees du menu
	 */
	private static Vector<String>  Menu () {
		Vector<String> choix = new Vector<String> ();
		choix.add("Quitter");
		choix.add("Consulter les categories tarifaires ");
		choix.add("Ajouter une categorie tarifaire ");
		choix.add("Modifier une categorie tarifaire ");
		choix.add("Executer une requete");
		return choix;
	}

	/**
	 * Gestion du choix de la fonction (depend du role de l'utilisateur)
	 * @param args
	 */
	public static void main(String[] args) {
		IO.setIn(); IO.setOut();
		int choix = 0;
		try {
			Utilisateur user = Utilitaires.Identification();
			if (user != null) {
				do {
					IO.afficher (Constantes.Menu) ;
					choix = IO.lireFromListe(Menu(), Constantes.Invite);
					if (choix == 0) {
						IO.afficherln("au revoir...");
					} else if (choix == 1) {
						/* consulter les categories */
						Utilitaires.AfficherCategories(user);
					} else if (choix == 2) {
						IO.afficherln ("programme a completer");
					} else if (choix == 3) {
						IO.afficherln ("programme a completer");
					}else if (choix == 4) {
						IO.afficherln ("programme a completer");
					}

				} while (choix != 0);
			}
		} catch (Exception e) {
			IO.afficherln("===========");
			IO.afficherln("Au revoir... " + e.getMessage());
		}
	}
}
