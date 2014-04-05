package panier;

import modele.Categorie;

public class ContenuPanier
{
	private int _numS;
	private String _spectacle;
	private String _dateS;
	private int _heure;
	private int _nbPlaces;
	private Categorie _categorie;
	private float _prixTotal;
	
	public ContenuPanier(int numS, String spectacle, String dateS, int heure, int nbPlaces, Categorie categorie)
	{
		_numS = numS;
		_spectacle = spectacle;
		_dateS = dateS;
		_heure = heure;
		_nbPlaces = nbPlaces;
		_categorie = categorie;
		_prixTotal = categorie.getPrix() * nbPlaces;
	}

	public int getNumS() {
		return _numS;
	}

	public String getSpectacle() {
		return _spectacle;
	}

	public String getDateS() {
		return _dateS;
	}

	public int getHeure() {
		return _heure;
	}

	public int getNbPlaces() {
		return _nbPlaces;
	}

	public Categorie getCategorie() {
		return _categorie;
	}

	public float getPrixTotal() {
		return _prixTotal;
	}
	
	public String toString()
	{
		String res = "";
		res += "num spectacle : " + _numS + "\n";
		res += "spectacle : " + _spectacle + "\n";
		res += "date : " + _dateS + "\n";
		res += "heure : " + _heure + "\n";
		res += "nb places : " + _nbPlaces + "\n";
		res += "categorie : " + _categorie.getCategorie() + "\n";
		res += "prix total : " + _prixTotal;
		return res;
	}
}
