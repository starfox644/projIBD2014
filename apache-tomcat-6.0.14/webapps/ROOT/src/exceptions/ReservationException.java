package exceptions;

/**
 * 		Exception levee lorsqu'une reservation est impossible.
 */
public class ReservationException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * 		Cree une exception lorsqu'une reservation est impossible.
	 * @param message Message decrivant la raison de l'echec de reservation,
	 * 				  pouvant etre directement affiche a l'utilisateur.
	 */
	public ReservationException(String message)
	{
		super(message);
	}
}
