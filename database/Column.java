import com.bclarke.testing.*;
import com.bclarke.general.*;

public class Column {

	private String columnName;
	private FieldDataType columnType;
	private int longestFieldSize;
	private FieldDataType keyStatus;

	public Column(String cName, FieldDataType t, FieldDataType key)	{
		columnName = cName;
		columnType = t;
		longestFieldSize = cName.length();
		keyStatus = key;
	}

	public Column(String cName, FieldDataType t)	{
		columnName = cName;
		columnType = t;
		longestFieldSize = cName.length();
		keyStatus = FieldDataType.NONKEY;
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

