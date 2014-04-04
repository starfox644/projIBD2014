/*
 * @(#)ProgrammeServlet.java	1.0 2007/10/31
 * 
 * Copyright (c) 2007 Sara Bouchenak.
 */
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import exceptions.*;
import utils.*;
import accesBD.BDRepresentations;
import accesBD.BDRequests;
import accesBD.BDSpectacles;
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

	private static final long serialVersionUID = 1L;
	private static final String invite = "Veuillez saisir un num&eacute;ro de spectacle";
	private static final String formLink = "RepresentationsServlet";

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
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// gestion des parametres de la servlet
		InputParameters parameters = new InputParameters();
		// numero de spectacle entier
		parameters.addParameter("numS", "Num&eacute;ro de spectacle", ParameterType.INTEGER);
		
		int numS;
		SimpleDateFormat formatterOld;
		SimpleDateFormat formatterNew;
		Calendar calendar;
		Date tmpDate;
		String tmpStrDate;
		int heureRep;
		ServletOutputStream out = res.getOutputStream();   

		ErrorLog errorLog = new ErrorLog();
		res.setContentType("text/html");

		/*out.println("<HEAD><TITLE> Liste des repr&eacute;sentations d'un spectacle </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\" " +
				"link=\"#FFFFFF\" vlink=\"#D0D0D0\" alink=\"#E0E0E0\">");
		out.println("<font color=\"#FFFFFF\"><h1> Liste des repr&eacute;sentations d'un spectacle </h1>");*/
		
		//out.println("<p><i><font color=\"#FFFFFF\">");
		
		out.print(HtmlGen.htmlPreambule("Liste des repr&eacute;sentations d'un spectacle"));
		
		parameters.readParameters(req);
		if(parameters.nullParameters())
		{
			out.println("<font color=\"#FFFFFF\"><p> Liste des spectacles existants : </p>");
			try 
			{
				Vector<Spectacle> spec = BDSpectacles.getSpectacles();
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
			out.print(parameters.getHtmlForm(invite, formLink));
			out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");
			out.println("</BODY>");
		} else 
		{
			String nom;
			boolean error = false;
			if(!parameters.validParameters())
			{
				out.print(parameters.getHtmlError());
				out.print(parameters.getHtmlForm(invite, formLink));
				out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");
				out.println("</BODY>");
				out.close();
				return;
			}
			// afficher resultat requete
			numS = parameters.getIntParameter("numS");
			try {
				nom = BDSpectacles.getNomSpectacle(numS);
				// si le nom n'est pas nul, le spectacle existe
				if(nom != null)
				{
					formatterOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
					formatterNew = new SimpleDateFormat(Constantes.dateFormat);
					Vector<Representation> reps = BDRepresentations.getSpectacleRepresentations(numS);
					if(reps.size() == 0)
					{
						out.println("Aucune repr&eacute;sentation pr&eacute;vue. <br>");
					}
					else
					{
						calendar = Calendar.getInstance();
						// affichage des dates de representations
						out.println("<h2>Dates des repr&eacute;sentations de " + nom + " </h2><br>");
						for (Representation r : reps)
						{
							// recuperation d'une string contenant la date sans l'heure
							tmpDate = formatterOld.parse(r.getDate());
							tmpStrDate = formatterNew.format(tmpDate);
							// recuperation de l'heure dans un entier
							calendar.setTime(tmpDate);
							heureRep = calendar.get(Calendar.HOUR_OF_DAY);
							// generation du lien avec les parametres necessaires pour la liste des places
							out.println("<a href=\"PlaceDispoServlet?numS=" + r.getNumero() 
									+ "&date=" + tmpStrDate
									+ "&heure=" + heureRep + "\">" 
									+ Utilitaires.printDate(r.getDate())
									+ "</a><br>");
						}
					}
				}
				else
				{
					out.println("<p><i><font color=\"#FFFFFF\">Ce num&eacute;ro de spectacle n'existe pas</i></p>");
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
			} catch (ParseException e) {
				errorLog.writeException(e);
				error = true;
			}

			if(error)
			{
				out.println("<p><i><font color=\"#FFFFFF\">Impossible d'afficher la liste des repr&eacute;sentations, veuillez r&eacute;essayer utltérieurement.</i></p>");
			}
			out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");
			out.println("</BODY>");
			out.close();
		}
	}

	/*private static void CloseOnError(ServletOutputStream out, String message) throws IOException
	{
		out.println("<p><i><font color=\"#FFFFFF\">" + message + "</i></p>");
		printForm(out);
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
		out.println("</BODY>");
		out.close();
	}*/

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
