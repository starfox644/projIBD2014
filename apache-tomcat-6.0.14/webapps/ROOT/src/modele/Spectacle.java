package modele;

/**
 * 		Definit un spectacle dans la base de donnees.
 */
public class Spectacle {

	private String nomS;	// nom spectacle
	private int numS;		// numero du spectacle
	
	/**
	 * 		Cree un spectacle a partir de son nom et de son numero.
	 * @param num 	Numero du spectacle.
	 * @param nom	Nom du spectacle.
	 */
	public Spectacle (int num, String nom) {
		this.nomS = nom;
		this.numS = num;
	}

	/**
	 * 		Renvoie le nom du spectacle.
	 * @return Nom du spectacle.
	 */
	public String getNom () {
		return this.nomS;
	}
	
	/**
	 * 		Renvoie le numero du spectacle.
	 * @return	Numero du spectacle.
	 */
	public int getNumero () {
		return this.numS;
	}
}
