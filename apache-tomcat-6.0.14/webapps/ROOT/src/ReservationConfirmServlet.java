import javax.servlet.*;
import javax.servlet.http.*;

import exceptions.ConnectionException;
import exceptions.RequestException;

import utils.ErrorLog;
import utils.HtmlGen;
import utils.InputParameters;
import utils.ParameterType;
import utils.Utilitaires;

import accesBD.BDPlaces;
import accesBD.BDRequests;
import accesBD.SQLRequest;

import modele.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class ReservationConfirmServlet extends HttpServlet {

	private static final String invite = "Veuillez saisir les informations relatives &agrave; la repr&eacute;sentation";
	private static final String formLink = "ReservationServlet";

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
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		InputParameters parameters = new InputParameters();
		parameters.addParameter("numS", "Num&eacute;ro de spectacle", ParameterType.INTEGER);
		parameters.addParameter("date", "Date de repr&eacute;sentation", ParameterType.DATE);
		parameters.addParameter("heure", "Heure de repr&eacute;sentation", ParameterType.HOUR);
		parameters.addParameter("numZ", "Num&eacute;ro de zone", ParameterType.INTEGER);
		parameters.addParameter("noPlace", "Num&eacute;ro de zone", ParameterType.INTEGER);
		parameters.addParameter("noRang", "Num&eacute;ro de zone", ParameterType.INTEGER);
		String dateS;
		int numS, numZ, heureS, noPlace, noRang;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		ErrorLog errorLog = new ErrorLog();

		out.print(HtmlGen.htmlPreambule("Reservation pour une representation"));

		parameters.readParameters(req);
		if(!parameters.validParameters())
		{
			out.print(parameters.getHtmlError());
			out.println("<p><i><font color=\"#FFFFFF\"> Reservation impossible.</i></p>");
			out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");
			out.println("</BODY>");
			out.close();
			return;
		}
		numS = parameters.getIntParameter("numS");
		dateS = parameters.getStringParameter("date");
		heureS = parameters.getIntParameter("heure");
		numZ = parameters.getIntParameter("numZ");
		noPlace = parameters.getIntParameter("noPlace");
		noRang = parameters.getIntParameter("noRang");

		// on verifie que la date de la representation existe
		String dateH = dateS + " " + heureS;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH");
		Date dateRep;
		try 
		{
			dateRep = formatter.parse(dateH);
			Calendar today = Calendar.getInstance();

			Calendar date = Calendar.getInstance();
			date.setTime(dateRep);
			today.add(Calendar.HOUR, 1);
			int month = today.get(Calendar.MONTH)+1;
			String dateToday = new String (today.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + today.get(Calendar.YEAR)
					+ " " + today.get(Calendar.HOUR_OF_DAY));
			// date inferieure a celle d'aujourd'hui
			if (date.compareTo(today) < 0)
			{
				out.println("<br>  Trop tard !!!!!! Cette date representation n'est plus valide. Veuillez ressaisir les informations <br>");
				out.print(parameters.getHtmlForm(invite, formLink));
				out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
				out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
				out.println("</BODY>");
				out.close();
				return;
			}
			if(!BDPlaces.reserverPlace(numS, dateS, dateToday, heureS, numZ, noPlace, noRang))
			{
				out.println("<br>Impossible de reserver la place. <br>");
			}
			else
			{
				out.println("<br>Place r&eacute;serv&eacute;e : <br>");
				out.println("<br>Place r&eacute;serv&eacute;e : <br>");
				out.println("<br>Place r&eacute;serv&eacute;e : <br>");
				out.println("<br>Place r&eacute;serv&eacute;e : <br>");
				out.println("<br>Place r&eacute;serv&eacute;e : <br>");
			}
		} catch (ConnectionException e) {
			out.println("<p><i><font color=\"#FFFFFF\"> Reservation impossible.</i></p>");
			errorLog.writeException(e);
		} catch (RequestException e) {
			out.println("<p><i><font color=\"#FFFFFF\"> Reservation impossible.</i></p>");
			errorLog.writeException(e);
		}catch (ParseException e) 
		{
			out.println("<p><i><font color=\"#FFFFFF\"> Reservation impossible.</i></p>");
			errorLog.writeException(e);
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

}

