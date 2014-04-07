package loginServlets;
/*
 * @(#)ProgrammeServlet.java	1.0 2007/10/31
 * 
 * Copyright (c) 2007 Sara Bouchenak.
 */
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;

import accesBD.BDLogin;
import exceptions.*;
import utils.*;

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
	private static final String formLink = "/NouvelUtilisateur";

	/**
	 * 		Entree de la methode get.
	 *
	 * @param req	Objet HttpServletRequest contenant la requete du client.
	 * @param res	Objet HttpServletResponse contenant la reponse a envoyer au client.
	 *
	 * @throws ServletException   Si la requete ne peut pas etre traitee.
	 * @throws IOException	 Si une erreur d'entree / sortie est generee lors du
	 * 						traitement de la requete.
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
