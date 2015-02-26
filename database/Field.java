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
		t.compare("TestField","==",testField.getValue(),"Field created with value TestField");
		testField.changeValue("NewTestValue");
		t.compare("NewTestValue","==",testField.getValue(),"Field value changed to NewTestValue");
		t.exitSuite();
		return t;
		}

}

