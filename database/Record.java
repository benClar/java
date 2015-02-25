import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.*;

public class Record  {

private ArrayList<Field> fields;

public Record(Field[] f)	{
	fields = new ArrayList<Field>();
	addNewFields(f);
}

private void nullifyRow(ArrayList<Field> fA)	{
	for(int i = 0; i < fA.size(); i++)	{
		fA.set(i,null);
	}
}

public Field getField(int targetField)	{
	try	{
		return fields.get(targetField);
	} catch (IndexOutOfBoundsException e)	{
		WhiteBoxTesting.catchException(e, "Field does not exist");
		return null;
	}
}

public int changeField(String newFieldValue, int targetField)	{
	try	{
		fields.get(targetField).changeValue(newFieldValue);
	} catch (IndexOutOfBoundsException e)	{
		WhiteBoxTesting.catchException(e, "Field does not exist");
	}

	return 1;
}

public int getNumberOfFields()	{
	return fields.size();
}

public int insertValue(String fieldValue, FieldDataType newFieldType, int targetField)	{
	try	{
		fields.set(targetField,new Field(fieldValue,newFieldType));
	} catch(Exception e)	{
		return WhiteBoxTesting.catchException(e, "Invalid attempt to add field");
	}

	return 1;
}

public int overwriteRow(Field[] newFields)	{
	try	{
		if(newFields.length != this.getNumberOfFields())	{
			throw new IllegalArgumentException();
		}
		for(int i = 0; i < newFields.length; i++)	{
			if(fields.get(i) == null)	{
				fields.set(i, newFields[i]);
			} else	{
				throw new IllegalArgumentException();
			}
		}
	} catch(IllegalArgumentException e)	{
		return WhiteBoxTesting.catchException(e, "Field Already Populated");
	} catch(Exception e)	{
		return WhiteBoxTesting.catchException(e, "Invalid attempt to add field");
	}

	return 1;
}

public void addNewFields(Field[] newFields)	{
	for(int i = 0; i < newFields.length; i++)	{
		fields.add(newFields[i]);
	}
}


/*----------Testing----------*/

	public static void main( String[] args )    {
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			Record.unitTest(new Testing()).endTesting();
		}
	}

	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		
		Field[] f = new Field[5];

		for(int i = 0; i < f.length; i++)	{
			f[i] = new Field("Test" + i, FieldDataType.STRING);
		}
		Record r = new Record(f);

		t.enterSuite("Record Unit Tests");
		t.compare(null,"==",r.getField(6),"Accessing out of bounds field");
		t.compare(1,"==",r.insertValue("test",FieldDataType.STRING,0),"Adding valid new String value to record");
		t.compare("test","==",r.getField(0).getValue(),"Value of field 0 is test");
		r.getField(0).changeValue("AnotherTest");
		t.compare("AnotherTest","==",r.getField(0).getValue(),"Value of field 0 changed to AnotherTest");
		r.getField(0).changeValue(null);
		r.nullifyRow(r.fields);
		for(int i = 0; i < r.getNumberOfFields(); i++) {
			t.compare(null,"==",r.getField(i),"Array Element " + i + " Initialized to null");
		}

		r.overwriteRow(f);
		for(int i = 0; i < f.length; i++)	{
			t.compare("Test" + i,"==",r.getField(i).getValue(),"Value of all fields in new record is test"+i);
		}

		Field[] errFieldTooLong = new Field[6];
		t.compare(0,"==",r.overwriteRow(errFieldTooLong),"Adding too many records to row");
		Field[] errFieldTooShort = new Field[4];
		t.compare(0,"==",r.overwriteRow(errFieldTooShort),"Adding too few records to row");
		r.addNewFields(f);
		t.compare(10,"==",r.getNumberOfFields(),"record size is now 10: 5 new fields added");
		t.exitSuite();
		return t;
	}
}

