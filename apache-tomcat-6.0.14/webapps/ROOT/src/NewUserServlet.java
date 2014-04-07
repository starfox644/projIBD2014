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

import accesBD.BDLogin;
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

public class NewUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String invite = "Veuillez saisir votre nom d'utilisateur et votre mot de passe";
	private static final String formLink = "NewUserServlet";

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
		ServletOutputStream out = res.getOutputStream();   

		res.setContentType("text/html");

		out.print(HtmlGen.htmlPreambule("Ajout d'utilisateur"));
		String login, passwd, passwdConfirm;
		
		InputParameters parameters = new InputParameters();
		parameters.addParameter("login", "Nom d'utilisateur", ParameterType.STRING);
		parameters.addParameter("password", "Mot de passe", ParameterType.PASSWD);
		parameters.addParameter("passwordConfirm", "Confirmation du mot de passe", ParameterType.PASSWD);
		
		// TO DO
		// Recuperation de la liste de tous les spectacles de la saison.
		// Puis construction dynamique d'une page web decrivant ces spectacles.
		// afficher resultat requete
		
		parameters.readParameters(req);
		if(parameters.nullParameters())
		{
			out.print(parameters.getHtmlForm(invite, formLink));
		}
		else
		{
			ErrorLog log = new ErrorLog();
			if(parameters.validParameters())
			{
				login = parameters.getStringParameter("login");
				passwd = parameters.getStringParameter("password");
				passwdConfirm = parameters.getStringParameter("passwordConfirm");
				try
				{
					if(passwd.equals(passwdConfirm))
					{
						if(BDLogin.addLogin(login, passwd))
						{
							out.println("Ajout realise avec success. <br>");
						}
						else
						{
							out.println("Ce login existe deja. <br>");
							out.print(parameters.getHtmlForm(invite, formLink));
						}
					}
					else
					{
						out.println("Les mots de passe dont differents. <br>");
						out.print(parameters.getHtmlForm(invite, formLink));
					}
				}
				catch (RequestException e)
				{
					out.println("<p><i><font color=\"#FFFFFF\">Impossible de verifier l'identification.</i></p>");
					out.print(parameters.getHtmlForm(invite, formLink));
					log.writeException(e);
				} catch (ConnectionException e) {
					out.println("<p><i><font color=\"#FFFFFF\">Impossible de verifier l'identification.</i></p>");
					out.print(parameters.getHtmlForm(invite, formLink));
					log.writeException(e);
				} 
			}
			else
			{
				out.print(parameters.getHtmlError());
				out.print(parameters.getHtmlForm(invite, formLink));
			}
		}
		
		/*out.println("</i></p>");
		out.println("<hr><p><font color=\"#FFFFFF\"><a href=\"/index.html\">Accueil</a></p>");*/
		out.println(HtmlGen.PiedPage(req));
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
		return "Retourne le programme du theatre";
	}

}
