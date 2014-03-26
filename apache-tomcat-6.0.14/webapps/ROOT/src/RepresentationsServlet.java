/*
 * @(#)ProgrammeServlet.java	1.0 2007/10/31
 * 
 * Copyright (c) 2007 Sara Bouchenak.
 */
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Vector;

import exceptions.*;
import utils.*;
import accesBD.BDRequests;
import modele.*;

/**
 * Proramme Servlet.
 *
 * This servlet dynamically returns the theater program.
 *
 * @author <a href="mailto:Sara.Bouchenak@imag.fr">Sara Bouchenak</a>
 * @version 1.0, 31/10/2007
 */

public class RepresentationsServlet extends HttpServlet {

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
		String strNumS;
		int numS;
		ServletOutputStream out = res.getOutputStream();   

		ErrorLog errorLog = new ErrorLog();
		res.setContentType("text/html");

		out.println("<HEAD><TITLE> Liste des repr&eacute;sentations d'un spectacle </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");
		out.println("<font color=\"#FFFFFF\"><h1> Liste des repr&eacute;sentations d'un spectacle </h1>");
		out.println("<p><i><font color=\"#FFFFFF\">");
		
		strNumS = req.getParameter("numS");
		if (strNumS == null) 
		{
			out.println("<font color=\"#FFFFFF\"><p> Liste des spectacles existants : </p>");
			try 
			{
				Vector<Spectacle> spec = BDRequests.getSpectacles();
				for (Spectacle s : spec)
				{
					out.println(s.getNom() + " : " + s.getNumero() + "<br>");
				}
				out.println("<br>");
			}catch(Exception e)
			{					
				out.println("<p><i><font color=\"#FFFFFF\">Impossible d'afficher la liste des spectacles.</i></p>");
				errorLog.writeException(e);
			}
			printForm(out);
		} else 
		{
			boolean error = false;
			String nom;
			// afficher resultat requete
			try
			{
				numS = Integer.parseInt(strNumS);
			}
			catch(NumberFormatException e)
			{
				error = true;
				CloseOnError(out, "Vous n'avez pas entr&eacute; un num&eacute;ro.");
				return;
			}
			if(!error)
			{
				try {
					nom = BDRequests.getNomSpectacle(numS);
					// si le nom n'est pas nul, le spectacle existe
					if(nom != null)
					{
						Vector<String> reps = BDRequests.getSpectacleRepresentations(numS);
						if(reps.size() == 0)
						{
							out.println("Aucune repr&eacute;sentation pr&eacute;vue. <br>");
						}
						else
						{
							// affichage des dates de representations
							out.println("Dates des repr&eacute;sentations de " + nom + " <br>");
							for (String r : reps)
							{
								out.println(r + "<br>");
							}
						}
					}
					else
					{
						out.println("<p><i><font color=\"#FFFFFF\">Ce num&eacute;ro de spectacle n'existe pas</i></p>");
					}
				} 
				catch (IOException e) 
				{
					errorLog.writeException(e);
					error = true;
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
					out.println("<p><i><font color=\"#FFFFFF\">Erreur dans l'interrogation</i></p>");
				}
				out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");
				out.println("</BODY>");
				out.close();
			}
		}
			}

	private static void printForm(ServletOutputStream out) throws IOException
	{
		out.println("<font color=\"#FFFFFF\">Veuillez saisir un num&eacute;ro de spectacle :");
		out.println("<P>");
		out.print("<form action=\"");
		out.print("RepresentationsServlet\" ");
		out.println("method=POST>");
		out.println("Num&eacute;ro de spectacle :");
		out.println("<input type=text size=20 name=numS>");
		out.println("<br>");
		out.println("<input type=submit>");
		out.println("</form>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");
		out.println("</BODY>");
	}
	
	private static void CloseOnError(ServletOutputStream out, String message) throws IOException
	{
		out.println("<p><i><font color=\"#FFFFFF\">" + message + "</i></p>");
		printForm(out);
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
		return "Retourne le programme du theatre";
	}

}
