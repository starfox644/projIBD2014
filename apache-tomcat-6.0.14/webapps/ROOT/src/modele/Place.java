package modele;

public class Place {
	
	private int noPlace; // numero de la place
	private int noRang; // numero du rang
	private int numZ; 	// numero de la zone
	
	// contructeur
	public Place (int np, int nr, int nz) {
		this.noPlace = np;
		this.noRang = nr;
		this.numZ = nz;
	}

	public int getNoPlace () {
		return this.noPlace;
	}
	
	public int getNoRang () {
		return this.noRang;
	}
	
	public int getNumZ () {
		return this.numZ;
	}
	public void setNoPlace (int n) {
		this.noPlace = n;
	}
	
	public void setNoRang (int r) {
		this.noRang = r;
	}
	
	public void setNumZ (int z) {
		this.numZ = z;
	}
}
