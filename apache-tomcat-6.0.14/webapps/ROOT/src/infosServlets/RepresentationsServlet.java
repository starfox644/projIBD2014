package infosServlets;

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
import accesBD.BDPlaces;
import accesBD.BDRepresentations;
import accesBD.BDSpectacles;
import modele.*;

/**
 * 		Construit la liste des representations d'un spectacle
 * 		dont le numero est donne en parametre.
 */
public class RepresentationsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String invite = "Veuillez saisir un num&eacute;ro de spectacle";
	private static final String formLink = "/Representations";

	/**
	 * 		Entree de la methode get.
	 * 		Genere la liste des representations d'un spectacle sous forme
	 * 		de liens permettant de les ajouter au panier.
	 *
	 * @param req	Objet HttpServletRequest contenant la requete du client.
	 * @param res	Objet HttpServletResponse contenant la reponse a envoyer au client.
	 *
	 * @throws ServletException   Si la requete ne peut pas etre traitee.
	 * @throws IOException	 Si une erreur d'entree / sortie est generee lors du
	 * 						traitement de la requete.
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
			boolean addPossible = true;
			boolean complet = false;
			
			if(!parameters.validParameters())
			{
				out.print(parameters.getHtmlError());
				out.print(parameters.getHtmlForm(invite, formLink));
				//out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");
				out.println(HtmlGen.PiedPage(req));
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
							addPossible = true;
							complet = false;
							try
							{
								BDPlaces.checkAjoutPanier(numS, tmpStrDate, heureRep, 1, null);
							}
							catch(ConnectionException e)
							{
								errorLog.writeException(e);
							}
							catch(RequestException e)
							{
								errorLog.writeException(e);
							}
							catch(ReservationException e)
							{
								addPossible = false;
								if(e.getMessage().equals("Il n'y a plus de place disponible pour cette representation."))
								{
									complet = true;
								}
							}
							out.println(Utilitaires.printDate(r.getDate())+ "&nbsp;&nbsp;&nbsp");
							if(addPossible)
							{
								out.println("<a href=\"/Reservation?numS=" + numS 
										+ "&date=" + tmpStrDate
										+ "&heure="+ heureRep 
										+ "&nomC=balcon\">" 
										+ "Ajouter au panier"
										+ "</a>");
							}
							else
							{
								out.println("  Complet");
							}
							out.print("<br>");
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

			out.println(HtmlGen.PiedPage(req));
			out.println("</BODY>");
			out.close();
		}
	}

	/**
	 * 		Entree de la methode post.
	 * 		Redirige vers la methode get.
	 *
	 * @param req	Objet HttpServletRequest contenant la requete du client.
	 * @param res	Objet HttpServletResponse contenant la reponse a envoyer au client.
	 *
	 * @throws ServletException   Si la requete ne peut pas etre traitee.
	 * @throws IOException	 Si une erreur d'entree / sortie est generee lors du
	 * 						traitement de la requete.
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
