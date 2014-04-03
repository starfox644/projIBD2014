package utils;

public class HtmlGen 
{
	public static String htmlPreambule(String title)
	{
		String res = "";
		res += "<HEAD><TITLE>" + title + "</TITLE></HEAD>";
		res += "<BODY bgproperties=\"fixed\" background=\"/images/rideau.JPG\" " +
				"link=\"#FFFFFF\" vlink=\"#D0D0D0\" alink=\"#E0E0E0\">";
		res += "<font color=\"#FFFFFF\"><h1>" + title + " </h1>";
		res += "<p><i><font color=\"#FFFFFF\">";
		return res;
	}
}
