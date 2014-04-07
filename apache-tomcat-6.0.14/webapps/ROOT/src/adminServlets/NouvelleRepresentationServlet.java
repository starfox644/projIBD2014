package adminServlets;

import javax.servlet.*;
import javax.servlet.http.*;

import exceptions.ConnectionException;
import exceptions.RequestException;
import utils.*;
import accesBD.*;
import modele.*;
import java.io.IOException;
import java.util.Vector;

/**
 * 		Servlet creant une page d'ajout de nouvelle representation.
 */
public class NouvelleRepresentationServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	private static final String invite = "Veuillez saisir les informations relatives &agrave; la nouvelle repr&eacute;sentation";
	private static final String formLink = "/admin/NouvelleRepresentation";
	
	/**
	 * 		Entree de la methode get.
	 * 		Genere un formulaire permettant d'entrer les informations de la nouvelle
	 * 		representation, puis ajoute celle-ci si ces informations sont valides.
	 *
	 * @param req	Objet HttpServletRequest contenant la requete du client.
	 * @param res	Objet HttpServletResponse contenant la reponse a envoyer au client.
	 *
	 * @throws ServletException   Si la requete ne peut pas etre traitee.
	 * @throws IOException	 Si une erreur d'entree / sortie est generee lors du
	 * 						traitement de la requete.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// gestion des parametres de la servlet
		InputParameters parameters = new InputParameters();
		parameters.addParameter("numS", "Num&eacute;ro de spectacle", ParameterType.INTEGER);
		parameters.addParameter("date", "Date de repr&eacute;sentation", ParameterType.DATE);
		parameters.addParameter("heure", "Heure de repr&eacute;sentation", ParameterType.HOUR);

		String dateS;
		int numS = 0;
		int heureS = 0;
		ServletOutputStream out = res.getOutputStream();  
		boolean error = false;

		res.setContentType("text/html");

		out.println("<HEAD><TITLE> Ajouter une nouvelle representation </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");


		ErrorLog errorLog = new ErrorLog();

		// affichage de la listes des spectacles 
		out.println("<font color=\"#FFFFFF\"><p> Liste des spectacles existants : </p>");
		try 
		{
			Vector<Spectacle> spec = BDSpectacles.getSpectacles();
			for (Spectacle s : spec)
			{
				out.println(s.getNom() + " : " + s.getNumero() + "<br>");
			}
		}catch(Exception e)
		{					
			out.println("<p><i><font color=\"#FFFFFF\">Impossible d'afficher la liste des spectacles.</i></p>");
			errorLog.writeException(e);
		}
		out.println("<font color=\"#FFFFFF\"><h1> Ajouter une nouvelle repr&eacute;sentation </h1>");

		// recuperation et traitement des parametres
		parameters.readParameters(req);
		if(parameters.nullParameters())
		{
			// affichage du formulaire si les parametres sont absents
			out.print(parameters.getHtmlForm(invite, formLink));
		}
		else 
		{
			if(!parameters.validParameters())
			{
				// parametres invalides, affichage d'une erreur et du formulaire
				out.print(parameters.getHtmlError());
				out.print(parameters.getHtmlForm(invite, formLink));
				out.println(HtmlGen.PiedPage(req));
				out.println("</BODY>");
				out.close();
				return;
			}
			// recuperation des parametres dans leurs bons formats
			numS = parameters.getIntParameter("numS");
			dateS = parameters.getStringParameter("date");
			heureS = parameters.getIntParameter("heure");
			try
			{
				// on verifie que le numero de spectacle existe
				if (BDSpectacles.isInSpectacles(numS))
				{
					// on verifie que la representation n'est pas deja presente
					if (BDRepresentations.existeDateRep(numS,dateS, heureS))
					{
						out.println("<br><i> Ce spectacle est d&eacute;ja pr&eacute;vu a cette heure, impossible de l'ajouter. </i></p>");
						out.print(parameters.getHtmlForm(invite, formLink));
					}
					else
					{
						// ajout de la representation
						BDRepresentations.addRepresentation(numS, dateS, heureS);
						out.println("<br> Ajout de la repr&eacute;sentation r&eacute;alis&eacute;e <br>");
					}
				}
				else
				{
					out.println("<br><i> Ce num&eacute;ro de spectacle n'existe pas, veuillez v&eacute;rifier ce dernier dans la liste ci-dessus. </i></p>");
					out.print(parameters.getHtmlForm(invite, formLink));
				}
			}
			catch (ConnectionException e)
			{
				errorLog.writeException(e);
				error = true;
			}
			catch (RequestException e)
			{
				errorLog.writeException(e);
				error = true;
			}

			if(error)
			{
				out.println("<p><i><font color=\"#FFFFFF\">Erreur interne du serveur, veuillez r&eacute;essayer utltérieurement.</i></p>");
			}
			out.println(HtmlGen.PiedPage(req));
			out.println("</BODY>");
			out.close();
		}
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
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
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
}
