import javax.servlet.*;
import javax.servlet.http.*;

import exceptions.RequestException;

import utils.ErrorLog;
import utils.Utilitaires;

import accesBD.BDRequests;
import accesBD.SQLRequest;

import modele.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class ReservationServlet extends HttpServlet {

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

		out.println("<font color=\"#FFFFFF\"><h1> Reservation pour une representation </h1>");

		numS		= req.getParameter("numS");
		dateS		= req.getParameter("date");
		heureS		= req.getParameter("heure");
		numZ		= req.getParameter("numZ");
		int heure, num, numZone;
		if (numS == null || dateS == null || numZ == null) {
			printForm(out);
		} 
		else 
		{
			
			try {

				SQLRequest request = new SQLRequest();
				Utilisateur user = Utilitaires.Identification();
				// verification que la date soit valide
				if(!Utilitaires.validDateFormat(dateS))
				{
					printError(out, new String("Le format de la date de la representation"));
					return;
				}
				
				try
				{
					num = Integer.parseInt(numS);
				}
				catch(NumberFormatException e)
				{
					printError(out, new String("Le numero du spectacle"));
					return;
				}
				try
				{
					numZone = Integer.parseInt(numZ);
				}
				catch(NumberFormatException e)
				{
					printError(out, new String("Le numero de la zone"));
					return;
				}
				try
				{
					heure = Integer.parseInt(heureS);
				}
				catch(NumberFormatException e)
				{
					printError(out, new String("Le format de l'heure de la representation"));
					return;
				}
				if (heure < 0 && heure > 23)
				{
					printError(out, new String("Le format de l'heure de la representation"));
					return;
				}
				
				// on verifie que le numero de spectacle existe
				if (BDRequests.isInSpectacles(num))
				{
					
					// on verifie que la date de la representation existe
					String dateH = dateS + " " + heureS;
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH");
					Date dateRep = formatter.parse(dateH);
					Calendar today = Calendar.getInstance();
					
					Calendar date = Calendar.getInstance();
					date.setTime(dateRep);
					today.add(Calendar.HOUR, 1);
					int month = today.get(Calendar.MONTH)+1;
					String dateToday = new String (today.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + today.get(Calendar.YEAR)
														+ " " + today.get(Calendar.HOUR_OF_DAY));
					// date inferieur a celle d'aujourd'hui
					if (date.compareTo(today) < 0)
					{
						out.println("<br>  Trop tard !!!!!! Cette date representation n'est plus valide. veuillez ressaisir les informations <br>");
						printForm(out);
						out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
						out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
						out.println("</BODY>");
						out.close();
						return;
					}
					
					if (BDRequests.existeDateRep (Integer.parseInt(numS),dateS, heure))
					{
						//out.println("<br> Cette representation existe <br>");
						Vector<Integer> result = new Vector<Integer>();
						Vector<Place> placesDispo = new Vector<Place>();
						Place p = null;
						int i = 0;
						// on verifie qu'il reste des place disponible pour 
						// cette representation
						placesDispo = BDRequests.getPlacesDispo(num, dateS, heure);
						// plus de place
						if (placesDispo.isEmpty())
						{
							out.println("<br> Il n'y a plus de place pour cette representation <br>");
							request.close();
						}
						else
						{
							///out.println("<br> Il reste de la place pour cette representation <br>");
							while (placesDispo.get(i).getNumZ() != numZone)
							{
								i++;
							}
							// plus de place dans cette zone
							if ( i >= placesDispo.size() )
							{
								out.println("<br> Il n'y a plus de place dans cette zone<br>");
								request.close();
								return;
							}
							else
							{
								//out.println("<br> Il reste de la place dans cette zone<br>");
								p = placesDispo.get(i);
							}
							
						}
						// recuperation du numero de ticket max 
						String strMax = "select max(noSerie) " +
									    "from LesTickets ";
						ResultSet rs = request.execute(strMax);
						int noSerie = 0;
						try {
							if ( rs.next() )
							{
								noSerie = rs.getInt(1)+1;
							}
						} catch (SQLException e) {
							throw new RequestException (" Erreur dans la recuperation du max noSerie : \n"
									+ "Code Oracle : " + e.getErrorCode() + "\n"
									+ "Message : " + e.getMessage() + "\n");
						}
						
						// ajout du ticket 
						Ticket t = new Ticket(noSerie, num, dateH, p.getNoPlace(), p.getNoRang(), dateToday, 66);
						out.println("<br> creation ticket ok. Le ticket est : " + t + "<br>");
						
						String strTicket = "INSERT INTO LesTickets " +
							    		   "VALUES( " + noSerie + ", " + num + ", "	+ "to_date('" + dateH  + "', 'DD/MM/YY HH24') , " 
							    		   + p.getNoPlace() + ", "  + p.getNoRang() + ", to_date('" + dateToday + "', 'DD/MM/YY HH24') , " + 
							    		   "66 )";		//TODO	   
						try
						{
							rs = request.execute(strTicket);
							out.println("<p><i><font color=\"#FFFFFF\"> Reservation realisee</i></p>");

						}
						catch (RequestException e)
						{
							out.println("<p><i><font color=\"#FFFFFF\"> Reservation impossible. Erreur lors de l'ajout du ticket</i></p>");
							printForm(out);
							errorLog.writeException(e);
						}
						
							/*try {
								if ( rs.next() )
								{
									noSerie = rs.getInt(1)+1;
								}
						} catch (SQLException e) {
							throw new RequestException (" Erreur dans la recuperation du max noSerie : \n"
									+ "Code Oracle : " + e.getErrorCode() + "\n"
									+ "Message : " + e.getMessage() + "\n");
						}*/

					}
					else
					{
						out.println("<br> Cette date de representation n'existe pas <br>");
						printForm(out);
						return;
						
					}
				}
				else
				{
					out.println("<br> Ce numero de spectacle n'existe pas<br>");
					printForm(out);
					return;
				}
				request.commit();
				request.close();
			}
			catch (RequestException e)
			{
				out.println("<p><i><font color=\"#FFFFFF\"> Reservation impossible. Erreur dans request</i></p>");
				printForm(out);
				errorLog.writeException(e);
			} 
			catch (Exception e)
			{
				out.println("<p><i><font color=\"#FFFFFF\">Impossible de reserver.</i></p>");
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
		out.print("ReservationServlet\" ");
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
		out.println("Zone:");
		out.println("<input type=text size=20 name=numZ>");
		out.println("<br>");
		out.println("<input type=submit>");
		out.println("</form>");
	}

	public static void printError(ServletOutputStream out, String objError) throws IOException
	{
		out.println("<br> "  +  objError + " est invalide, veuillez ressaisir les informations <br>");
		printForm(out);
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
		out.println("</BODY>");
		out.close();	
	}
	
}

