package modele;

public class Representation {

	private String nomS;	// nom spectacle
	private String dateRep;	// date de representation du spectacle
	private int numS;		// numero de la representation du spectacle
	
	// contructeur
	public Representation (String nom, String date, int num) {
		this.nomS = nom;
		this.dateRep = date;
		this.numS = num;
	}

	public String getNom () {
		return this.nomS;
	}
	
	public String getDate () {
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
