import com.bclarke.testing.*;
import com.bclarke.general.*;

public class Field  {

	private String value;
	private FieldDataType type;

	public Field(String v, FieldDataType t)	{
		value = v;
		type = t;
	}

	public String getValue()	{
		return value;
	}

	/* private String validateString(String newValue)	{
		try	{
			if(newValue != null)	{
				return newValue;
			} else	{
				throw new IllegalArgumentException();
			}
		}	catch(Exception e)	{
			WhiteBoxTesting.catchException(e,"Invalid string");
			return null;
		}

	}*/

	public void changeValue(String newValue)	{
		value = newValue;
	}

/*----------Testing----------*/

	public static void main( String[] args )    {
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			Field.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Field Unit Tests");
		Field testField = new Field("TestField",FieldDataType.STRING);
		/*t.compare(null,"==",testField.validateString(null),"Validating invalid null value");
		t.compare("pass","==",testField.validateString("pass"),"Validating valid String value");*/
		t.exitSuite();
		return t;
		}

}

