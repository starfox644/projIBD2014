package utils;

import java.util.Map;

/**
 * Rassemble les constantes de l'application
 * @author fauvet
 *
 */
public class Constantes {
	private final static String dirPathVar = "CATALINA_HOME";
	
	public static final String Menu =
		"Bienvenue - Theatre - Gestion des categories " + "\n";
	
	public static final String Invite = "Votre choix" ;
	
	private static String ConfigPath = "";
	
	private final static String relativeConfigPath = "/conf/connection.conf"; 
	
	private static String LogDirPath = "";
	
	private final static String relativeLogPath = "/logs";
	
	private static boolean isConfigPathInit = false;
	
	private static boolean isLogPathInit = false;
	
	public final static String dateFormat = "dd/MM/yyyy";
	
	private static void initConfigPath()
	{
		Map<String, String> env = System.getenv();
		String dir = env.get(Constantes.dirPathVar);
		if(dir == null)
		{
			System.out.println("Error : Catalina home variable not defined");
			throw new RuntimeException("Catalina home variable not defined");
		}
		else
		{
			ConfigPath = dir + relativeConfigPath;
		}
		isConfigPathInit = true;
	}
	
	private static void initLogDirPath()
	{
		Map<String, String> env = System.getenv();
		String dir = env.get(Constantes.dirPathVar);
		if(dir == null)
		{
			System.out.println("Error : Catalina home variable not defined");
			throw new RuntimeException("Catalina home variable not defined");
		}
		else
		{
			LogDirPath = dir + relativeLogPath;
		}
		isLogPathInit = true;
	}
	
	public static String getConfigPath()
	{
		if(!isConfigPathInit)
		{
			initConfigPath();
		}
		return ConfigPath;
	}
	
	public static String getLogPath()
	{
		if(!isLogPathInit)
		{
			initLogDirPath();
		}
		return LogDirPath;
	}
	
}
