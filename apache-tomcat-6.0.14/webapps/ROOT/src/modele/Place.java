package modele;

/**
 * 		Definit une place de la salle de theatre.
 */
public class Place {
	
	private int noPlace; // numero de la place
	private int noRang; // numero du rang
	private int numZ; 	// numero de la zone
	
	/**
	 * 		Cree une place associee a une zone, un rang et un numero.
	 * @param noPlace	Numero de la place dans le rang.
	 * @param noRang	Numero du rang de la place.
	 * @param noZone	Numero de zone de la place.
	 */
	public Place (int noPlace, int noRang, int noZone) {
		this.noPlace = noPlace;
		this.noRang = noRang;
		this.numZ = noZone;
	}

	/**
	 * 		Renvoie le numero de la place dans son rang.
	 * @return Numero de la place dans son rang.
	 */
	public int getNoPlace () {
		return this.noPlace;
	}
	
	/**
	 * 		Renvoie le numero de rang de la place.
	 * @return Numero de rang de la place.
	 */
	public int getNoRang () {
		return this.noRang;
	}

	/**
	 * 		Renvoie le numero de zone de la place.
	 * @return Numero de zone de la place.
	 */
	public int getNumZ () {
		return this.numZ;
	}
}
