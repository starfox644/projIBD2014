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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

		// Transformation des parametres vers les types adequats.
		// Ajout de la nouvelle representation.
		// Puis construction dynamique d'une page web de reponse.
		String strNumS, dateS, strHeureS;
		int numS = 0;
		int heureS = 0;
		ServletOutputStream out = res.getOutputStream();  

		res.setContentType("text/html");

		out.println("<HEAD><TITLE> Ajouter une nouvelle representation </TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");


		ErrorLog errorLog = new ErrorLog();
		
		// affichage de la listes des spectacles 
		out.println("<font color=\"#FFFFFF\"><p> Liste des spectacles existants : </p>");
		try 
		{
			Vector<Spectacle> spec = BDRequests.getSpectacles();
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

		strNumS		= req.getParameter("numS");
		dateS		= req.getParameter("date");
		strHeureS	= req.getParameter("heure");
		if (strNumS == null || dateS == null || strHeureS == null) {
			printForm(out, strNumS, strHeureS, dateS);
		}
		else 
		{
			if(!Utilitaires.validDateFormat(dateS))
			{
				CloseOnError(out, "Veuillez entrer une date valide. Exemple : 01/12/2014", strNumS, strHeureS, dateS);
				return;
			}
			try
			{
				numS = Integer.parseInt(strNumS);
			}
			catch(NumberFormatException e)
			{
				CloseOnError(out, "Veuillez entrer un nombre pour le num&eacute;ro de spectacle.", strNumS, strHeureS, dateS);
				return;
			}
			try
			{
				heureS = Integer.parseInt(strHeureS);
			}
			catch(NumberFormatException e)
			{
				CloseOnError(out, "Veuillez entrer un nombre pour l'heure du spectacle.", strNumS, strHeureS, dateS);
				return;
			}
			try {
				// on verifie que l'heure est valide
				if (heureS >= 0 && heureS <= 23)
				{
					// on verifie que le numero de spectacle existe
					if (BDRequests.isInSpectacles(numS))
					{
						// on verifie que la representation n'est pas deja presente
						if (BDRequests.existeDateRep(numS,dateS))
						{
							out.println("<br><i> Cette representation existe deja, impossible de l'ajouter. </i></p>");
							printForm(out, strNumS, strHeureS, dateS);
						}
						else
						{
							BDRequests.addRepresentation(numS, dateS, strHeureS);
							out.println("<br> ajout de la representation realisee <br>");
						}
					}
					else
					{
						out.println("<br><i> Ce num&eacute;ro de spectacle n'existe pas, veuillez v&eacute;rifier ce dernier dans la liste ci-dessus. </i></p>");
						printForm(out, strNumS, strHeureS, dateS);
					}
				}
				else
				{
					out.println("<br><i> Heure invalide, impossible d'ajouter la representation. </i></p>");
					printForm(out, strNumS, strHeureS, dateS);
				}
			}
			catch (Exception e)
			{
				out.println("<p><i><font color=\"#FFFFFF\">Impossible d'ajouter la representation. Veuillez r&eacute;essayer ult&eacute;rieurement.</i></p>");
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
	
	private static void printForm(ServletOutputStream out, String strNumS, String strHeureS, String date) throws IOException
	{
		out.println("<font color=\"#FFFFFF\">Veuillez saisir les informations relatives &agrave; la nouvelle repr&eacute;sentation :");
		out.println("<P>");
		out.print("<form action=\"");
		out.print("NouvelleRepresentationServlet\" ");
		out.println("method=POST>");
		out.println("Num&eacute;ro de spectacle :");
		if(strNumS != null)
		{
			out.println("<input type=text size=20 name=numS value="+strNumS+">");
		}
		else
		{
			out.println("<input type=text size=20 name=numS>");
		}
		out.println("<br>");
		out.println("Date de la repr&eacute;sentation :");
		if(date != null)
		{
			out.println("<input type=text size=20 name=date value=" + date + ">");
		}
		else
		{
			out.println("<input type=text size=20 name=date>");
		}
		out.println("<br>");
		out.println("Heure de d&eacute;but de la repr&eacute;sentation :");
		if(strHeureS != null)
		{
			out.println("<input type=text size=20 name=heure value=" + strHeureS + ">");
		}
		else
		{
			out.println("<input type=text size=20 name=heure>");
		}
		out.println("<br>");
		out.println("<input type=submit>");
		out.println("</form>");
	}
	
	private static void CloseOnError(ServletOutputStream out, String message, String strNumS, String strHeureS, String dateS) throws IOException
	{
		out.println("<p><i><font color=\"#FFFFFF\">" + message + "</i></p>");
		printForm(out, strNumS, strHeureS, dateS);
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
		out.println("</BODY>");
		out.close();
	}
}
