package utils;

/**
 * 		Definit un parametre recupere automatiquement par un objet de type InputParameters
 * 		a partir des parametres envoyes a une servlet.
 */
public class Parameter
{
	/** valeur directement obtenue du formulaire */
	private String _rowValue;
	/** nom du parametre */
	private final String _name;
	/** description du parametre affichee dans le formulaire */
	private final String _description;
	/** type du parametre */
	private final ParameterType _type;
	
	/**
	 * 		Cree un parametre avec un nom, une description et un type.
	 * @param name			Nom du parametre, pour le referencer.
	 * @param description	Description du parametre affichee dans les formulaires et les erreurs.
	 * @param type			Type du parametre pour la verification.
	 */
	public Parameter(String name, String description, ParameterType type)
	{
		_name = name;
		_description = description;
		_type = type;
		_rowValue = null;
	}
	
	/**
	 * 		Specifie la valeur du parametre directement recuperee a partir des parametres de la servlet.
	 * @param rowValue
	 */
	public void setRowValue(String rowValue)
	{
		_rowValue = rowValue;
	}
	
	/**
	 * 		Renvoie la description du parametre.
	 * @return	String contenant la description du parametre.
	 */
	public String getDescription()
	{
		return _description;
	}
	
	/**
	 * 		Renvoie la valeur du parametre sans conversion.
	 * @return	String contenant la valeur du parametre.
	 */
	public String getRowValue()
	{
		return _rowValue;
	}
	
	/**
	 * 		Renvoie le type du parametre.
	 * @return	Type du parametre.
	 */
	public ParameterType getType()
	{
		return _type;
	}
	
	/**
	 * 		Indique si le parametre est present dans les parametres de la servlet.
	 * 		Un parametre est considere present si il est non nul et non vide.
	 * @return true si le parametre est present, false sinon.
	 */
	public boolean isPresent()
	{
		return (_rowValue != null && !_rowValue.equals(""));
	}
	
	/**
	 * 		Indique si la valeur du parametre est null.
	 * @return true si la valeur du parametre est null, false sinon.
	 */
	public boolean isNull()
	{
		return (_rowValue == null);
	}
	
	/**
	 * 		Indique si le parametre est non nul et de type correct.
	 * @return true si le parametre est valide, false sinon.
	 */
	public boolean isValid()
	{
		boolean valid;
		if(_rowValue == null)
		{
			valid = false;
		}
		else
		{
			switch(_type)
			{
				case INTEGER:
					valid = Utilitaires.validIntegerFormat(_rowValue);
					break;
				case DATE:
					valid = Utilitaires.validDateFormat(_rowValue);
					break;
				case HOUR:
					valid = Utilitaires.validHourFormat(_rowValue);
					break;
				case BOOLEAN:
					valid = (_rowValue.equals("true") || _rowValue.equals("false"));
					break;
				default:
					// String et passwd ne provoquent pas d'erreurs (chaines de caracteres)
					valid = true;
					break;
			}
		}
		return valid;
	}
	
}
