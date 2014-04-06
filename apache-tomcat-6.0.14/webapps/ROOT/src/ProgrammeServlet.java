/*
 * @(#)ProgrammeServlet.java	1.0 2007/10/31
 * 
 * Copyright (c) 2007 Sara Bouchenak.
 */
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Vector;

import accesBD.BDSpectacles;
import exceptions.*;
import utils.*;
import modele.*;

/**
 * Proramme Servlet.
 *
 * This servlet dynamically returns the theater program.
 *
 * @author <a href="mailto:Sara.Bouchenak@imag.fr">Sara Bouchenak</a>
 * @version 1.0, 31/10/2007
 */

public class ProgrammeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	/**
	 * HTTP GET request entry point.
	 *
	 * @param req	an HttpServletRequest object that contains the request 
	 *			the client has made of the servlet
	 * @param res	an HttpServletResponse object that contains the response 
	 *			the servlet sends to the client
	 *
	 * @throws ServletException   if the request for the GET could not be handled
	 * @throws IOException	   if an input or output error is detected 
	 *					   when the servlet handles the GET request
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.print(HtmlGen.htmlPreambule("Programme de la saison"));
		
		/*out.println("<HEAD><TITLE> Programme de la saison </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");
		out.println("<font color=\"#FFFFFF\"><h1> Programme de la saison </h1>");*/

		// TO DO
		// Recuperation de la liste de tous les spectacles de la saison.
		// Puis construction dynamique d'une page web decrivant ces spectacles.
		// afficher resultat requete
		ErrorLog errorLog = new ErrorLog();
		try {
			Vector<Spectacle> spec = BDSpectacles.getSpectacles();
			out.println("<font color=\"#FFFFFF\"><h2>Les spectacles a l'affiche</h2> <br>");
			out.println("<ul>");
			for (Spectacle s : spec)
			{
				out.println("<li>");
			    out.println("<a href=\"RepresentationsServlet?numS=" + s.getNumero() + "\">" 
			    		+ s.getNom() + " : " + s.getNumero()
			    		+ "</a><br>");
				out.println("</li>");
				//out.println(r.getNom() + " : " + r.getDate() + "<br>");
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
		/*out.println("</i></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");*/
		out.println(HtmlGen.PiedPage(req));
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
		return "Retourne le programme du theatre";
	}

}
