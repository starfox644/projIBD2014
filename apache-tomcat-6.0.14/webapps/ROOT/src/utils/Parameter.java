package utils;


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
	
	public Parameter(String name, String description, ParameterType type)
	{
		_name = name;
		_description = description;
		_type = type;
		_rowValue = null;
	}
	
	public void setRowValue(String rowValue)
	{
		_rowValue = rowValue;
	}
	
	public String getDescription()
	{
		return _description;
	}
	
	public String getRowValue()
	{
		return _rowValue;
	}
	
	public ParameterType getType()
	{
		return _type;
	}
	
	public boolean isPresent()
	{
		return (_rowValue != null && !_rowValue.equals(""));
	}
	
	public boolean isNull()
	{
		return (_rowValue == null);
	}
	
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
