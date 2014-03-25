/*
 * @(#)NouvelleRepresentationServlet.java	1.0 2007/10/31
 * 
 * Copyright (c) 2007 Sara Bouchenak.
 */
import javax.servlet.*;
import javax.servlet.http.*;
import utils.*;
import accesBD.*;
import modele.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

/**
 * NouvelleRepresentation Servlet.
 *
 * This servlet dynamically adds a new date a show.
 *
 * @author <a href="mailto:Sara.Bouchenak@imag.fr">Sara Bouchenak</a>
 * @version 1.0, 31/10/2007
 */

public class NouvelleRepresentationServlet extends HttpServlet {

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
		
		String numS, dateS, heureS;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.println("<HEAD><TITLE> Ajouter une nouvelle representation </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");


		ErrorLog errorLog = new ErrorLog();
		
		// affichage de la listes des spectacles 
		out.println("<font color=\"#FFFFFF\"><h1> Listes des spectacles existants : </h1>");
		try 
		{
			Utilisateur user = Utilitaires.Identification();
			Vector<Spectacle> spec = BDRequests.getSpectacles(user);
			for (Spectacle s : spec)
			{
				out.println(s.getNom() + " : " + s.getNumero() + "<br>");
			}
		}catch(Exception e)
		{					
			out.println("<p><i><font color=\"#FFFFFF\">Impossible d'ajouter la representation.</i></p>");
			errorLog.writeException(e);
		}
		out.println("<font color=\"#FFFFFF\"><h1> Ajouter une nouvelle repr&eacute;sentation </h1>");

		numS		= req.getParameter("numS");
		dateS		= req.getParameter("date");
		heureS	= req.getParameter("heure");
		if (numS == null || dateS == null || heureS == null) {
			printForm(out);
		} 
		else 
		{
			
			try {
				Utilisateur user = Utilitaires.Identification();
				// on verifie que l'heure est valide
				if (Integer.parseInt(heureS) >= 0 && Integer.parseInt(heureS) <= 23)
				{
					// on verifie que le numero de spectacle existe
					if (BDRequests.isInSpectacles(user, Integer.parseInt(numS)))
					{
						// on verifie que la representation n'est pas deja presente
						if (BDRequests.existeDateRep (user, Integer.parseInt(numS),dateS))
						{
							out.println("<br> Cette date de representation existe deja <br>");
							printForm(out);
						}
						else
						{
							BDRequests.addRepresentation(user, Integer.parseInt(numS), dateS, heureS);
							out.println("<br> ajout de la representation realise <br>");
						}
					}
					else
					{
						out.println("<br> Ce numero de spectacle n'existe pas, veuillez verifier ce dernier dans la liste ci-dessus <br>");
						printForm(out);
					}
				}
				else
				{
					out.println("<br> Heure invalide, impossible d'ajouter la representation. <br>");
					printForm(out);
				}
			}
			catch (SQLException e)
			{
				out.println("<p><i><font color=\"#FFFFFF\">Impossible d'ajouter la representation. Veuillez verifier le format de la date</i></p>");
				out.println("<p><i><font color=\"#FFFFFF\">exemple : 01/12/2014</i></p>");
				printForm(out);
				errorLog.writeException(e);
			} 
			catch (Exception e)
			{
				out.println("<p><i><font color=\"#FFFFFF\">Impossible d'ajouter la representation.</i></p>");
				printForm(out);
				errorLog.writeException(e);
			} 
			// Transformation des parametres vers les types adequats.
			// Ajout de la nouvelle representation.
			// Puis construction dynamique d'une page web de reponse.
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
		out.println("<font color=\"#FFFFFF\">Veuillez saisir les informations relatives &agrave; la nouvelle repr&eacute;sentation :");
		out.println("<P>");
		out.print("<form action=\"");
		out.print("NouvelleRepresentationServlet\" ");
		out.println("method=POST>");
		out.println("Num&eacute;ro de spectacle :");
		out.println("<input type=text size=20 name=numS>");
		out.println("<br>");
		out.println("Date de la repr&eacute;sentation :");
		out.println("<input type=text size=20 name=date>");
		out.println("<br>");
		out.println("Heure de d&eacute;but de la repr&eacute;sentation :");
		out.println("<input type=text size=20 name=heure>");
		out.println("<br>");
		out.println("<input type=submit>");
		out.println("</form>");
	}

}
