import javax.servlet.*;
import javax.servlet.http.*;

import exceptions.ConnectionException;
import exceptions.RequestException;

import utils.ErrorLog;
import utils.Utilitaires;

import accesBD.SQLRequest;

import modele.*;

import java.io.IOException;

public class ZoneReservationServlet extends HttpServlet {

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
		
		String numS, dateS, numZ, heureS;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.println("<HEAD><TITLE> Reservation pour une representation</TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");
		ErrorLog errorLog = new ErrorLog();

		out.println("<font color=\"#FFFFFF\"><h1> Choix zone </h1>");

		numS		= req.getParameter("numS");
		dateS		= req.getParameter("date");
		heureS		= req.getParameter("heure");
		numZ		= req.getParameter("numZ");
		
		
		if (numZ == null && dateS != null || heureS != null || numS != null) 
		{
			printForm(out, numS, dateS, heureS);
			out.println(" numS : " + numS);
			out.println(" dateS : " + dateS);
			out.println(" heures : " + heureS);
		} 
		else 
		{
			out.println("<hr><p>erreur parametres</a></p>");

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
	
	public static void printForm(ServletOutputStream out, String numS, String dateS, String heureS) throws IOException
	{
		out.println("<font color=\"#FFFFFF\">Veuillez saisir la zone dans laquelle vous souhaitez etre place");
		out.println("<P>");
		out.print("<form action=\"");
		out.print("ReservationServlet?numS=" + numS + "&date=" + dateS + "&heure=" + heureS +"\" ");
		out.println("method=POST>");
		out.println("<br>");	
		//out.println("Zone:");
		out.println("<input type=text size=20 name=numZ>");
		out.println("<br>");
		out.println("<input type=submit>");
		out.println("</form>");
	}

	/*public static void printError(ServletOutputStream out, String objError) throws IOException
	{
		out.println("<br> "  +  objError + " est invalide, veuillez ressaisir les informations <br>");
		printForm(out);
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
		out.println("</BODY>");
		out.close();	
	}*/
	
}

