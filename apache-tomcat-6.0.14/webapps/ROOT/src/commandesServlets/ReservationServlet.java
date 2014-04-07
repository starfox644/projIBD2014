package commandesServlets;
import javax.servlet.*;
import javax.servlet.http.*;

import panier.ContenuPanier;
import panier.Panier;

import exceptions.ConnectionException;
import exceptions.RequestException;
import exceptions.ReservationException;

import utils.ErrorLog;
import utils.HtmlGen;
import utils.InputParameters;
import utils.ParameterType;

import accesBD.BDCategories;
import accesBD.BDPlaces;
import accesBD.BDSpectacles;

import modele.*;

import java.io.IOException;

public class ReservationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final String invite = "Veuillez saisir les informations relatives &agrave; la repr&eacute;sentation";
	private static final String formLink = "/Reservation";

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
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{

		InputParameters parameters = new InputParameters();
		parameters.addParameter("numS", "Num&eacute;ro de spectacle", ParameterType.INTEGER);
		parameters.addParameter("date", "Date de repr&eacute;sentation", ParameterType.DATE);
		parameters.addParameter("heure", "Heure de repr&eacute;sentation", ParameterType.HOUR);
		parameters.addParameter("nomC", "Cat&eacute;gorie", ParameterType.STRING);
		parameters.addParameter("nbPlaces", "Nombre de places", ParameterType.INTEGER);
		String dateS;
		int numS, heureS, nbPlaces;
		//String SnumS, SheureS, SnbPlaces;
		String nomC;
		boolean success;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		ErrorLog errorLog = new ErrorLog();

		out.print(HtmlGen.htmlPreambule("Reservation pour une representation"));

		parameters.readParameters(req);
		
		
		/* recuperation des parametres */
		/*SnumS		= req.getParameter("numS");
		dateS		= req.getParameter("date");
		SheureS		= req.getParameter("heure");
		nomC		= req.getParameter("nomC");
		SnbPlaces		= req.getParameter("nbPlaces");*/

		if(parameters.nullParameters())
		{
			out.print(parameters.getHtmlForm(invite, formLink));
		}
		/*else if (SnumS == null || dateS == null || SheureS == null)
		//if(parameters.nullParameters())
		{
			out.print(parameters.getHtmlForm(invite, formLink));
		}
		else if (SnumS != null && dateS != null && SheureS != null)
		{
			if (nomC == null && SnbPlaces == null)
			{
				
			}
		}*/
		else
		{
			if(!parameters.validParameters())
			{
				out.print(parameters.getHtmlError());
				out.print(parameters.getHtmlForm(invite, formLink));
				out.println(HtmlGen.PiedPage(req));
				out.println("</BODY>");
				out.close();
				return;
			}
			numS = parameters.getIntParameter("numS");
			dateS = parameters.getStringParameter("date");
			heureS = parameters.getIntParameter("heure");
			nomC = parameters.getStringParameter("nomC");
			nbPlaces = parameters.getIntParameter("nbPlaces");
			nomC = "balcon";
			try 
			{
				String nomS = BDSpectacles.getNomSpectacle(numS);
				// on verifie que le numero de spectacle existe
				if (nomS != null)
				{
					Categorie categorie = BDCategories.getCategorie(nomC);
					success = true;
					if(categorie != null)
					{
						try {
							BDPlaces.checkAjoutPanier(numS, dateS, heureS, nbPlaces, categorie);
						}
						catch (ReservationException e) 
						{
							out.println("<br>" + e.getMessage() + "<br>");
							success = false;
						}
						if(success)
						{
							HttpSession session = req.getSession();
							Panier panier = Panier.getUserPanier(req);
							ContenuPanier contenu = new ContenuPanier(numS, nomS, dateS, heureS, 1, categorie);
							panier.addContenu(contenu);
							RequestDispatcher dispatcher = req.getRequestDispatcher("/Panier");
							dispatcher.forward(req, res);
						}
					}
					else
					{
						out.println("<br> Cette categorie n'existe pas <br>");
						out.print(parameters.getHtmlForm(invite, formLink));
						out.println(HtmlGen.PiedPage(req));
						out.println("</BODY>");
						out.close();
						return;
					}
				}
				else
				{
					out.println("<br> Ce numero de spectacle n'existe pas<br>");
					out.print(parameters.getHtmlForm(invite, formLink));
					out.println(HtmlGen.PiedPage(req));
					out.println("</BODY>");
					out.close();
					return;
				}
			}
			catch (RequestException e)
			{
				out.println("<p><i><font color=\"#FFFFFF\"> Reservation impossible.</i></p>");
				out.print(parameters.getHtmlForm(invite, formLink));
				errorLog.writeException(e);
			} 
			catch (ConnectionException e)
			{
				out.println("<p><i><font color=\"#FFFFFF\"> Reservation impossible.</i></p>");
				out.print(parameters.getHtmlForm(invite, formLink));
				errorLog.writeException(e);
			} 
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
		return "Ajoute une representation e une date donnee pour un spectacle existant";
	}

}

