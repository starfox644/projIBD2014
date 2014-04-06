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

	private static final String invite = "Veuillez saisir les informations relatives &agrave; la repr&eacute;sentation";
	private static final String formLink = "ReservationServlet";

	/**
	 * HTTP GET request entry point.
	 *
	 * @param req	an HttpServletRequest object that contains the request 
	 *			the client has made of the servlet
	 * @param res	an HttpServletResponse object that contains the response 
	 *			the servlet sends to the client
	 *
	 * @throws ServletException   if the request for the GET could not be handled
	 * @throws IOException	 if an input or output error is detected 
	 *				 when the servlet handles the GET request
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
		
		if(parameters.nullParameters() )
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
			nomC = parameters.getStringParameter("nomC");
			nbPlaces = parameters.getIntParameter("nbPlaces");
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
							BDPlaces.checkAjoutPanier(numS, dateS, heureS, nbPlaces, categorie, out);
						}
						catch (ReservationException e) 
						{
							out.println("<br>" + e.getMessage() + "<br>");
							success = false;
						}
						if(success)
						{
							HttpSession session = req.getSession();
							Panier panier = (Panier)session.getAttribute("panier");
							if(panier == null)
							{
								panier = new Panier();
								session.setAttribute("panier", panier);
							}
							ContenuPanier contenu = new ContenuPanier(numS, nomS, dateS, heureS, nbPlaces, categorie);
							panier.addContenu(contenu);
							/*out.println("<br> Contenu du panier : <br>");
							out.print(panier.toString());*/
							RequestDispatcher dispatcher = req.getRequestDispatcher("PanierServlet");
							dispatcher.forward(req, res);
						}
					}
					else
					{
						out.println("<br> Cette categorie n'existe pas <br>");
						out.print(parameters.getHtmlForm(invite, formLink));
						out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
						out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
						out.println("</BODY>");
						out.close();
						return;
					}
				}
				else
				{
					out.println("<br> Ce numero de spectacle n'existe pas<br>");
					out.print(parameters.getHtmlForm(invite, formLink));
					out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
					out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
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

		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
		out.println("</BODY>");
		out.close();
	}

	/**
	 * HTTP POST request entry point.
	 *
	 * @param req	an HttpServletRequest object that contains the request 
	 *			the client has made of the servlet
	 * @param res	an HttpServletResponse object that contains the response 
	 *			the servlet sends to the client
	 *
	 * @throws ServletException   if the request for the POST could not be handled
	 * @throws IOException	   if an input or output error is detected 
	 *					   when the servlet handles the POST request
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

