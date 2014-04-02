package utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

public class InputParameters 
{
	private Hashtable<String, Parameter> _params;
	private LinkedList<String> _absentParams;
	private LinkedList<String> _paramNames;
	private LinkedList<String> _invalidParams;
	
	public InputParameters()
	{
		_params = new Hashtable<String, Parameter>();
		_paramNames = new LinkedList<String>();
		_absentParams = new LinkedList<String>();
		_invalidParams = new LinkedList<String>();
	}
	
	public void addParameter(String name, String description, ParameterType type)
	{
		Parameter param = new Parameter(name, description, type);
		_params.put(name, param);
		_paramNames.add(name);
	}
	
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
			System.out.println("name : " + name);
			System.out.println("null ? " + current.isNull());
			nullP &= current.isNull();
		}
		return nullP;
	}
	
	public boolean readParameters(HttpServletRequest req)
	{
		boolean success = true;
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
			success &= (current.isPresent() && current.isValid());
			if(!current.isPresent())
			{
				_absentParams.add(name);
			}
			else if(!current.isValid())
			{
				_invalidParams.add(name);
			}
		}
		return success;
	}
	
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
			//+ message + "</i></p>");
		}
		return strList;
	}
	
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
			form += "<input type=text size=20 name=" + name;
			if(current.isPresent())
			{
				form += " value = " + current.getRowValue();
			}
			form += ">";
			form += "<br>";
		}
		form += "<input type=submit>";
		form += "</form>";
		return form;
	}
	
	public String getStringParameter(String name)
	{
		return _params.get(name).getRowValue();
	}
	
	public int getIntParameter(String name)
	{
		return Integer.parseInt(_params.get(name).getRowValue());
	}
}
