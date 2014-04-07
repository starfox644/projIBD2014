package exceptions;

/**
 * 		Exception levee par un objet Transaction lorsqu'une erreur
 * 		survient pendant une requete. 
 * 		Levee a partir d'une SQLException la plupart du temps.
 */
public class RequestException extends Exception
{
	/**
	 * 		Cree une exception indiquant une erreur au cours d'une requete.
	 * @param message	Message contenant la requete et l'erreur provoquee.
	 */
	public RequestException(String message) {
		super(message);
	}
}
