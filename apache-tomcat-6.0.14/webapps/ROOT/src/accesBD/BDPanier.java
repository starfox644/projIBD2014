package accesBD;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.text.ParseException;

import modele.Categorie;

import panier.ContenuPanier;
import panier.Panier;
import utils.Constantes;
import utils.ErrorLog;

import exceptions.ConnectionException;
import exceptions.RequestException;

public class BDPanier 
{
	public static Panier loadPanier(String login) throws ConnectionException, RequestException
	{
		System.out.println("entree dans load panier");
		SimpleDateFormat formatterOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		SimpleDateFormat formatterNew = new SimpleDateFormat(Constantes.dateFormat);

		Calendar calendar;
		Date tmpDate;
		String dateS;
		int heure;
		
		int numS;
		String nomS;
		String dateRep;
		String nomC;
		int prix;
		int nbPlaces;
		
		Categorie categorie;

		calendar = Calendar.getInstance();
		Panier panier = new Panier();
		SQLRequest request = new SQLRequest();
		String recupRequest = 
				"Select numS, nomS, dateRep, nomC, prix, nbPlaces "+
				"from LesPaniers natural join LesSpectacles natural join LesCategories " +
				"where login = '" + login + "'";
		ResultSet rs = request.execute(recupRequest);
		System.out.println("lecture panier");
		try {
			while(rs.next())
			{
				numS = rs.getInt(1);
				nomS = rs.getString(2);
				dateRep = rs.getString(3);
				nomC = rs.getString(4);
				prix = rs.getInt(5);
				nbPlaces = rs.getInt(6);
				// recuperation d'une string contenant la date sans l'heure
				tmpDate = formatterOld.parse(dateRep);
				dateS = formatterNew.format(tmpDate);
				// recuperation de l'heure dans un entier
				calendar.setTime(tmpDate);
				heure = calendar.get(Calendar.HOUR_OF_DAY);
				categorie = new Categorie(nomC, prix);
				ContenuPanier contenu = new ContenuPanier(numS, nomS, dateS, heure, nbPlaces, categorie);
				panier.addContenu(contenu);
			}
		} 
		catch(ParseException e)
		{
			throw new RequestException ("Erreur dans loadPanier \n"
					+ "Message " + e.getMessage());
		}
		catch (SQLException e) 
		{
			throw new RequestException ("Erreur dans loadPanier \n"
					+ "Code Oracle " + e.getErrorCode()
					+ "Message " + e.getMessage());
		}
		request.close();
		return panier;
	}
	
	public static boolean synchronizePanier(String login, Panier panier) throws ConnectionException, RequestException
	{
		boolean removeSuccess = false;

		String addReqBase = "INSERT INTO LesPaniers VALUES ('" + login + "', ";
		String addReq;
		ContenuPanier currContenu;

		SQLRequest request = new SQLRequest();
		removeSuccess = removePanier(request, login);
		if(removeSuccess)
		{
			if(panier.size() > 0)
			{
				for(int i = 0 ; i < panier.size() ; i++)
				{
					currContenu = panier.getContenu(i);
					addReq = addReqBase +
							currContenu.getNumS() + ", " +
							"to_date('" + currContenu.getDateS() + 
							" " + currContenu.getHeure()  + "', 'DD/MM/YY HH24'), '" +
							currContenu.getCategorie().getCategorie() + "', " +
							currContenu.getNbPlaces() + ")";
					request.execute(addReq);
				}
				request.commit();
			}
		}
		request.close();
		return removeSuccess;
	}

	public static boolean removePanier(SQLRequest request, String login) throws RequestException
	{
		String removeReq = 
				"delete from LesPaniers " +
						"where login = '" + login + "'";

		boolean loginPresent = false;
		loginPresent = BDLogin.isLoginPresent(request, login);
		request.execute(removeReq);
		return loginPresent;
	}
	
	public static boolean removePanier(String login) throws ConnectionException, RequestException
	{
		SQLRequest request = new SQLRequest();
		boolean loginPresent = false;
		
		loginPresent = removePanier(request, login);
		request.commit();
		request.close();
		return loginPresent;
	}
	
}
