import com.bclarke.testing.*;
import com.bclarke.general.*;

public class Column {

	private String columnName;
	private FieldDataType columnType;
	private int longestFieldSize;
	private FieldDataType keyStatus;
	private String references;

	public Column(String cName, FieldDataType t, FieldDataType key)	{
		columnName = cName;
		columnType = t;
		longestFieldSize = cName.length();
		keyStatus = key;
		setReference(null);
	}

	public Column(String cName, FieldDataType t)	{
		columnName = cName;
		columnType = t;
		longestFieldSize = cName.length();
		keyStatus = FieldDataType.NONKEY;
		setReference(null);
	}

	public Column(String cName, FieldDataType t, FieldDataType key, String ref)	{
		columnName = cName;
		columnType = t;
		longestFieldSize = cName.length();
		keyStatus = key;
		setReference(validateReference(ref,key));
	}

	private String validateReference(String reference, FieldDataType key)	{
		if(key != FieldDataType.FKEY)	{
			return null;
		}

		return reference;
	}

	public void setReference(String newReference)	{
		references = newReference;
	}

	public String getReference()	{
		try	{
			if(references != null)	{
				return references;
			} else	{
				if(keyStatus == FieldDataType.FKEY)	{
					throw new Exception();
				}
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Column is a foreign key without a reference");
		}
		return null;
	}

	public String toString()	{
		if(keyStatus ==FieldDataType.FKEY)	{
			return new String("{Name: " + columnName + "} {Type: " + columnType + "} {Key Type:" + keyStatus.toString() + "->" + references + "}");
		} else	{
			return new String("{Name: " + columnName + "} {Type: " + columnType + "} {Key Type:" + keyStatus.toString() + "}");
		}

	}

	public Column copyOf()	{
		StringBuffer copyName = new StringBuffer();
		copyName.append(columnName);
		return new Column(new String(copyName),columnType,keyStatus);
	}

	public void setKeyType(FieldDataType newType)	{
		try	{
		if(newType != FieldDataType.NONKEY && newType != FieldDataType.PKEY)	{
			throw new Exception("Tried to set key field to data type");
		} else	{
			keyStatus = newType;
		}
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Internal error");
		}

	}

	public String validateColumnName(String cName)	{
		if(cName != "")	{
			return cName;
		} 
		try	{
			throw new Exception("Invalid Column Name");
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Column Name invalid");
			return null;
		} 
	}

	public FieldDataType getKeyType()	{
		return keyStatus;
	}

	public int getLongestFieldSize()	{
		return longestFieldSize;
	}

	public void setLongestFieldSize(int newLength)	{
		longestFieldSize = newLength;
	}

	public FieldDataType getColumnType()	{
		return columnType;
	}

	public String getColumnName()	{
		return columnName;
	}

	public void setColumnName(String newName)	{
		columnName = newName;
	}

	public void setColumnType(FieldDataType newColumnType)	{
		columnType = newColumnType;
	}


/*----------Testing----------*/

	public static void main( String[] args )    {
	 
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			Column.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		// WhiteBoxTesting.startTesting();
		// t.enterSuite("Column Unit Tests");
		// /*Unit Tests Here*/
		// t.exitSuite();
		return t;
	}
}

