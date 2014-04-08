package commandesServlets;

import javax.servlet.*;
import javax.servlet.http.*;

import accesBD.BDPanier;

import panier.ContenuPanier;
import panier.Panier;

import java.io.IOException;
import java.util.LinkedList;
import exceptions.*;
import utils.*;

/**
 * 		Servlet permettant d'afficher et de gerer le panier de l'utilisateur.
 */
public class PanierServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	/**
	 * 		Entree de la methode get.
	 * 		Verifie les parametres de la servlet, pouvant contenir des demandes
	 * 		de modification du panier. Effectue ces modifications puis affiche le 
	 * 		contenu du panier de l'utilisateur.
	 * 		Si des elements du panier ne sont plus disponibles, l'utilisateur en est
	 * 		informe et ils sont retires.
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
		int index;
		boolean synch;
		LinkedList<ContenuPanier> invalidContenus = new LinkedList<ContenuPanier>();
		LinkedList<String> validParams;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.print(HtmlGen.htmlPreambule("Panier"));

		// parametres possibles de la servlet
		InputParameters parameters = new InputParameters();
		parameters.addParameter("delete", "element du panier a retirer", ParameterType.INTEGER);
		parameters.addParameter("add", "ajout d'une place", ParameterType.INTEGER);
		parameters.addParameter("sub", "retrait d'une place", ParameterType.INTEGER);
		parameters.addParameter("synch", "presence de choix de synchronisation du panier", ParameterType.BOOLEAN);
		parameters.addParameter("synchPresent", "valeur synchronisation du panier", ParameterType.BOOLEAN);

		HttpSession session = req.getSession();
		ErrorLog errorLog = new ErrorLog();

		// creation d'un panier par defaut
		Panier panier = new Panier();
		try {
			// recuperation du panier de l'utilisateur dans la session, par cookie ou
			// dans la base selon les cas
			panier = Panier.getUserPanier(req);
			// verification et recuperation des contenus invalides
			invalidContenus = panier.checkContenu();
		} catch (ConnectionException e) {
			errorLog.writeException(e);
		} catch (RequestException e) {
			errorLog.writeException(e);
		}
		
		// recuperation et traitement des parametres
		parameters.readParameters(req);
		validParams = parameters.getValidParametersNames();

		// delete : suppression d'un element du panier envoye par un bouton de suppression
		if(validParams.contains("delete"))
		{
			index = parameters.getIntParameter("delete");
			if(index < panier.size())
			{
				// suppression de l'element du panier apres verification de l'indice
				panier.removeContenu(index);
			}
		}
		
		// ajout d'une place envoye par un bouton "+"
		if(validParams.contains("add"))
		{
			index = parameters.getIntParameter("add");
			if(index < panier.size())
			{
				// ajout de la place au contenu apres verification de l'indice
				panier.getContenu(index).addPlace();
			}
		}
		
		// ajout d'une place envoye par un bouton "-"
		if(validParams.contains("sub"))
		{
			index = parameters.getIntParameter("sub");
			if(index < panier.size())
			{
				// retrait de la place au contenu apres verification de l'indice
				panier.getContenu(index).subPlace();
			}
		}
		
		// parametre indiquant le changement de statut de la synchronisation avec la base
		if(validParams.contains("synchPresent"))
		{
			// parametre qui contient le changement de statut
			if(validParams.contains("synch"))
			{
				synch = parameters.getBooleanParameter("synch");
			}
			else
			{
				// si le parametre n'est pas present, la synchronisation doit etre activee
				// le bouton de choix a ete desactive
				synch = false;
			}
			session.setAttribute("synch", synch);
		}
		
		// recuperation du login de l'utilisateur, qui vaut null s'il n'est pas identifie
		String login = (String)session.getAttribute("login");
		
		// traitement des contenus invalides
		if(invalidContenus.size() != 0)
		{
			if(panier.size() == 0 && login != null)
			{
				// cas particulier ou le panier de l'utilisateur devient vide et qu'il est loge
				// on doit supprimer son panier de la base car il est invalide
				// car un panier vide n'est pas synchronise par la suite
				try {
					BDPanier.removePanier(login);
				} catch (ConnectionException e) 
				{
					errorLog.writeException(e);
				} catch (RequestException e) 
				{
					errorLog.writeException(e);
				}
			}
			out.println("<br> Ces commandes ne sont plus disponibles : <br>");
			out.println("<table>");
			out.println("<tr>");

			// titres des colonnes du tableau
			out.println("<th> Spectacle </th>" +
					"<th> Date </th>" +
					"<th> Heure </th>" +
					"<th> Categorie </th>" +
					"<th> Nombre de places </th>" +
					"<th> Motif </th>");

			// affichage des contenus invalides
			for(ContenuPanier contenu : invalidContenus)
			{
				printLigneInvalide(out, contenu);
			}
			out.println("</table>");
		}

		if(panier.size() == 0)
		{
			out.println("<br> Votre panier est vide. <br>");
		}
		else
		{
			out.println("<br> Contenu du panier : <br><br>");
			out.println("<table>");
			out.println("<tr>");

			// titres des colonnes du tableau
			out.println("<th> Spectacle </th>" +
					"<th> Date </th>" +
					"<th> Heure </th>" +
					"<th> Categorie </th>" +
					"<th> Nombre de places </th>" +
					"<th> </th>" +
					"<th> Prix total </th>");

			for(int i = 0 ; i < panier.size() ; i++)
			{
				// affichage d'une commande du panier
				printLignePanier(out, panier.getContenu(i), i);
			}
			out.println("</table>");

			out.println("<br> Prix total du panier : " + panier.getPrixTotal() + " <br>");
			out.print("<form action=\"/Validation\" method=POST> <input type=\"submit\" " +
					"value=\"valider le panier\">"); 

			// recuperation de l'attribut indiquant la sauvegarde du panier
			if(session.getAttribute("synch") == null)
			{
				// attribut non present : par defaut a vrai
				synch = true;
			}
			else
			{
				synch = (boolean)session.getAttribute("synch");
			}

			// generation du bouton de demande de synchronisation du panier avec la base
			out.print(generateCheckSynch(synch));
		}
		panier.synchronize(req, res);
		out.println(HtmlGen.PiedPage(req));
		out.println("</BODY>");
		out.close();
	}

	/**
	 * 		Genere le code html pour le bouton d'activation de sauvegarde du panier sur la base.
	 * @param synch	Indique si l'utilisateur a choisi de sauvegarder son panier ou non.
	 * @return	String contenant le code html genere pour le bouton de choix.
	 */
	private String generateCheckSynch(boolean synch)
	{
		String str = "";

		// generation du bouton radio pour la demande de sauvegarde du panier
		str += "<form action=\"/Panier\" method=POST> " +
				"<input type=\"hidden\" name=\"synchPresent\" value=\"true\" >" +
				"<input type=\"checkbox\" name=\"synch\" value=\"true\" ";
		if(synch)
		{
			// si la sauvegarde etait activee, le bouton est coche par defaut
			str +="checked";
		}
		str +=  ">" +
				" Sauvegarder mon panier pour la prochaine visite" +
				"<input type=\"submit\" value=\"Valider\">" +
				"</form>";
		return str;
	}

	/**
	 * 		Affichage d'une ligne du panier de l'utilisateur.
	 * @param out			
	 * @param contenu		Contient la ligne du panier a afficher.
	 * @param index			Numero de la ligne dans le panier.
	 * @throws IOException
	 */
	private void printLignePanier(ServletOutputStream out, ContenuPanier contenu, int index) throws IOException
	{
		out.println("<tr>");
		out.print("<td>" + contenu.getSpectacle() + "</td>" +
				"<td>" + contenu.getDateS() +  "</td>" +
				"<td>" + contenu.getHeure() + "</td>" +
				"<td>" + contenu.getCategorie().getNom() +  "</td>" +
				"<td>" + contenu.getNbPlaces() + "</td>");
		// boutons + et - pour ajouter ou retirer une place
		out.print("<td><form action=\"/Panier\" method = \"post\"> " +
				"<input type=\"submit\" value=\"+\">" +
				"<input type=\"hidden\" name=\"add\" value=" + index + ">" +
				"</form>" +
				"<form action=\"/Panier\" method = \"post\"> " +
				"<input type=\"submit\" value=\"-\">" +
				"<input type=\"hidden\" name=\"sub\" value=" + index + ">" +
				"</form></td>" +
				"<td>" + contenu.getPrixTotal() + "</td>");
		// bouton de retrait du panier
		out.print("<td><form action=\"/Panier\" method = \"post\"> " +
				"<input type=\"submit\" value=\"Retirer\">" +
				"<input type=\"hidden\" name=\"delete\" value=" + index + ">" +
				"</form></td>");
		out.println("</tr>");
	}

	private void printLigneInvalide(ServletOutputStream out, ContenuPanier contenu) throws IOException
	{
		out.println("<tr>");
		// affichage de la ligne barree pour indiquer qu'elle est invalide
		out.print("<td><del>" + contenu.getSpectacle() + "</td></del>" +
				"<td><del>" + contenu.getDateS() +  "</td></del>" +
				"<td><del>" + contenu.getHeure() + "</td></del>" +
				"<td><del>" + contenu.getCategorie().getNom() +  "</td></del>" +
				"<td><del>" + contenu.getNbPlaces() + "</td></del>" +
				"<td>" + contenu.getError() + "</td>");
		out.println("</tr>");
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
