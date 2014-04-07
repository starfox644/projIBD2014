package modele;

/**
 * 		Definit un ticket dans la base de donnees.
 */
public class Ticket {

	private int noSerie;
	private int numS;
	private String dateRep;
	private int noPlace;
	private int noRang;
	private String dateEmission;
	
	public Ticket (int ns, int num, String dateR, int p, int r, String dateEm)
	{
		this.noSerie = ns;
		this.numS = num;
		this.dateRep = dateR;
		this.noPlace = p;
		this.noRang = r;
		this.dateEmission = dateEm;
	}
	
	public int getNoSerie() {
		return noSerie;
	}
	
	public void setNoSerie(int noSerie) {
		this.noSerie = noSerie;
	}
	
	public int getNumS() {
		return numS;
	}
	
	public void setNumS(int numS) {
		this.numS = numS;
	}
	
	public String getDateRep() {
		return dateRep;
	}
	
	public void setDateRep(String dateRep) {
		this.dateRep = dateRep;
	}
	
	public int getNoPlace() {
		return noPlace;
	}
	public void setNoPlace(int noPlace) {
		this.noPlace = noPlace;
	}
	
	public int getNoRang() {
		return noRang;
	}
	
	public void setNoRang(int noRang) {
		this.noRang = noRang;
	}

	public String getDateEmission() {
		return dateEmission;
	}
	
	public void setDateEmission(String dateEmission) {
		this.dateEmission = dateEmission;
	}
	
	
	public String toString()
	{
		
		String str = new String ("noSerie : " + noSerie + "\n numS : " + numS + "\n dateRep : " + dateRep 
								+ "\n noPlace : " + noPlace + "\n noRang : " + noRang + "\n dateEmission : " 
								+ dateEmission + "\n");
		return str;
	}
	
	
	
}
