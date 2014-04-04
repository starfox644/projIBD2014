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
		LinkedList<String> validParams;
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.print(HtmlGen.htmlPreambule("Panier"));
		
		InputParameters parameters = new InputParameters();
		parameters.addParameter("delete", "element du panier a retirer", ParameterType.INTEGER);
		
		// TO DO
		// Recuperation de la liste de tous les spectacles de la saison.
		// Puis construction dynamique d'une page web decrivant ces spectacles.
		// afficher resultat requete
		
		HttpSession session = req.getSession();
		Panier panier = (Panier)session.getAttribute("panier");
		if(panier == null)
		{
			out.println("<br> Votre panier est vide. <br>");
			panier = new Panier();
			ContenuPanier contenu1 = new ContenuPanier(101, "manu chao", "12/03/14", 15, 3, new Categorie("balcon", 13.5f));
			ContenuPanier contenu2 = new ContenuPanier(105, "indochine", "12/08/14", 23, 2, new Categorie("poulaillier", 39.99f));
			panier.addContenu(contenu1);
			panier.addContenu(contenu2);
			session.setAttribute("panier", panier);
		}
		else
		{
			parameters.readParameters(req);
			validParams = parameters.getValidParametersNames();
			if(validParams.contains("delete"))
			{
				deleteIndex = parameters.getIntParameter("delete");
				if(deleteIndex < panier.size())
				{
					panier.removeContenu(deleteIndex);
				}
			}
			
			if(panier.size() == 0)
			{
				out.println("<br> Votre panier est vide. <br>");
			}
			else
			{
				out.println("<br> Contenu du panier : <br><br>");
				//out.print(panier.toString());
				out.println("<table>");
				out.println("<tr>");
				out.println("<th> Spectacle </th>" +
							"<th> Date </th>" +
							"<th> Heure </th>" +
							"<th> Categorie </th>" +
							"<th> Nombre de places </th>" +
							"<th> Prix total </th>");
				for(int i = 0 ; i < panier.size() ; i++)
				{
					printLignePanier(out, panier.getContenu(i), i);
				}
				out.println("</table>");
			}
		}
		
		out.println("</i></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");
		out.println("</BODY>");
		out.close();
	}
	
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
