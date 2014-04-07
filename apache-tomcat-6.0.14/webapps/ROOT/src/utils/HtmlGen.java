package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HtmlGen 
{
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
