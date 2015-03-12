import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.*;

public class Record implements Comparable<Record> {

// public class Record {

private ArrayList<Field> fields;
private int keyField;

private final int BITSHIFT = 3;

public Record(Field[] f, int keyField)	{
	fields = new ArrayList<Field>();
	addNewFields(f);
}

@Override
//! May need to include comparison of floating points.
 public int compareTo(Record r) {
 	if(ifInteger(this.getPrimaryKeyValue()))	{
 		return (Integer.parseInt(this.getPrimaryKeyValue()) -  Integer.parseInt(r.getPrimaryKeyValue()));
 	} else	{
 		return this.getPrimaryKeyValue().compareToIgnoreCase(r.getPrimaryKeyValue());
 	}
 }

 public String getPrimaryKeyValue()	{
 	return getField(keyField).getValue();
 }

 private boolean ifInteger(String val)	{
 	try	{
 		Integer.parseInt(val);
 	} catch (NumberFormatException e){
 		return false;
 	}
 	return true;
 }

private void nullifyRow(ArrayList<Field> fA)	{
	for(int i = 0; i < fA.size(); i++)	{
		fA.set(i,null);
	}
}

@Override
public int hashCode()	{
	int code = 0;
	for(int i = 0; i < fields.size(); i++)	{
		code = code >> BITSHIFT;
		code += fields.get(i).getFieldValue().hashCode();
	}	
	return code;
}

@Override
public boolean equals(Object obj)	{
	if(obj.toString().equals(this.toString()))	{
		return true;
	}
	return false;

}

@Override
public String toString()	{
	String value = "";
	for(int i = 0; i < fields.size(); i++)	{
		value += fields.get(i).getFieldValue();
		if( i != fields.size() - 1)	{
			value += ",";
		}
	}

	return value;
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
		unitTest_populatingRecord(t);
		unitTest_RecordHashing(t);
		
		return t;
	}

	public static Testing unitTest_populatingRecord(Testing t)	{
		WhiteBoxTesting.startTesting();		
		Field[] f = new Field[5];

		for(int i = 0; i < f.length; i++)	{
			if(i == 0)	{
				f[i] = new Field("Test" + i, FieldDataType.PKEY);
			} else {
				f[i] = new Field("Test" + i, FieldDataType.STRING);
			}
		}
		Record r = new Record(f,1);
		t.enterSuite("Record Unit Tests: Populating Record");
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

	public static Testing unitTest_RecordHashing(Testing t)	{
		WhiteBoxTesting.startTesting();	
		Field[] f = new Field[3];
		f[0] = new Field("a",FieldDataType.STRING);
		f[1] = new Field("b",FieldDataType.STRING);
		f[2] = new Field("c",FieldDataType.STRING);
		Record r1 = new Record(f,1);
		f[0] = new Field("b",FieldDataType.STRING);
		f[1] = new Field("c",FieldDataType.STRING);
		f[2] = new Field("a",FieldDataType.STRING);
		Record r2 = new Record(f,1);
		f[0] = new Field("c",FieldDataType.STRING);
		f[1] = new Field("a",FieldDataType.STRING);
		f[2] = new Field("b",FieldDataType.STRING);
		Record r3 = new Record(f,1);
		Record r4 = new Record(f,1);
		t.compare("a,STRING,b,STRING,c,STRING","==",r1.toString(),"Field Value is a,PKEY,b,STRING,c,STRING");
		t.compare("b,STRING,c,STRING,a,STRING","==",r2.toString(),"Field Value is b,PKEY,c,STRING,a,STRING"); 
 		t.compare(true,"==",r3.equals(r4),"r3 record equals r4");
 		t.compare(false,"==",r3.equals(r1),"r3 record not equal to r1");
 		t.compare(r4.hashCode(),"==",r3.hashCode(),"r3 hashcode equals r4 hashcode");
 		t.compare(r1.hashCode(),"!=",r3.hashCode(),"r1 hashcode does not equal r4 hashcode");
 		t.compare(true,"==",r3.ifInteger("1"),"1 is an integer");
 		t.compare(false,"==",r3.ifInteger("test"),"1 is an integer");

 		f[0] = new Field("1",FieldDataType.STRING);
		f[1] = new Field("a",FieldDataType.STRING);
		f[2] = new Field("b",FieldDataType.STRING);
 		Record r5 = new Record(f,1);
 		f[0] = new Field("2",FieldDataType.STRING);
		f[1] = new Field("c",FieldDataType.STRING);
		f[2] = new Field("d",FieldDataType.STRING);
		Record r6 = new Record(f,1);
 		t.compare(-1,"==",r5.compareTo(r6),"r6 primary key is larger than r5 primary key");
 		t.compare(0,"==",r5.compareTo(r5),"r5 primary key is the same as its own primary key");
 		t.compare(1,"==",r2.compareTo(r1),"r2 string key is greater than r1 string key");
		t.exitSuite();
		return t;
	}
}