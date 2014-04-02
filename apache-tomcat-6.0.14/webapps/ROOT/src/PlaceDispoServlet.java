/*
 * @(#)NouvelleRepresentationServlet.java	1.0 2007/10/31
 * 
 * Copyright (c) 2007 Sara Bouchenak.
 */
import javax.servlet.*;
import javax.servlet.http.*;

import exceptions.ConnectionException;
import exceptions.RequestException;

import utils.ErrorLog;
import utils.InputParameters;
import utils.ParameterType;
import utils.Utilitaires;

import accesBD.BDRequests;

import modele.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class PlaceDispoServlet extends HttpServlet {

	private static final String invite = "Veuillez saisir les informations relatives &agrave; la repr&eacute;sentation";
	private static final String formLink = "PlaceDispoServlet";
	
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

		out.println("<HEAD><TITLE> Places disponibles pour une representation </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");


		ErrorLog errorLog = new ErrorLog();

		out.println("<font color=\"#FFFFFF\"><h1> Recuperer les places disponibles de la representation </h1>");

		boolean success = parameters.readParameters(req);
		if(parameters.nullParameters())
		{
			out.print(parameters.getHtmlForm(invite, formLink));
		}
		else 
		{
			if(!success)
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
				if (BDRequests.isInSpectacles(numS))
				{
					// on verifie que la date de la representation existe et est valide
					if (BDRequests.existeDateRep (numS,dateS, heureS))
					{
						Vector<Place> list= BDRequests.getPlacesDispo(numS, dateS, heureS);
						int nb = BDRequests.getNbPlacesOccupees (numS, dateS, heureS);
						out.println("<br> Il y a " + nb+ " places occupees sur "
								+ BDRequests.getNbPlacesTotales()+ "  places au total<br>");
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
