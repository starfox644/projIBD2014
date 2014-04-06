package accesBD;

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

import exceptions.ConnectionException;
import exceptions.RequestException;

public class BDPanier 
{
	/**
	 * 		Charge le panier associe a un nom d'utilisateur.
	 * @param login		Nom de l'utilisateur dont le panier est recupere.
	 * @return			Panier associe au nom de l'utilisateur.
	 * @throws ConnectionException
	 * @throws RequestException
	 */
	public static Panier loadPanier(String login) throws ConnectionException, RequestException
	{
		System.out.println("entree dans load panier");
		SimpleDateFormat formatterOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		SimpleDateFormat formatterNew = new SimpleDateFormat(Constantes.dateFormat);

		Calendar calendar;
		Date tmpDate;
		int heure, numS, prix, nbPlaces;
		String nomS, dateRep, nomC, dateS;
		
		Categorie categorie;

		calendar = Calendar.getInstance();
		Panier panier = new Panier();
		Transaction request = new Transaction();
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
	
	/**
	 * 		Synchronise le panier de l'utilisateur avec la base de donnees.
	 * 		Le panier de l'utilisateur present dans la base est supprime,
	 * 		puis le panier passe en parametre est ajoute.
	 * @param login		Nom de l'utilisateur dont le panier doit etre synchronise.
	 * @param panier	Panier a synchroniser avec la base.
	 * @return			True si le panier a ete synchronise, false si l'utilisateur n'est
	 * 					pas present dans la base.
	 * @throws ConnectionException
	 * @throws RequestException
	 */
	public static boolean synchronizePanier(String login, Panier panier) throws ConnectionException, RequestException
	{
		boolean removeSuccess = false;

		String addReqBase = "INSERT INTO LesPaniers VALUES ('" + login + "', ";
		String addReq;
		ContenuPanier currContenu;

		Transaction request = new Transaction();
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

	/**
	 * 		Supprime le panier d'un utilisateur dans la base en utilisant la transaction
	 * 		passee en parametre.
	 * @param request		Transaction a utiliser.
	 * @param login			Nom de l'utilisateur dont le panier doit etre supprime.
	 * @return	true si le panier est supprime, false si l'utilisateur n'est pas present.
	 * @throws RequestException
	 */
	public static boolean removePanier(Transaction request, String login) throws RequestException
	{
		String removeReq = 
				"delete from LesPaniers " +
						"where login = '" + login + "'";

		boolean loginPresent = false;
		loginPresent = BDLogin.isLoginPresent(request, login);
		request.execute(removeReq);
		return loginPresent;
	}
	
	/**
	 * 		Supprime le panier d'un utilisateur dans la base.
	 * @param login			Nom de l'utilisateur dont le panier doit etre supprime.
	 * @return	true si le panier est supprime, false si l'utilisateur n'est pas present.
	 * @throws RequestException
	 */
	public static boolean removePanier(String login) throws ConnectionException, RequestException
	{
		Transaction request = new Transaction();
		boolean loginPresent = false;
		
		loginPresent = removePanier(request, login);
		request.commit();
		request.close();
		return loginPresent;
	}
	
}
