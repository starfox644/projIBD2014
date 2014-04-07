package modele;

/**
 * 		Definit une categorie de place de la salle de theatre.
 */
public class Categorie {

	private String nom;
	private float prix;
	
	/**
	 * 		Cree une categorie avec un nom et un prix par place.
	 * @param nom Nom de la categorie.
	 * @param prix Prix par place de la categorie.
	 */
	public Categorie (String nom, float prix) {
		this.nom = nom;
		this.prix = prix;
	}

	/**
	 * 		Renvoie le nom de la categorie.
	 * @return Nom de la categorie.
	 */
	public String getNom () {
		return this.nom;
	}
	
	/**
	 * 		Renvoie le prix par place de la categorie.
	 * @return Prix par place de la categorie.
	 */
	public float getPrix () {
		return this.prix;
	}
}
