package panier;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import exceptions.ConnectionException;
import exceptions.RequestException;

import accesBD.BDPanier;

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
	
	public int getPrixTotal()
	{
		int prix = 0;
		for(ContenuPanier contenu : _contenu)
		{
			prix += contenu.getPrixTotal();
		}
		return prix;
	}
	
	public void valider()
	{
		
	}
	
	public static Panier getUserPanier(HttpSession session) throws ConnectionException, RequestException
	{
		String login = (String)session.getAttribute("login");
		Panier panier = (Panier)session.getAttribute("panier");
		if((panier == null || panier.size() == 0) && login != null)
		{
			panier = BDPanier.loadPanier(login);
			if(panier.size() != 0)
			{
				session.setAttribute("synch", true);
			}
		}
		else if(panier == null)
		{
			panier = new Panier();
		}
		session.setAttribute("panier", panier);
		return panier;
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
