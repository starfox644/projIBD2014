package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 		Parties de pages en html communes a toutes les pages.
 */
public class HtmlGen 
{
	/**
	 * 		Genere un pied de page en html.
	 * 		Affiche les liens a la page d'accueil, au panier et a la liste des spectacles.
	 * 		Si l'utilisateur est loge, affiche son nom et le lien deconnexion, 
	 * 		sinon affiche le lien connexion.
	 * @param req	Requete de la servlet dont le pied de page est genere, pour afficher le nom d'utilisateur.
	 * @return		String contenant le pied de page a renvoyer au format html.
	 * 				La balise /body n'est pas ajoutee.
	 */
	public static String PiedPage(HttpServletRequest req)
	{
		HttpSession session;
		String login;
		session = req.getSession();
		String res = "";
		res += "</i></p>";
		login = (String)session.getAttribute("login");
		res += "<hr>";
		if(login != null)
		{
			res += "<font color=\"#FFFFFF\"> Bienvenue " + login + "<br>"; 
			res += "<p><font color=\"#FFFFFF\"><a href=\"LogoutServlet\">D&eacute;connexion</a></p>";
		}
		else
		{
			res += "<p><font color=\"#FFFFFF\"><a href=\"IdentificationServlet\">Connexion</a></p>";
		}
		res += "<p><font color=\"#FFFFFF\"><a href=\"PanierServlet\">Mon panier</a></p>";
		
		res += "<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>";
		res += "<hr><p><font color=\"#FFFFFF\"><a href=\"ProgrammeServlet\">Liste des spectacles</a></p>";
		
		res += "<hr>";
		return res;
	}
	
	/**
	 * 		Genere un preambule commun a toutes les pages du site.
	 * @param title	Titre de la page.
	 * @return	String contenant le preambule de la page au format html a renvoyer au client.
	 */
	public static String htmlPreambule(String title)
	{
		String res = "";
		res += "<HEAD><TITLE>" + title + "</TITLE></HEAD>";
		res += "<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\" " +
				"link=\"#FFFFFF\" vlink=\"#D0D0D0\" alink=\"#E0E0E0\">";
		res += "<font color=\"#FFFFFF\"><h1>" + title + " </h1>";
		res += "<p><i><font color=\"#FFFFFF\">";
		return res;
	}
}
