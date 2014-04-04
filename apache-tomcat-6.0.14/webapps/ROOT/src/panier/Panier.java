package panier;

import java.util.ArrayList;

public class Panier
{
	private ArrayList<ContenuPanier> _contenu;
	
	public Panier()
	{
		_contenu = new ArrayList<ContenuPanier>();
	}
	
	public void addContenu(ContenuPanier contenu)
	{
		_contenu.add(contenu);
	}
	
	public void removeContenu(int numContenu)
	{
		if(numContenu < _contenu.size())
		{
			_contenu.remove(numContenu);
		}
	}
	
	public ContenuPanier getContenu(int numContenu)
	{
		return _contenu.get(numContenu);
	}
	
	public int size()
	{
		return _contenu.size();
	}
	
	public void valider()
	{
		
	}
	
	public String toString()
	{
		String res = "";
		for(ContenuPanier c : _contenu)
		{
			res += c + "\n";
		}
		return res;
	}
}
