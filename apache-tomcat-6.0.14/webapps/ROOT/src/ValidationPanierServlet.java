import javax.servlet.*;
import javax.servlet.http.*;
import panier.ContenuPanier;
import panier.Panier;
import exceptions.ConnectionException;
import exceptions.RequestException;
import utils.ErrorLog;
import accesBD.BDPlaces;
import accesBD.SQLRequest;
import modele.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

public class ValidationPanierServlet extends HttpServlet {

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

		ServletOutputStream out = res.getOutputStream();   
		Panier panier = new Panier();
		Vector< Vector<Place> > placesReserv = new Vector<Vector<Place>>();
		Vector<Place> tmp = new Vector<Place>();
		res.setContentType("text/html");

		out.println("<HEAD><TITLE> Validation du panier</TITLE></HEAD>");
		out.println("<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\">");
		out.println("<font color=\"#FFFFFF\"><h1> Validation Panier </h1>"
		+"<p><i><font color=\"#FFFFFF\">");
		ErrorLog errorLog = new ErrorLog();

		try {

			// recuperation du panier de l'utilisateur
			// si l'utilisateur a un panier dans la base il est recupere, sinon on prend celui de la session
			panier = Panier.getUserPanier(req.getSession());
		} catch (ConnectionException e) {
			errorLog.writeException(e);
			out.println("Erreur panier dans validation panier <br>");
			piedDePage(out);
			return;
		} catch (RequestException e) {
			errorLog.writeException(e);
			out.println("Erreur panier dans validation panier <br>");
			piedDePage(out);
			return;
		}
		
		// panier non vide
		if (panier.size() > 0)
		{

			SQLRequest request;
			Calendar today = Calendar.getInstance();
			int month = today.get(Calendar.MONTH)+1;
			String dateToday = new String (today.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + today.get(Calendar.YEAR)
								+ " " + today.get(Calendar.HOUR_OF_DAY));
			try {
				request = new SQLRequest();
			} catch (ConnectionException e1) {
				out.println("Validation du panier impossible, veuillez  reessayer ulterieurement <br>");
				errorLog.writeException(e1);
				piedDePage(out);
				return;
			}
			try 
			{

				int i = 0;
				boolean possible = true;
				
				// pour chaque ligne du panier
				// on verifie s'il y a encore assez de place consecutive 
				// si c'est bon, on reserve
				while (i < panier.size() && possible)
				{
					// on recupere les places a reserver pour cette ligne
					tmp = validerLignePanier(request, panier.getContenu(i));
					if (!tmp.isEmpty())
					{
						placesReserv.add(tmp);
						ContenuPanier contenu = panier.getContenu(i);
						// on reserve chaque place
						for(int j = 0 ; j < tmp.size(); j++)
						{
							Place p = tmp.get(j);
							BDPlaces.reserverPlace(request, contenu.getNumS(), contenu.getDateS(), dateToday,
									contenu.getHeure(), p.getNumZ(), p.getNoPlace(), p.getNoRang());
						}	
					}
					else
						possible = false;
					i++;
					
				}
				
				// si toutes les reservations on pu etre reservees
				if (i == panier.size() && possible)
				{
					// affichage du recapitulatif de la commande
					if (validationOk(request, out, panier, placesReserv))
					{
						request.commit();
						// panier.clear(); TODO
					}
					request.close();
				}
				else
				{
					out.println("Il ne reste plus assez de places pour la representation : " 
							+ panier.getContenu(i-1).getSpectacle() 
							+ "a la date : " + panier.getContenu(i-1).getDateS() + "<br>" +
							" Commande annulee ");
					request.rollback();
					request.close();
				}
			} 
			catch (ConnectionException e) {
				errorLog.writeException(e);
				out.println("Validation du panier impossible, veuillez  reessayer ulterieurement <br>");
				try {
					request.rollback();
				} catch (RequestException e1) 
				{
					errorLog.writeException(e1);
				}
				piedDePage(out);
				request.close();
				return;
			} catch (RequestException e) {
				errorLog.writeException(e);
				out.println("Validation du panier impossible, erreur interne <br>");	
				try {
					request.rollback();
				} catch (RequestException e1) 
				{
					errorLog.writeException(e1);
				}
				piedDePage(out);
				request.close();
				return;
			}
		}
		else
			out.println("Erreur panier vide <br>");
		
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
	public static void piedDePage(ServletOutputStream out) throws IOException 
	{
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/admin/admin.html\">Page d'administration</a></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Page d'accueil</a></p>");
		out.println("</BODY>");
		out.close();
	}
	
	/**
	 * Returns information about this servlet.
	 *
	 * @return String information about this servlet
	 */

	public String getServletInfo() {
		return "Ajoute une representation e une date donnee pour un spectacle existant";
	}
	
	public static Vector<Place> validerLignePanier(SQLRequest request, ContenuPanier reserv) throws IOException, ConnectionException, RequestException
	{
		int numS = reserv.getNumS();
		String dateS = reserv.getDateS();
		int heureS = reserv.getHeure();
		Categorie categorie = reserv.getCategorie();
		Vector<Place> placesDispo = BDPlaces.getPlacesDispo(request, numS, dateS, heureS, categorie);
		
		if (placesDispo.size() == 0)
			return placesDispo;
		System.out.println("***************Places Dispo ****************");
		for (int i = 0 ; i < placesDispo.size() ; i++)
		{
			System.out.println("numZ : "+ placesDispo.get(i).getNumZ() + 
						    " , noRang : " + placesDispo.get(i).getNoRang() +
						    " , noPlace : "+ placesDispo.get(i).getNoPlace());
		}
		
		Vector<Place> places = BDPlaces.placesSucc(placesDispo, reserv.getNbPlaces());
		if (places.isEmpty())
		{
			System.out.println(" Les places succ sont ::: Y en A PAS MOUAH AHAHAH");
		}
		else
		{
			System.out.println(" Les places succ sont ::: ");
			for (int i  = 0 ; i < places.size() ; i++)
			{
				System.out.println("numZ : "+ places.get(i).getNumZ() + 
					    " , noRang : " + places.get(i).getNoRang() +
					    " , noPlace : "+ places.get(i).getNoPlace());
			}
		}
		return places;
	}
	
	public static boolean validationOk(SQLRequest request, ServletOutputStream out, Panier panier, Vector<Vector<Place>> placesRes) throws IOException, ConnectionException, RequestException
	{
		int nbPlaces;
		if (placesRes.size() == 0)
		{
			out.println("Liste Vide");
			request.rollback();
			return false;
		}
		out.println("Votre commande a bien été validée <br>" +
				" Récapitulatif de votre commande : <br><br>");
		for(int i = 0 ; i < panier.size() ; i++)
		{
			nbPlaces = panier.getContenu(i).getNbPlaces();
			out.println(" ==> " + nbPlaces );
			if (nbPlaces > 1)
				out.println(" places pour voir ");
			else
				out.println(" place pour voir ");
			
			out.println(panier.getContenu(i).getSpectacle() + " le " 
						+ panier.getContenu(i).getDateS() + " a " 
						+ panier.getContenu(i).getHeure() + "H <br>");
			
			if (nbPlaces > 1)
				out.println("Places reservees pour cette representation  : " );
			else
				out.println("Place reservee pour cette representation  : " );
			
			for  (int j = 0 ; j < panier.getContenu(i).getNbPlaces()-1 ; j++)
			{
				Place p = placesRes.get(i).get(j);
				out.print(p.getNoPlace() + ", ");
			}
			out.println( placesRes.get(i).lastElement().getNoPlace());
			 

			if (nbPlaces > 1)
				out.println(" . Elles sont dans la categorie : " );
			else
				out.println(" . Elle est dans la categorie : " );
				
			out.println(panier.getContenu(i).getCategorie().getCategorie() 
						+ " au rang " + placesRes.get(i).get(0).getNoRang());
			out.println("<br>");
		}
		
		out.println("<br> Prix total de la commande : " + panier.getPrixTotal() + " <br>");
		return true;
	}
	
	/*public static void reserverPlaces(SQLRequest request, Panier panier, Vector<Vector<Place>> placesRes) throws ConnectionException, RequestException
	{
		Calendar today = Calendar.getInstance();
		today.add(Calendar.HOUR, 1);
		int month = today.get(Calendar.MONTH)+1;
		String dateToday = new String (today.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + today.get(Calendar.YEAR)
							+ " " + today.get(Calendar.HOUR_OF_DAY));
		
		// pour chaque representation
		for (int i = 0 ; i < panier.size() ; i++)
		{
			ContenuPanier contenu = panier.getContenu(i);
			
			// pour chaque place reservees 
			for(int j = 0 ; j < placesRes.get(i).size(); j++)
			{
				Place p = placesRes.get(i).get(j);
				BDPlaces.reserverPlace(request, contenu.getNumS(), contenu.getDateS(), dateToday,
						contenu.getHeure(), p.getNumZ(), p.getNoPlace(), p.getNoRang());
			}
				
		}
	
	}*/
}