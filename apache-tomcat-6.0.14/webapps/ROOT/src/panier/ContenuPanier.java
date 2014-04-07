package panier;

import exceptions.ConnectionException;
import exceptions.RequestException;
import exceptions.ReservationException;

import accesBD.BDPlaces;

import modele.Categorie;

/**
 * 		Definit une commande presente dans le panier de l'utilisateur.
 * 		Une commande est definie par une representation a une certaine date,
 * 		un nombre de places et une categorie dans laquelle choisir ces places.
 * 		Les places d'une meme commande sont selectionnees de facon contiguee
 * 		lors de la reservation.
 */
public class ContenuPanier
{
	private int _numS;
	private String _spectacle;
	private String _dateS;
	private int _heure;
	private int _nbPlaces;
	private Categorie _categorie;
	
	// indique que ce contenu est invalide apres une verification
	private boolean _invalid;
	// message de l'erreur si le contenu est invalide
	private String _error;
	
	/**
	 * 		Cree une nouvelle commande de panier.
	 * @param numS			Numero de spectacle.
	 * @param spectacle		Nom de spectacle.
	 * @param dateS 		Date de la representation sans l'heure, au format defini par Constantes.dateFormat.
	 * @param heure			Heure du spectacle.
	 * @param nbPlaces		Nombre de places a reserver.
	 * @param categorie		Categorie dans laquelle reserver les places.
	 */
	public ContenuPanier(int numS, String spectacle, String dateS, int heure, int nbPlaces, Categorie categorie)
	{
		_numS = numS;
		_spectacle = spectacle;
		_dateS = dateS;
		_heure = heure;
		_nbPlaces = nbPlaces;
		_categorie = categorie;
		_invalid = false;
		_error = "";
	}

	/**
	 * 		Renvoie le numero de spectacle de la commande.
	 * @return Numero de spectacle de la commande.
	 */
	public int getNumS() {
		return _numS;
	}

	/**
	 * 		Renvoie le nom de spectacle de la commande.
	 * @return Nom de spectacle de la commande.
	 */
	public String getSpectacle() {
		return _spectacle;
	}

	/**
	 * 		Renvoie la date de representation de la commande.
	 * @return Date de la representation de la commande, sans l'heure, 
	 * 			au format defini par Constantes.dateFormat.
	 */
	public String getDateS() {
		return _dateS;
	}

	/**
	 * 		Renvoie l'heure de representation de la commande.
	 * @return Heure de representation de la commande.
	 */
	public int getHeure() {
		return _heure;
	}

	/**
	 * 		Renvoie le nombre de places de la commande.
	 * @return Nombre de places de la commande.
	 */
	public int getNbPlaces() {
		return _nbPlaces;
	}

	/**
	 * 		Renvoie la categorie des places de la commande.
	 * @return Categorie de la commande.
	 */
	public Categorie getCategorie() {
		return _categorie;
	}

	/**
	 * 		Renvoie le prix total de la commande.
	 * @return Prix total de la commande.
	 */
	public float getPrixTotal() {
		return _categorie.getPrix() * _nbPlaces;
	}

	/**
	 * 		Indique si la commande est invalide.
	 * 		Le resultat de cette methode est mis a jour apres un appel a la methode check.
	 * @return true si la commande est devenue invalide, false si elle reste valide.
	 */
	public boolean isInvalid()
	{
		return _invalid;
	}

	/**
	 * 		Renvoie une description d'erreur affichable a l'utilisateur lorsque
	 * 		la commande est devenue invalide. (par exemple plus assez de places ou date depassee) 
	 * @return Un message d'erreur indiquant la raison de l'invalidite, affichable a l'utilisateur.
	 */
	public String getError()
	{
		return _error;
	}
	
	/**
	 * 		Ajoute une place a la commande.
	 */
	public void addPlace()
	{
		_nbPlaces++;
	}
	
	/**
	 * 		Retire une place a la commande si celle ci contient au moins 2 places.
	 */
	public void subPlace()
	{
		if(_nbPlaces > 1)
		{
			_nbPlaces--;
		}
	}
	
	/**
	 * 		Verifie si la commande est toujours disponible a partir de la methode BDPlaces.checkAjoutPanier.
	 * 		Si la commande n'est plus disponible, isInvalid() renvoie vrai et getError() renvoie
	 * 		un message decrivant la raison de l'invalidite de la commande. 
	 * @see BDPlaces.checkAjoutPanier
	 * 
	 * @throws RequestException		Si une erreur pendant la requete (erreur SQL) s'est produite.
	 * @throws ConnectionException	Si la connexion a la base de donnees n'a pu etre etablie.
	 */
	public void check() throws RequestException, ConnectionException
	{
		try {
			BDPlaces.checkAjoutPanier(_numS, _dateS, _heure, _nbPlaces, _categorie);
		}
		catch (ReservationException e) 
		{
			_invalid = true;
			_error = e.getMessage();
		}
	}
	
	/**
	 * 		Renvoie la commande sous forme de string pour le debug.
	 * 	@return La commande dans une string.
	 */
	public String toString()
	{
		String res = "";
		res += "num spectacle : " + _numS + "\n";
		res += "spectacle : " + _spectacle + "\n";
		res += "date : " + _dateS + "\n";
		res += "heure : " + _heure + "\n";
		res += "nb places : " + _nbPlaces + "\n";
		res += "categorie : " + _categorie.getNom() + "\n";
		res += "prix total : " + getPrixTotal() + "\n";
		return res;
	}
	
	/**
	 * 		Transforme le contenu du panier en une ligne pouvant etre placée dans un cookie.
	 * @return
	 */
	public String toCookieForm()
	{
		String res = "";
		res += _numS + "&#&";
		res += _spectacle + "&#&";
		res += _dateS + "&#&";
		res += _heure + "&#&";
		res += _nbPlaces + "&#&";
		res += _categorie.getNom() + "&#&";
		res += _categorie.getPrix();
		return res;
	}
	
	/**
	 * 		Recupere la commande a partir d'une chaine de caracteres presente dans un cookie.
	 * 		Les champs doivent etre separees par "&#&"
	 * @param ligne String contenant les champs de la commande.
	 * @return	Une commande pouvant etre ajoutee au panier ou null si la ligne ne contient pas 
	 * 			une commande au bon format.
	 */
	public static ContenuPanier getFromCookie(String ligne)
	{
		ContenuPanier contenu = null;
		Categorie categorie;
		float prixCategorie;
		int numS;
		int heure;
		int nbPlaces;
		// separation des champs de la commande
		String[] parts = ligne.split("&#&");
		// verification de la presence de 8 champs exactement dans la ligne
		if(parts.length == 7)
		{
			try
			{
				// recuperation des champs de la commande
				numS = Integer.parseInt(parts[0]);
				heure = Integer.parseInt(parts[3]);
				nbPlaces = Integer.parseInt(parts[4]);
				prixCategorie = Float.parseFloat(parts[6]);categorie = new Categorie(parts[5], prixCategorie);
				contenu = new ContenuPanier(numS, parts[1],parts[2], heure, nbPlaces, categorie);
			} catch(NumberFormatException e)
			{
				// en cas d'exception, la methode retourne null
				System.out.println("Erreur de format : " + e);
			}
		}
		return contenu;
	}
}
