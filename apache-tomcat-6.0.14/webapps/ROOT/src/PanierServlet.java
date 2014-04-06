/*
 * @(#)ProgrammeServlet.java	1.0 2007/10/31
 * 
 * Copyright (c) 2007 Sara Bouchenak.
 */
import javax.servlet.*;
import javax.servlet.http.*;

import panier.ContenuPanier;
import panier.Panier;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import accesBD.BDPanier;
import accesBD.BDPlaces;
import accesBD.BDRequests;
import accesBD.BDSpectacles;
import exceptions.*;
import utils.*;
import modele.*;

/**
 * Proramme Servlet.
 *
 * This servlet dynamically returns the theater program.
 *
 * @author <a href="mailto:Sara.Bouchenak@imag.fr">Sara Bouchenak</a>
 * @version 1.0, 31/10/2007
 */

public class PanierServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


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
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
			{
		int deleteIndex;
		boolean synch;
		LinkedList<String> validParams;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		System.out.println("panier servlet get");
		out.print(HtmlGen.htmlPreambule("Panier"));

		InputParameters parameters = new InputParameters();
		parameters.addParameter("delete", "element du panier a retirer", ParameterType.INTEGER);
		parameters.addParameter("synch", "presence de choix de synchronisation du panier", ParameterType.BOOLEAN);
		parameters.addParameter("synchPresent", "valeur synchronisation du panier", ParameterType.BOOLEAN);

		HttpSession session = req.getSession();
		String login;
		ErrorLog errorLog = new ErrorLog();
		login = (String)session.getAttribute("login");
		
		// creation d'un panier par defaut
		Panier panier = new Panier();
		try {
			// recuperation du panier de l'utilisateur
			// si l'utilisateur a un panier dans la base il est recupere, sinon on prend celui de la session
			panier = Panier.getUserPanier(req.getSession());
		} catch (ConnectionException e) {
			errorLog.writeException(e);
		} catch (RequestException e) {
			errorLog.writeException(e);
		}
		parameters.readParameters(req);
		validParams = parameters.getValidParametersNames();
		
		// delete : suppression d'un element du panier envoye par un bouton de suppression
		if(validParams.contains("delete"))
		{
			deleteIndex = parameters.getIntParameter("delete");
			if(deleteIndex < panier.size())
			{
				// suppression de l'element du panier apres verification de l'indice
				panier.removeContenu(deleteIndex);
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
					"<th> Prix total </th>");
			
			for(int i = 0 ; i < panier.size() ; i++)
			{
				// affichage d'une commande du panier
				printLignePanier(out, panier.getContenu(i), i);
			}
			out.println("</table>");
			
			out.println("<br> Prix total du panier : " + panier.getPrixTotal() + " <br>");
			
			// recuperation de l'attribut indiquant le stockage sur la base
			if(session.getAttribute("synch") == null)
			{
				// attribut non present : par defaut pas de stockage sur base
				synch = false;
			}
			else
			{
				synch = (boolean)session.getAttribute("synch");
			}
			
			// on teste si l'utilisateur est identifie
			if(login != null)
			{
				// generation de bouton de demande de synchronisation du panier avec la base
				out.print(generateCheckSynch(synch));
				try {
					if(synch)
					{
						// mise a jour du panier dans la base
						BDPanier.synchronizePanier(login, panier);
					}
					else
					{
						// plus de sauvegarde du panier, suppression de celui-ci dans la base
						BDPanier.removePanier(login);
					}
				}catch (RequestException e)
				{
					out.println("<p><i><font color=\"#FFFFFF\">Erreur de panier.</i></p>");
					errorLog.writeException(e);
				} catch (ConnectionException e) {
					out.println("<p><i><font color=\"#FFFFFF\">Erreur de panier.</i></p>");
					errorLog.writeException(e);
				}
			}
		}
		out.println(HtmlGen.PiedPage(req));
		out.println("</BODY>");
		out.close();
			}
	
	/**
	 * 		Genere le code html pour le bouton d'activation de sauvegarde du panier sur la base.
	 * @param synch
	 * @return
	 */
	private String generateCheckSynch(boolean synch)
	{
		String str = "";

		// generation du bouton radio pour la demande de sauvegarde du panier
		str += "<form action=\"PanierServlet\" method=POST> " +
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
				"<td>" + contenu.getCategorie().getCategorie() +  "</td>" +
				"<td>" + contenu.getNbPlaces() + "</td>" +
				"<td>" + contenu.getPrixTotal() + "</td>");
		out.print("<td><form action=\"PanierServlet\" method = \"post\"> " +
				"<input type=\"submit\" value=\"Retirer\">" +
				"<input type=\"hidden\" name=\"delete\" value=" + index + ">" +
				"</form></td>");
		out.println("</tr>");
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
		return "Retourne le programme du theatre";
	}

}
