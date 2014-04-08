package exceptions;

/**
 * 		Exception levee lorsque l'acces a la base echoue.
 */
public class ConnectionException extends Exception
{

	private static final long serialVersionUID = 1L;

	/**
	 * 		Cree une exception de connexion sans message.
	 */
	public ConnectionException() {
	}

	/**
	 * 		Cree une exception de connexion avec message.
	 * @param message Message de l'exception.
	 */
	public ConnectionException(String message) {
		super(message);
	}

}
