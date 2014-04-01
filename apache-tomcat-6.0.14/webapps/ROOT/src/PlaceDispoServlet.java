/*
 * @(#)NouvelleRepresentationServlet.java	1.0 2007/10/31
 * 
 * Copyright (c) 2007 Sara Bouchenak.
 */
import javax.servlet.*;
import javax.servlet.http.*;

import exceptions.RequestException;

import utils.ErrorLog;
import utils.Utilitaires;

import accesBD.BDRequests;

import modele.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class PlaceDispoServlet extends HttpServlet {

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
		
		String strNumS, dateS, strHeureS;
		int numS, heureS;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.println("<HEAD><TITLE> Places disponibles pour une representation </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");


		ErrorLog errorLog = new ErrorLog();

		out.println("<font color=\"#FFFFFF\"><h1> Recuperer les places disponibles de la representation </h1>");

		strNumS		= req.getParameter("numS");
		dateS		= req.getParameter("date");
		strHeureS		= req.getParameter("heure");
		if (strNumS == null || dateS == null || strHeureS == null) {
			printForm(out);
		} 
		else 
		{
			if(Utilitaires.validIntegerFormat(strHeureS))
			{
				heureS = Integer.parseInt(strHeureS);
			}
			else
			{
				out.close();
				return;
			}
			if(Utilitaires.validIntegerFormat(strNumS))
			{
				numS = Integer.parseInt(strNumS);
			}
			else
			{
				out.close();
				return;
			}
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
						out.println("<br> Cette date de representation n'existe pas <br>");
						printForm(out);
						
					}
				}
				else
				{
					out.println("<br> Ce numero de spectacle n'existe pas<br>");
					printForm(out);
				}
			}
			catch (RequestException e)
			{
				out.println("<p><i><font color=\"#FFFFFF\">Impossible d'acceder a la liste des places disponibles. Veuillez verifier le format de la date</i></p>");
				out.println("<p><i><font color=\"#FFFFFF\">exemple : 01/12/2014</i></p>");
				printForm(out);
				errorLog.writeException(e);
			} 
			catch (Exception e)
			{
				out.println("<p><i><font color=\"#FFFFFF\">Impossible d'acceder a la liste des places disponibles.</i></p>");
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
	
	public static void printForm(ServletOutputStream out) throws IOException
	{
		out.println("<font color=\"#FFFFFF\">Veuillez saisir les informations relatives &agrave; la repr&eacute;sentation :");
		out.println("<P>");
		out.print("<form action=\"");
		out.print("PlaceDispoServlet\" ");
		out.println("method=POST>");
		out.println("Num&eacute;ro de spectacle :");
		out.println("<input type=text size=20 name=numS>");
		out.println("<br>");
		out.println("Date de la repr&eacute;sentation :");
		out.println("<input type=text size=20 name=date>");
		out.println("<br>");
		out.println("Heure de la repr&eacute;sentation :");
		out.println("<input type=text size=20 name=heure>");
		out.println("<br>");
		out.println("<input type=submit>");
		out.println("</form>");
	}

}
