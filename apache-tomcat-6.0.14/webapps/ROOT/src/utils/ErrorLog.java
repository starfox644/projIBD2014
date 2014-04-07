package utils;

import java.io.IOException;

/**
 *		Definit un moyen d'ecriture des exceptions des servlets dans un fichier de log dedie
 *		pour faciliter le debug et le tracage des erreurs.
 */
public class ErrorLog extends Log
{
	/**
	 * 		Cree un acces au fichier de log d'exceptions.
	 * @throws IOException	Si l'acces au fichier de log est impossible.
	 */
	public ErrorLog() throws IOException 
	{
		super("error");
	}
	
	/**
	 * 		Ecrit une exception dans le fichier de log dedie.
	 * 		Le nom de la classe de l'exception est ecrit, ainsi que son message.
	 * @param e	Exception a ecrire dans le fichier de log des exceptions.
	 */
	public void writeException(Exception e)
	{
		String exRep = "Exception name : " + e.getClass() + "\n";
		exRep += "Message : \n " + e.getMessage() + "\n"; 
		write(exRep);
	}
	
}
