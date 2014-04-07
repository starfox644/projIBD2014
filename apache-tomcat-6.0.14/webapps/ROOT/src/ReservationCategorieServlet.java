import javax.servlet.*;
import javax.servlet.http.*;

import exceptions.ConnectionException;
import exceptions.RequestException;

import utils.ErrorLog;
import utils.Utilitaires;

import accesBD.BDCategories;
import accesBD.BDRequests;
import accesBD.SQLRequest;

import modele.*;

import java.io.IOException;

public class ReservationCategorieServlet extends HttpServlet {

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
		
		String numS, dateS, numZ, heureS, nomC, nbPlaces;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.println("<HEAD><TITLE> Reservation pour une representation</TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");
		ErrorLog errorLog = new ErrorLog();

		out.println("<font color=\"#FFFFFF\"><h1> Choisissez une categorie et le nombre de places desire : </h1>");

		numS		= req.getParameter("numS");
		dateS		= req.getParameter("date");
		heureS		= req.getParameter("heure");
		nomC		= req.getParameter("nomC");
		nbPlaces	= req.getParameter("nbPlaces");
		
		// deja remplit 
		if (dateS != null && heureS != null && numS != null) 
		{
			if (nomC != null && nbPlaces != null)
			{
				// on verifie que les parametres sont valides
				// avant de les envoyer a ReservationServlet
				try {
					if (BDCategories.getCategorie(nomC) != null )
					{
						if (Utilitaires.validIntegerFormat(nbPlaces))
						{
							RequestDispatcher dispatcher = req.getRequestDispatcher("ReservationServlet");
							dispatcher.forward(req, res);
						}
						else
						{
							out.println("Le nombre de places est invalide <br>");
							printForm(out, numS, dateS, heureS);
							out.println(" numS : " + numS);
							out.println(" dateS : " + dateS);
							out.println(" heures : " + heureS);
						}
					}
					else
					{

						out.println("La categorie est invalide <br>");
						printForm(out, numS, dateS, heureS);
						out.println(" numS : " + numS);
						out.println(" dateS : " + dateS);
						out.println(" heures : " + heureS);
					}
				} catch (ConnectionException e) {
					errorLog.writeException(e);
					e.printStackTrace();
					out.println("<p><i><font color=\"#FFFFFF\">Reservation impossible, veuillez r&eacute;essayer utltérieurement.</i></p>");
				} catch (RequestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					out.println("<p><i><font color=\"#FFFFFF\">Reservation impossible, veuillez r&eacute;essayer utltérieurement.</i></p>");
					errorLog.writeException(e);

				}
			}
			else
			{
				printForm(out, numS, dateS, heureS);
				out.println(" numS : " + numS);
				out.println(" dateS : " + dateS);
				out.println(" heures : " + heureS);
			}
		} 
		else 
		{
			out.println("<hr><p>Une erreur s'est produite lors de la reservation</a></p>");
			out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/servlet/ProgrammeServlet\">Retour au programme</a></p>");
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
		out.println("<font color=\"#FFFFFF\">Veuillez saisir la categorie et le nombre de places desire");
		out.println("<P>");
		out.print("<form action=\"");
		out.print("ReservationCategorieServlet?numS=" + numS + "&date=" + dateS + "&heure=" + heureS +"\" ");
		out.println("method=POST>");
		out.println("<br>");	
		out.println("Categorie:");
		out.println("<input type=text size=20 name=nomC>");
		out.println("<br>");
		out.println("nombre de places :");
		out.println("<input type=text size=20 name=nbPlaces>");
		out.println("<br>");
		out.println("<input type=submit>");
		out.println("</form>");
	}
	
}

