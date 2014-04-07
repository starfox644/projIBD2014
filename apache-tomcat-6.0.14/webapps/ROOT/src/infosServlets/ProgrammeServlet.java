package infosServlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Vector;

import accesBD.BDSpectacles;
import exceptions.*;
import utils.*;
import modele.*;

/**
 * 		Servlet affichant les spectacles du theatre.
 * 		Chaque spectacle est presente sous forme de lien menant a la liste
 * 		de ses representations.
 */
public class ProgrammeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	/**
	 * 		Entree de la methode get.
	 * 		Genere les liens correspondant aux spectacles du theatre.
	 *
	 * @param req	Objet HttpServletRequest contenant la requete du client.
	 * @param res	Objet HttpServletResponse contenant la reponse a envoyer au client.
	 *
	 * @throws ServletException   Si la requete ne peut pas etre traitee.
	 * @throws IOException	 Si une erreur d'entree / sortie est generee lors du
	 * 						traitement de la requete.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.print(HtmlGen.htmlPreambule("Programme de la saison"));
		
		ErrorLog errorLog = new ErrorLog();
		try {
			// recuperation des spectacles
			Vector<Spectacle> spec = BDSpectacles.getSpectacles();
			out.println("<font color=\"#FFFFFF\"><h2>Les spectacles a l'affiche</h2> <br>");
			out.println("<ul>");
			for (Spectacle s : spec)
			{
				out.println("<li>");
				// generation d'un lien pour chaque spectacle avec son numero
			    out.println("<a href=\"/Representations?numS=" + s.getNumero() + "\">" 
			    		+ s.getNom() + " : " + s.getNumero()
			    		+ "</a><br>");
				out.println("</li>");
			}
			out.println("</ul>");
		}
		catch (ConnectionException e)
		{
			out.println("Impossible de r&eacute;cup&eacute;rer la liste de spectacles.<br>");
			errorLog.writeException(e);
		}
		catch (RequestException e) 
		{
			out.println("Impossible de r&eacute;cup&eacute;rer la liste de spectacles.<br>");
			errorLog.writeException(e);
		}
		out.println(HtmlGen.PiedPage(req));
		out.println("</BODY>");
		out.close();
	}

	/**
	 * 		Entree de la methode post.
	 * 		Redirige vers la methode get.
	 *
	 * @param req	Objet HttpServletRequest contenant la requete du client.
	 * @param res	Objet HttpServletResponse contenant la reponse a envoyer au client.
	 *
	 * @throws ServletException   Si la requete ne peut pas etre traitee.
	 * @throws IOException	 Si une erreur d'entree / sortie est generee lors du
	 * 						traitement de la requete.
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
			{
		doGet(req, res);
			}


	/**
	 * Returns information about this servlet.
	 *
	 * @return String information about this servlet
	 */

	public String getServletInfo() {
		return "Retourne le programme du theatre";
	}

}
