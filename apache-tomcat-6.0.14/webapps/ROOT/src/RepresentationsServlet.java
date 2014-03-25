/*
 * @(#)ProgrammeServlet.java	1.0 2007/10/31
 * 
 * Copyright (c) 2007 Sara Bouchenak.
 */
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;

import exceptions.CategorieException;
import exceptions.ExceptionConnexion;
import exceptions.ExceptionUtilisateur;

import utils.ErrorLog;
import utils.Utilitaires;

import accesBD.BDRequests;

import modele.Representation;
import modele.Utilisateur;

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
		String numS;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.println("<HEAD><TITLE> Liste des representations </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");
		out.println("<font color=\"#FFFFFF\"><h1> Liste des representations </h1>");
		out.println("<p><i><font color=\"#FFFFFF\">");

		numS = req.getParameter("numS");
		if (numS == null) 
		{
			printForm(out);
		} else 
		{
			boolean error = false;
			String nom;
			// afficher resultat requete
			Utilisateur user;
			ErrorLog errorLog = new ErrorLog();
			try
			{
				Integer.parseInt(numS);
			}
			catch(NumberFormatException e)
			{
				error = true;
				out.println("<p><i><font color=\"#FFFFFF\">Vous n'avez pas entr&eacute; un nombre.</i></p>");
				printForm(out);
			}
			if(!error)
			{
				try {
					user = Utilitaires.Identification();
					nom = BDRequests.getNomSpectacle(user, numS);
					// si le nom n'est pas nul, le spectacle existe
					if(nom != null)
					{
						Vector<String> reps = BDRequests.getSpectacleRepresentations(user, numS);
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
				} catch (ExceptionConnexion e)
				{
					errorLog.writeException(e);
					error = true;
				}
				catch (ExceptionUtilisateur e)
				{
					errorLog.writeException(e);
					error = true;
				}
				catch (CategorieException e)
				{
					errorLog.writeException(e);
					error = true;
				}
				catch (IOException e) 
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

	private void printForm(ServletOutputStream out) throws IOException
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
