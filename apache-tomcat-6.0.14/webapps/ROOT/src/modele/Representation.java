package modele;

/**
 * 		Definit une representation de spectacle dans la base de donnees.
 */
public class Representation {

	private String nomS;	// nom spectacle
	private String dateRep;	// date de representation du spectacle
	private int numS;		// numero de la representation du spectacle
	
	/**
	 * 		Cree une representation a partir d'un nom de spectacle, d'une date et d'un numero.
	 * @param nom	Nom du spectacle.
	 * @param date	Date de la representation.
	 * @param num	Numero de la representation.
	 */
	public Representation (String nom, String date, int num) {
		this.nomS = nom;
		this.dateRep = date;
		this.numS = num;
	}

	public String getNom () {
		return this.nomS;
	}
	
	public String getDate() {
		return this.dateRep;
	}
	
	public int getNumero () {
		return this.numS;
	}
	public void setNom (String n) {
		this.nomS = n;
	}
	
	public void setDate (String d) {
		this.dateRep = d;
	}
	
	public void setNumero (int n) {
		this.numS = n;
	}
	
	
}
