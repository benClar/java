import com.bclarke.testing.*;
import com.bclarke.general.*;

public class Record  {

private Field[] fields;

public Record(int size)	{
	fields = new Field[size];
	initializeArray(fields);
}

private void initializeArray(Field[] fA)	{
	for(int i = 0; i < fA.length; i++)	{
		fA[i] = null;
	}
}

public Field getField(int targetField)	{
	try	{
		return fields[targetField];
	} catch (ArrayIndexOutOfBoundsException e)	{
		WhiteBoxTesting.catchException(e, "Field does not exist");
		return null;
	}
}

public int changeField(String newFieldValue, int targetField)	{
	try	{
		fields[targetField].changeValue(newFieldValue);
	} catch (ArrayIndexOutOfBoundsException e)	{
		WhiteBoxTesting.catchException(e, "Field does not exist");
	}

	return 1;
}

public int getNumberOfFields()	{
	return fields.length;
}

public int addField(String fieldValue, FieldDataType newFieldType, int targetField)	{
	try	{
		if(fields[targetField] == null)	{
			fields[targetField] = new Field(fieldValue,newFieldType);
		} else	{
			throw new IllegalArgumentException();
		}
	} catch(IllegalArgumentException e)	{
		return WhiteBoxTesting.catchException(e, "Field Already Populated");
	} catch(Exception e)	{
		return WhiteBoxTesting.catchException(e, "Invalid attempt to add field");
	}

	return 1;
}


/*----------Testing----------*/

	public static void main( String[] args )    {
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			Record.unitTest(new Testing()).endTesting();
		}
	}

	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		Record r = new Record(5);

		t.enterSuite("Record Unit Tests");
		for(int i = 0; i < r.getNumberOfFields(); i++) {
			t.compare(null,"==",r.getField(i),"Array Element " + i + " Initialized to null");
		}
		t.compare(null,"==",r.getField(6),"Accessing out of bounds field");
		t.compare(1,"==",r.addField("test",FieldDataType.STRING,0),"Adding valid new String value to record");
		t.compare(0,"==",r.addField("newTest",FieldDataType.STRING,0),"Adding invalid new field to already existing field");
		t.compare(0,"==",r.addField("test",FieldDataType.STRING,6),"Adding invalid out of bounds field");
		t.compare("test","==",r.getField(0).getValue(),"Value of field 0 is test");
		r.getField(0).changeValue("AnotherTest");
		t.compare("AnotherTest","==",r.getField(0).getValue(),"Value of field 0 changed to AnotherTest");
		r.getField(0).changeValue(null);
		t.exitSuite();
		return t;
	}

}

