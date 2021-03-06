package loginServlets;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;

/**
 * 		Servlet gerant la deconnexion de l'utilisateur.
 * 		Deconnecte l'utilisateur et le redirige vers la page de connexion
 * 		sans generer de page html.
 */
public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

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
		res.setContentType("text/html");
		
		HttpSession session = req.getSession();
		session.removeAttribute("login");
		RequestDispatcher dispatcher = req.getRequestDispatcher("/");
		dispatcher.forward(req, res);
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
