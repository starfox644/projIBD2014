package modele;

public class Spectacle {

	private String nomS;	// nom spectacle
	private int numS;		// numero du spectacle
	
	// contructeur
	public Spectacle (int num, String nom) {
		this.nomS = nom;
		this.numS = num;
	}

	public String getNom () {
		return this.nomS;
	}
	
	public int getNumero () {
		return this.numS;
	}
	
	public void setNom (String n) {
		this.nomS = n;
	}
	
	public void setNumero (int n) {
		this.numS = n;
	}
}
