package loginServlets;

import javax.servlet.*;
import javax.servlet.http.*;

import panier.Panier;
import java.io.IOException;

import accesBD.BDLogin;
import exceptions.*;
import utils.*;

/**
 * 		Servlet permettant d'identifier un utilisateur en lui proposant
 * 		un formulaire.
 */
public class IdentificationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String invite = "Veuillez saisir votre nom d'utilisateur et votre mot de passe";
	private static final String formLink = "/Identification";

	/**
	 * 		Entree de la methode get.
	 * 		Si les parametres sont nuls, generation d'un formulaire.
	 * 		Sinon tentative d'identification de l'utilisateur a partir de la base.
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

		out.print(HtmlGen.htmlPreambule("Identification"));
		String login, passwd;
		HttpSession session;
		
		// parametres de la servlet
		InputParameters parameters = new InputParameters();
		parameters.addParameter("login", "Nom d'utilisateur", ParameterType.STRING);
		parameters.addParameter("password", "Mot de passe", ParameterType.PASSWD);
		
		parameters.readParameters(req);
		if(parameters.nullParameters())
		{
			// parametres nuls, generation du formulaire
			out.print(parameters.getHtmlForm(invite, formLink));
		}
		else
		{
			ErrorLog log = new ErrorLog();
			if(parameters.validParameters())
			{
				// recuperation du couple login / mot de passe
				login = parameters.getStringParameter("login");
				passwd = parameters.getStringParameter("password");
				try
				{
					// test de la correspondance des identifiants avec la base
					if(BDLogin.isLoginAccepted(login, passwd))
					{
						// si l'identification reussit, le login est mis dans la session
						session = req.getSession();
						session.setAttribute("login", login);
						Panier.synchronizePanierSession(req, res);
						out.println("Bonjour " + login +"<br>");
					}
					else
					{
						out.println("Login failed <br>");
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
