package panier;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import exceptions.ConnectionException;
import exceptions.RequestException;

import accesBD.BDPanier;
import accesBD.Transaction;

/**
 * 		Represente le panier de l'utilisateur.
 * 		Il est sauvegarde dans la session, dans un cookie ou dans la base de donnees
 * 		selon l'utilisateur et son choix.
 * 		Il contient une liste de contenus associes aux commandes de l'utilisateur.
 */
public class Panier
{
	/** contenu du panier */
	private ArrayList<ContenuPanier> _contenu;

	/**
	 * 		Cree un panier vide.
	 */
	public Panier()
	{
		_contenu = new ArrayList<ContenuPanier>();
	}

	/**
	 * 		Ajout d'un contenu a la liste du panier.
	 * @param contenu Contenu a ajouter.
	 */
	public void addContenu(ContenuPanier contenu)
	{
		_contenu.add(contenu);
	}

	/**
	 * 		Supprime un contenu de la liste du panier.
	 * @param numContenu	Numero de contenu a retirer de la liste.
	 */
	public void removeContenu(int numContenu)
	{
		if(numContenu < _contenu.size())
		{
			_contenu.remove(numContenu);
		}
	}

	/**
	 * 		Retourne le contenu associe au numero donne.
	 * @param numContenu	Numero du contenu dans la liste du panier.
	 * @return	Le contenu associe au numero.
	 */
	public ContenuPanier getContenu(int numContenu)
	{
		return _contenu.get(numContenu);
	}

	/**
	 * 		Retourne le nombre de contenus du panier.
	 * @return	Nombre de contenus du panier.
	 */
	public int size()
	{
		return _contenu.size();
	}

	/**
	 * 		Retourne le prix total du panier
	 * @return	Prix total du panier.
	 */
	public float getPrixTotal()
	{
		float prix = 0;
		for(ContenuPanier contenu : _contenu)
		{
			prix += contenu.getPrixTotal();
		}
		return prix;
	}
	
	/**
	 * 		Verifie si les commandes du panier sont toujours valides.
	 * 		Appelle la methode check sur chaque contenu du panier.
	 * 
	 * @return	Une liste contenant les contenus de panier invalides.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public LinkedList<ContenuPanier> checkContenu() throws ConnectionException, RequestException
	{
		ContenuPanier contenu;
		LinkedList<ContenuPanier> invalidContenus = new LinkedList<ContenuPanier>();
		for(int i = 0 ; i < _contenu.size() ; i++)
		{
			contenu = _contenu.get(i);
			// verification du contenu (representation, nombre de places...)
			// met a jour la methode isInvalid du contenu
			contenu.check();
			if(contenu.isInvalid())
			{
				System.out.println("contenu invalide : " + contenu);
				// si le contenu est invalide, retrait du panier et ajout dans la liste resultat
				invalidContenus.add(contenu);
				_contenu.remove(contenu);
			}
		}
		return invalidContenus;
	}

	/**
	 * 		Retourne le panier de l'utilisateur.
	 * 		<br>
	 * 		Si le panier associe a la session n'est pas vide il est retourne et le champ synch
	 * 		de la session n'est pas modifie.
	 * 		<br>
	 * 		Sinon, le champ synch est mis a true.
	 * 		<br>
	 * 		Si l'utilisateur est identifie, son panier est charge a partir de la base.
	 * 		<br>
	 * 		S'il n'est pas identifie il est charge a partir d'un cookie.
	 * 		<br>
	 * 		Si le panier n'est trouve ni dans la base ni dans un cookie, un panier vide est
	 * 		cree.
	 * @param request	Requete transmise a la servlet.
	 * @return	Panier de l'utilisateur charge, ou vide si aucun panier n'est charge.
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public static Panier getUserPanier(HttpServletRequest request) throws ConnectionException, RequestException
	{
		HttpSession session = request.getSession();
		String login;
		Panier bdPanier;
		
		// recuperation du panier de la session en cours
		Panier panier = (Panier)session.getAttribute("panier");
		// recuperation du champ indiquant le choix de synchronisation de l'utilisateur
		Boolean synchSess = (Boolean)session.getAttribute("synch");
		// synchronisation activee par defaut
		boolean synch = true;
		
		// panier vide
		if(panier == null || panier.size() == 0)
		{
			if(synchSess == null)
			{
				synch = true;
			}
			else
			{
				synch = synchSess;
			}
			
			if(synch)
			{
				// recuperation du login de la session en cours
				login = (String)session.getAttribute("login");
				// si le login est present l'utilisateur est connecte
				if(login != null)
				{
					// panier vide et utilisateur loge -> chargement du panier dans la base
					bdPanier = BDPanier.loadPanier(login);
					if(bdPanier != null && bdPanier.size() != 0)
					{
						panier = bdPanier;
					}
				}
				else
				{
					// utilisateur non loge, recuperation du panier a partir des cookies
					Cookie[] cookies = request.getCookies();
					if(cookies != null)
					{
						// recherche du cookie contenant le panier
						Cookie cookiePanier;
						int i = 0;
						while(i < cookies.length && !cookies[i].getName().equals("panier"))
						{
							i++;
						}
						if(i < cookies.length)
						{
							// recuperation du panier a partir du cookie trouve
							cookiePanier = cookies[i];
							panier = getPanierFromCookie(cookiePanier);
							// si un panier a ete trouve, on active la synchronisation
							if(panier != null)
							{
								session.setAttribute("synch", true);
							}
						}
					}
				}
			}
		}
		else
		{
			// panier non vide et synchronisation non definie, on l'active par defaut
			if(synchSess == null)
			{
				session.setAttribute("synch", true);
			}
		}
		// si aucun panier n'a ete recupere on en cree un
		if(panier == null)
		{
			panier = new Panier();
		}
		
		// mise a jour du panier de la session en cours
		session.setAttribute("panier", panier);

		return panier;
	}
	
	/**
	 * 		Synchronise le panier de l'utilisateur recupere a partir de la session actuelle.
	 * @param request
	 * @param response
	 */
	public static void synchronizePanierSession(HttpServletRequest request, HttpServletResponse response)
	{
		Panier panier = (Panier)request.getSession().getAttribute("panier");
		if(panier != null)
		{
			panier.synchronize(request, response);
		}
	}
	
	/**
	 * 		Synchronise le panier.
	 * 		Si l'utilisateur est loge (champ login de la session non nul), le panier
	 * 		est synchronise a partir de la base de donnees. Si la valeur du champ synch de la
	 * 		session est false, les paniers de la base correspondant a l'utilisateur sont supprimes,
	 * 		sinon le panier de la session actuelle est sauvegarde dans la base avec le login de
	 * 		l'utilisateur.
	 * 
	 *		Si l'utilisateur n'est pas logge la synchronisation se fait pas cookies. Un cookie
	 *		contenant le contenu du panier est envoye si synch vaut true, sinon un cookie vide est
	 *		envoye pour effacer le contenu du panier.
	 *
	 *		Un champ synch non defini correspond a une valeur egale a true, la synchronisation
	 *		est activee par defaut.
	 *
	 * @param request
	 * @param response
	 */
	public void synchronize(HttpServletRequest request, HttpServletResponse response)
	{
		Cookie cookiePanier;
		boolean sendCookie = false;
		Boolean synch = true;
		HttpSession session = request.getSession();
		// mise a jour du panier de la session
		session.setAttribute("panier", this);
		// recuperation du login de l'utilisateur, a null si non loge
		String login = (String)session.getAttribute("login");
		// recuperation du choix de synchronisation de l'utilisateur
		synch = (Boolean)session.getAttribute("synch");
		
		if(synch == null)
		{	
			// synchronisation activee par defaut (si non definie)
			synch = true;
		}
		if(synch)
		{
			// panier non vide
			if(size() != 0)
			{
				if(login != null)
				{
					// utilisateur loge, on met a jour le panier de la base
					try {
						BDPanier.synchronizePanier(login, this);
					} catch (ConnectionException e) 
					{
						sendCookie = true;
					} catch (RequestException e) 
					{
						sendCookie = true;
					}
				}
				else
				{
					// utilisateur non loge, on envoie un cookie avec le panier
					sendCookie = true;
				}
			}
		}
		else
		{
			if(login != null)
			{
				// pas de synchronisation et utilisateur loge, on supprime le panier de la base
				try 
				{
					BDPanier.removePanier(login);
				} 
				// exceptions ignorees, un panier vide sera cree
				catch (ConnectionException e) 
				{
				} catch (RequestException e) 
				{
				}
			}
			// si l'utilisateur n'est pas loge le cookie ne sera pas envoye (sendCookie a false)
		}
		if(sendCookie)
		{
			// envoi d'un cookie contenant le panier
			cookiePanier = getCookieFromPanier();
			cookiePanier.setMaxAge(60*60*24*7);
		}
		else
		{
			// envoi d'un cookie vide pour effacer le contenu du panier
			cookiePanier = new Cookie("panier", "");
			cookiePanier.setMaxAge(0);
		}
		// ajout du cookie a la reponse
		response.addCookie(cookiePanier);
	}
	
	/**
	 * 		Vide le panier de la session de l'utilisateur.
	 * 		Si l'utilisateur est loge, son panier est retire de la base.
	 * @throws RequestException Si une erreur de requete survient pendant la suppression
	 *  		de la base du panier de l'utilisateur.
	 */							
	public static void clearUserPanier(Transaction transaction, HttpServletRequest request, HttpServletResponse response) throws RequestException
	{
		HttpSession session = request.getSession();
		Panier panier = (Panier)session.getAttribute("panier");
		String login = (String)session.getAttribute("login");
		if(panier != null)
		{
			panier.clear();
		}
		if(login != null)
		{
			// si l'utilisateur est loge, suppression de son panier de la base
			BDPanier.removePanier(transaction, login);
		}
		// envoi d'un cookie vide pour effacer le contenu du panier
		Cookie cookiePanier = new Cookie("panier", "");
		cookiePanier.setMaxAge(0);
		// ajout du cookie a la reponse
		response.addCookie(cookiePanier);
	}
	
	/**
	 * 		Retire tous les elements du panier.
	 */
	public void clear()
	{
		_contenu.clear();
	}

	/**
	 * 		Recupere le panier de l'utilisateur a partir d'un cookie envoye par celui-ci.
	 * @param cookie Cookie contenant le panier.
	 * @return Panier construit a partir de l'utilisateur ou null si le cookie ne contient pas
	 * 			de panier au format valide.
	 */
	public static Panier getPanierFromCookie(Cookie cookie)
	{
		Panier panier = new Panier();
		ContenuPanier contenu;
		String value = cookie.getValue();
		// recuperation des contenus a partir du separateur
		String parts[] = value.split("##&##");
		for(String current : parts)
		{
			// recuperation du contenu actuel
			contenu = ContenuPanier.getFromCookie(current);
			if(contenu != null)
			{
				panier.addContenu(contenu);
			}
			else
			{
				// contenu non recuperable a partir du cookie, la recuperation est annulee
				System.out.println("contenu de cookie illisible : " + contenu);
				return null;
			}
		}
		return panier;
	}

	/**
	 * 		Construit un cookie contenant le contenu du panier sous forme de chaine de caracteres.
	 * 		Le panier peut etre reconstruit a partir de ce cookie lorsqu'il est renvoye par le
	 * 		navigateur de l'utilisateur.
	 * @return	Cookie contenant tout le contenu du panier pour le sauvegarder.
	 */
	public Cookie getCookieFromPanier()
	{
		Cookie cookie;
		String value = "";
		for(ContenuPanier contenu : _contenu)
		{
			// ajout de chaque contenu separes par un ensemble special de caracteres
			value += contenu.toCookieForm() + "##&##";
		}
		cookie = new Cookie("panier", value);
		return cookie;
	}

	public String toString()
	{
		String res = "";
		for(ContenuPanier c : _contenu)
		{
			res += c + "\n";
		}
		return res;
	}
}
