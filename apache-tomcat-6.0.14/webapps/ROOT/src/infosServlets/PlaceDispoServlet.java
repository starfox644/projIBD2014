package infosServlets;

import javax.servlet.*;
import javax.servlet.http.*;

import exceptions.ConnectionException;
import exceptions.RequestException;

import utils.ErrorLog;
import utils.HtmlGen;
import utils.InputParameters;
import utils.ParameterType;

import accesBD.BDPlaces;
import accesBD.BDRepresentations;
import accesBD.BDSpectacles;

import modele.*;

import java.io.IOException;
import java.util.Vector;

public class PlaceDispoServlet extends HttpServlet 
{

	private static final long serialVersionUID = 1L;
	
	private static final String invite = "Veuillez saisir les informations relatives &agrave; la repr&eacute;sentation";
	private static final String formLink = "/PlacesDispo";
	
	/**
	 * 		Entree de la methode get.
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
		InputParameters parameters = new InputParameters();
		parameters.addParameter("numS", "Num&eacute;ro de spectacle", ParameterType.INTEGER);
		parameters.addParameter("date", "Date de repr&eacute;sentation", ParameterType.DATE);
		parameters.addParameter("heure", "Heure de repr&eacute;sentation", ParameterType.HOUR);
		
		String dateS;
		int numS, heureS;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		/*out.println("<HEAD><TITLE> Places disponibles pour une representation </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");*/

		out.print(HtmlGen.htmlPreambule("Places disponibles pour une repr&eacute;sentation"));

		ErrorLog errorLog = new ErrorLog();

		out.println("<font color=\"#FFFFFF\"><h1> Recuperer les places disponibles de la representation </h1>");

		parameters.readParameters(req);
		if(parameters.nullParameters())
		{
			out.print(parameters.getHtmlForm(invite, formLink));
		}
		else 
		{
			if(!parameters.validParameters())
			{
				out.print(parameters.getHtmlError());
				out.print(parameters.getHtmlForm(invite, formLink));
				out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");
				out.println("</BODY>");
				out.close();
				return;
			}
			numS = parameters.getIntParameter("numS");
			dateS = parameters.getStringParameter("date");
			heureS = parameters.getIntParameter("heure");
			try {
				// on verifie que le numero de spectacle existe
				if (BDSpectacles.isInSpectacles(numS))
				{
					// on verifie que la date de la representation existe et est valide
					if (BDRepresentations.existeDateRep (numS,dateS, heureS))
					{
						Vector<Place> list= BDPlaces.getPlacesDispo(numS, dateS, heureS);
						int nb = BDPlaces.getNbPlacesOccupees (numS, dateS, heureS);
						out.println("<br> Il y a " + nb+ " places occupees sur "
								+ BDPlaces.getNbPlacesTotales()+ "  places au total<br>");
						out.println("<br> Voici la liste des places disponibes <br>");
						
						for (Place p : list)
						{
							out.println("<br> noPlace = "+p.getNoPlace()+ ", noRang = "+p.getNoRang()+ 
										" numZ = " + p.getNumZ() + " <br>");
						}
					}
					else
					{
						out.println("Cette date de repr&eacute;sentation n'existe pas. <br><br>");
						out.print(parameters.getHtmlForm(invite, formLink));
						
					}
				}
				else
				{
					out.println("Ce num&eacute;ro de spectacle n'existe pas. <br><br>");
					out.print(parameters.getHtmlForm(invite, formLink));
				}
			}
			catch (RequestException e)
			{
				out.println("<p><i><font color=\"#FFFFFF\">Impossible d'acceder a la liste des places disponibles.</i></p>");
				out.print(parameters.getHtmlForm(invite, formLink));
				errorLog.writeException(e);
			} catch (ConnectionException e) {
				out.println("<p><i><font color=\"#FFFFFF\">Impossible d'acceder a la liste des places disponibles.</i></p>");
				out.print(parameters.getHtmlForm(invite, formLink));
				errorLog.writeException(e);
			} 
		}

		/*out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");*/
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
		return "Ajoute une representation e une date donnee pour un spectacle existant";
	}
	
	/*public static void printForm(ServletOutputStream out) throws IOException
	{
		out.println("<font color=\"#FFFFFF\">Veuillez saisir les informations relatives &agrave; la repr&eacute;sentation :");
		out.println("<P>");
		out.print("<form action=\"");
		out.print("PlaceDispoServlet\" ");
		out.println("method=POST>");
		out.println("Num&eacute;ro de spectacle :");
		out.println("<input type=text size=20 name=numS>");
		out.println("<br>");
		out.println("Date de repr&eacute;sentation :");
		out.println("<input type=text size=20 name=date>");
		out.println("<br>");
		out.println("Heure de la repr&eacute;sentation :");
		out.println("<input type=text size=20 name=heure>");
		out.println("<br>");
		out.println("<input type=submit>");
		out.println("</form>");
	}
	
	private static void CloseOnError(ServletOutputStream out, String message) throws IOException
	{
		out.println("<p><i><font color=\"#FFFFFF\">" + message + "</i></p>");
		printForm(out);
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
		out.println("</BODY>");
		out.close();
	}*/

}
