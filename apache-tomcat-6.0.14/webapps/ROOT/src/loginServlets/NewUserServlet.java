package loginServlets;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;

import accesBD.BDLogin;
import exceptions.*;
import utils.*;

/**
 * 		Servlet permettant d'ajouter un utilisateur dans la base.
 * 		<br>
 * 		Genere un formulaire pour recuperer le login, mot de passe et la 
 * 		confirmation du mot de passe du nouvel utilisateur puis ajoute
 * 		l'utilisateur dans la base si le login n'y est pas deja present.
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
							out.println("Nouvel utilisateur ajout&eacute; avec succes. <br>");
							req.getSession().setAttribute("login", login);
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
