package utils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

/**
 * 		Permet de gerer les parametres d'une servlet :
 * 		- recuperation
 * 		- generation de formulaire
 * 		- test de presence et de validite
 * 		- generation de messages d'erreurs
 */
public class InputParameters 
{
	/** table des parametres selon leur nom */
	private Hashtable<String, Parameter> _params;
	/** noms des parametres absents */
	private LinkedList<String> _absentParams;
	/** noms des parametres invalides */
	private LinkedList<String> _invalidParams;
	/** noms des parametres tries dans l'ordre de leur ajout (pour l'affichage du formulaire) */
	private LinkedList<String> _paramNames;
	/** noms des parametres valides. */
	private LinkedList<String> _validParams;
	/** indique l'absence d'erreurs de parametres (absents ou invalides) */
	private boolean _success;
	
	/**
	 * 		Cree un gestionnaire de parametres d'une page.
	 */
	public InputParameters()
	{
		_params = new Hashtable<String, Parameter>();
		_paramNames = new LinkedList<String>();
		_absentParams = new LinkedList<String>();
		_invalidParams = new LinkedList<String>();
		_validParams = new LinkedList<String>();
		_success = false;
	}
	
	/**
	 * 		Ajoute un nouveau parametre a recuperer.
	 * @param name			Nom du parametre tel qu'il est defini dans le formulaire.
	 * 						Ce nom permettra de le recuperer par la suite.
	 * @param description	Description du parametre affichee dans le formulaire et dans
	 * 						les messages d'erreur.
	 * @param type			Type du parametre pour la verification de validite.
	 */
	public void addParameter(String name, String description, ParameterType type)
	{
		Parameter param = new Parameter(description, type);
		_params.put(name, param);
		_paramNames.add(name);
	}
	
	/**
	 * 		Permet de veifier si tous les parametres valent null, ce qui correspond
	 * 		a la premiere génération de formulaire pour le client.
	 * @return true si tous les parametres valent null, false sinon.
	 */
	public boolean nullParameters()
	{
		String name;
		Parameter current;
		boolean nullP = true;
		Iterator<String> it = _paramNames.iterator();
		while(it.hasNext())
		{
			name = it.next();
			current = _params.get(name);
			nullP &= current.isNull();
		}
		return nullP;
	}
	
	/**
	 * 		Lit les parametres ajoutes grace à addParameter dans la requete
	 * 		et verifie leur validite.
	 * 		Apres l'appel a cette methode, la methode validParameters peut etre appelee.
	 * @see #validParameters()
	 * @param req	Requete du client sur la servlet.
	 */
	public void readParameters(HttpServletRequest req)
	{
		_success = true;
		Parameter current;
		String name;
		Iterator<String> it = _paramNames.iterator();
		_absentParams.clear();
		_invalidParams.clear();
		while(it.hasNext())
		{
			name = it.next();
			current = _params.get(name);
			current.setRowValue(req.getParameter(name));
			_success &= (current.isPresent() && current.isValid());
			if(!current.isPresent())
			{
				_absentParams.add(name);
			}
			else if(!current.isValid())
			{
				_invalidParams.add(name);
			}
			else
			{
				_validParams.add(name);
			}
		}
	}
	
	/**
	 * 		Indique si tous les parametres sont corrects. 
	 * 		Un parametre est considere correct s'il est present (non null et non vide)
	 * 		et si il correspond a son type defini par ParameterType.
	 * 		Si cette methode renvoie false, un message d'erreur au format HTML
	 * 		peut etre recupere grace à getHtmlError.
	 * @return true si les parametres sont valides, false sinon.
	 */
	public boolean validParameters()
	{
		return _success;
	}
	
	/**
	 * 		Renvoie la liste des noms des parametres valides.
	 * @return Liste des noms des parametres valides.
	 */
	public LinkedList<String> getValidParametersNames()
	{
		return _validParams;
	}
	
	/**
	 * 		Renvoie un message d'erreur automatiquement généré selon la description des paramètres.
	 * 		Ce message est de la forme :
	 * 		"Les champs description1, description2 sont absents"
	 * 		ou "Le champs description1 est absent"
	 * 		si des champs sont absents, suivi de 
	 * 		"Les champs description1, description2 sont invalides"
	 * 		ou "Le champs description1 est valide"
	 * 		si des champs sont invalides.
	 * @return Un message d'erreur généré selon la description des paramètres.
	 */
	public String getHtmlError()
	{
		String error = "";
		String tmp;
		if(!_absentParams.isEmpty())
		{
			error += "<p><i><font color=\"#FFFFFF\">";
			error += getStringList(_absentParams);
			if(_absentParams.size() == 1)
			{
				tmp = "absent";
			}
			else
			{
				tmp = "absents";
			}
			error += " " + tmp + "</i></p>";
		}
		if(!_invalidParams.isEmpty())
		{
			error += "<p><i><font color=\"#FFFFFF\">";
			error += getStringList(_invalidParams);
			if(_invalidParams.size() == 1)
			{
				tmp = "invalide";
			}
			else
			{
				tmp = "invalides";
			}
			error += " " + tmp + "</i></p>";
		}
		return error;
	}
	
	/**
	 * 		Renvoie une liste de descriptions suivant les noms de la liste passes en parametre,
	 * 		utilisee pour generer les messages d'erreur.
	 * @param list	Liste contenant les noms des paramètres dont les descriptions sont placées dans la liste.
	 * @return	Liste de descriptions séparées par des virgules.
	 */
	private String getStringList(LinkedList<String> list)
	{
		String strList = "";
		boolean begin;
		String name;
		Parameter current;
		Iterator<String> it;
		if(!list.isEmpty())
		{
			if(list.size() == 1)
			{
				strList += "Champ ";
			}
			else
			{
				strList += "Champs ";
			}
			it = list.listIterator();
			begin = true;
			while(it.hasNext())
			{
				if(begin)
				{
					begin = false;
				}
				else
				{
					strList += ", ";
				}
				name = it.next();
				current = _params.get(name);
				strList += current.getDescription().toLowerCase();
			}
		}
		return strList;
	}
	
	/**
	 * 		Renvoie le formulaire au format HTML genere selon les descriptions de paramètres.
	 * 		Les parametres recuperes sont places dans le formulaire a l'exception des champs
	 * 		de type PASSWD.
	 * @param invite		Titre du formulaire.
	 * @param link			Lien vers lequel l'utilisateur est redirige lors de la validation du formulaire.
	 * @return	String contenant le formulaire au format html a renvoyer au client.
	 */
	public String getHtmlForm(String invite, String link)
	{
		Iterator<String> it = _paramNames.iterator();
		Parameter current;
		String name;
		String form = "";

		form += "<font color=\"#FFFFFF\">" + invite + " :";
		form += "<P>";
		form += "<form action=\"";
		form += link + " \" ";
		form += "method=POST>";

		while(it.hasNext())
		{
			name = it.next();
			current = _params.get(name);
			form += current.getDescription() + " : ";
			if(current.getType() == ParameterType.PASSWD)
			{
				form += "<input type=password size=20 name=" + name;
				// pas de valeur par defaut pour le mot de passe par securite
			}
			else if(current.getType() != ParameterType.BOOLEAN)
			{
				form += "<input type=text size=20 name=" + name;
				if(current.isPresent())
				{
					form += " value = " + current.getRowValue();
				}
			}
			form += ">";
			form += "<br>";
		}
		form += "<input type=submit>";
		form += "</form>";
		return form;
	}
	
	/**
	 * 		Permet de recuperer la valeur d'un parametre sous forme de string.
	 * 		La methode readParameters doit etre appelee auparavant
	 * 		et le nom du parametre doit etre contenu dans la liste renvoyee
	 * 		par getValidParametersNames ou alors validParameters doit renvoyer true.
	 * @param name	Nom du paramètre à récupérer.
	 * @return String contenant la valeur du parametre.
	 */
	public String getStringParameter(String name)
	{
		return _params.get(name).getRowValue();
	}
	
	/**
	 * 		Permet de recuperer la valeur d'un parametre sous forme d'entier.
	 * 		La methode readParameters doit etre appelee auparavant
	 * 		et le nom du parametre doit etre contenu dans la liste renvoyee
	 * 		par getValidParametersNames ou alors validParameters doit renvoyer true.
	 * @param name	Nom du parametre à recuperer.
	 * @return int contenant la valeur du parametre.
	 */
	public int getIntParameter(String name)
	{
		return Integer.parseInt(_params.get(name).getRowValue());
	}
	
	/**
	 * 		Permet de recuperer la valeur d'un parametre sous forme de booleen.
	 * 		La methode readParameters doit etre appelee auparavant
	 * 		et le nom du parametre doit etre contenu dans la liste renvoyee
	 * 		par getValidParametersNames ou alors validParameters doit renvoyer true.
	 * @param name	Nom du parametre a recuperer.
	 * @return boolean contenant la valeur du paramètre.
	 */
	public boolean getBooleanParameter(String name)
	{
		return Boolean.parseBoolean(_params.get(name).getRowValue());
	}
}
