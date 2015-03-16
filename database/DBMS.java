/**
 *To do:
 *-Replace with catchFatalException where required.
 */

import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.io.*;
import java.util.*;

public class DBMS  {

	public static void main( String[] args )    {
	
	Testing t = new Testing();

		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			DBMS.unitTest(new Testing()).endTesting();
		} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.COMPONENT_TEST)) {
			DBMS.componentTest(new Testing()).endTesting();
		} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.NOP)) {
			DBMS main = new DBMS();
			main.run(args);
		}
	}

	public void run(String[] args)  {
		Database db = new Database(args[0]);
		RelationStack rs = new RelationStack();
		Interpreter i = new Interpreter(db,rs);
		String[] cNames = new String[3];
		StringToParse stp = new StringToParse();
		System.out.println(args[0]); 
		try{
			BufferedReader dataIn = new BufferedReader(new InputStreamReader(System.in));
			String input;
	 		System.out.printf("> ");
			while((input=dataIn.readLine())!=null){
				System.out.printf("> ");
				if(i.parse(input) == null)	{
					System.exit(0);
				}
			}
		}catch(IOException io){
			io.printStackTrace();
		}	
	}

/*----------Testing----------*/



	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Database unit tests");
		/*Unit Tests Here*/
		t.exitSuite();
		return t;
	}

	public static Testing componentTest(Testing t) {
		Field.unitTest(t);
		Record.unitTest(t);
		Table.unitTest(t);
		TableReader.unitTest(t);
		TableWriter.unitTest(t);
		DataOutput.unitTest(t);
		Database.unitTest(t);
		StringToParse.unitTest(t);
		DBMS.componentTests_tableRecordField(t);
		DBMS.componentTest_ReadAndWriteTables(t);
		DBMS.componentTests_keyTesting(t);
		Interpreter.unitTest(t);
		RelationStack.unitTest(t);

		return t;
	}

	public static Testing componentTests_tableRecordField(Testing t)	{
		WhiteBoxTesting.startTesting();
		componentTests_AddingRows(t);
		componentTests_DeletingRows(t);
		return t;
	}

	public static Testing componentTests_AddingRows(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table-Record-Field Component Tests: Adding Rows");
		String[] cNames = new String[]{"col1","col2","col3"};
		FieldDataType[] dtype = new FieldDataType[3];
		FieldDataType[] ktype = new FieldDataType[3];
		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
			ktype[i] = FieldDataType.NONKEY;
		}
		ktype[0] = FieldDataType.PKEY;
		Table tab=new Table(cNames,dtype,ktype,"testTable");
		Field[] newRecord = new Field[3];
		Field[] recordTooLong = new Field[4];
		int fieldCount = 0;

		for(int i = 0; i < newRecord.length; i++)	{
			fieldCount++;
			newRecord[i]=new Field("field" + fieldCount,FieldDataType.STRING);

		}
		t.compare(1,"==",tab.addRecord(newRecord),"Populated table with one valid record");
		t.compare(1,"==",tab.getCardinality(),"Table cardinality is 1");
		
		for(int i = 0; i < newRecord.length; i++)	{
			fieldCount++;
			newRecord[i]=new Field("field" + fieldCount,FieldDataType.STRING);
		}

		t.compare(1,"==",tab.addRecord(newRecord),"Populated table with two valid records");
		t.compare(2,"==",tab.getCardinality(),"Table cardinality is 2");
		
		for(int i = 0; i < recordTooLong.length; i++)	{
			recordTooLong[i]=new Field("field" + (i + 6),FieldDataType.STRING);
		}
		t.compare(0,"==",tab.addRecord(recordTooLong),"Populated table invalid records");
		DataOutput dio = new DataOutput();
		dio.printTable(tab);
		t.compare(3,"==",tab.getRecordByKey("field1").getNumberOfFields(),"row 0 has 3 fields");
		fieldCount = 0;
		Set<Record> copyOfSet = tab.getRecordSet();

		for(Record rToTest : copyOfSet)	{
			for(int c = 0; c < tab.getWidth(); c++)	{
				fieldCount++;
				t.compare("field"+fieldCount,"==",tab.getFieldValue(rToTest.getPrimaryKeyValue(),c),"Field value is " + "field"+fieldCount);
			}
		}
		t.exitSuite();
		return t;
	}

	public static Testing componentTests_keyTesting(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableReader-TableWriter-DataOutput-Table-Record-Field Component Tests: Primary Key Functionality");

		//Create Column array
		String[] cNames = new String[3]; 
		FieldDataType[] fType = new FieldDataType[3];
		FieldDataType[] kType = new FieldDataType[3];
		Field[] fToAdd = new Field[3];
		for(int i = 0; i < cNames.length; i++)	{
			cNames[i] = "col" + i;
			fType[i] = FieldDataType.STRING;
			kType[i] = FieldDataType.NONKEY;
		}
		kType[1] = FieldDataType.PKEY;

		fToAdd[0] = new Field("Red",FieldDataType.STRING);
		fToAdd[1] = new Field("1",FieldDataType.STRING);
		fToAdd[2] = new Field("Bus",FieldDataType.STRING);

		Table tab = new Table(cNames,fType,kType,"testTable");
		t.compare(1,"==",tab.getKey(),"Primary key is second row");
		t.compare(1,"==",tab.addRecord(fToAdd),"Added valid record to table");
		t.compare(0,"==",tab.addRecord(fToAdd),"Added invalid record to table: Duplicate key");

		fToAdd[0] = new Field("Blue",FieldDataType.STRING);
		fToAdd[1] = new Field("2",FieldDataType.STRING);
		fToAdd[2] = new Field("Car[honda]",FieldDataType.STRING);

		t.compare(1,"==",tab.addRecord(fToAdd),"Added valid record to table");

		fToAdd[0] = new Field("green",FieldDataType.STRING);
		fToAdd[1] = new Field("3",FieldDataType.STRING);
		fToAdd[2] = new Field("MotorBike,",FieldDataType.STRING);

		t.compare(1,"==",tab.addRecord(fToAdd),"Added valid record to table");

		t.compare(3,"==",tab.getCardinality(),"Table has three rows");
		t.compare("MotorBike,","==",tab.getFieldValue("3",2),"Record with key three has motorbike in third field");
		TableWriter tw = new TableWriter(tab,"text");
		tw.writeTable();
		TableReader tr = new TableReader("text","testTable_writeKeyTable");
		tab = tr.getTable();

		t.compare(1,"==",tab.getKey(),"Table from File: Primary key is second row");
		t.compare(3,"==",tab.getCardinality(),"Table has three rows");

		t.compare("Red","==",tab.getFieldValue("1",0),"Table from File: key 1 field 1 is red");
		t.compare("Bus","==",tab.getFieldValue("1",2),"Table from File: key 1 field 3 is Bus");

		t.compare("Blue","==",tab.getFieldValue("2",0),"Table from File: key 2 field 1 is Blue ");
		t.compare("Car[honda]","==",tab.getFieldValue("2",2),"Table from File: key 3 field 1 is Car[honda]");

		t.compare("green","==",tab.getFieldValue("3",0),"Table from File: key 3 field 1 is green");
		t.compare("MotorBike,","==",tab.getFieldValue("3",2),"Table from File: key 3 field 3 is Motorbike,");

		t.exitSuite();
		return t;
	}
	public static Testing componentTests_DeletingRows(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table-Record-Field Component Tests: Deleting Rows");

		String[] cNames = new String[]{"col1","col2","col3"};
		FieldDataType[] dtype = new FieldDataType[3];
		FieldDataType[] ktype = new FieldDataType[3];
		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
			ktype[i] = FieldDataType.NONKEY;
		}
		ktype[0] = FieldDataType.PKEY;
		Table tab=new Table(cNames,dtype,ktype,"testTable");
		Field[] newRecord = new Field[3];

		for(int i = 0, c = tab.getNumberOfFields(); i < newRecord.length; i++, c++)	{
			newRecord[i]=new Field("field" + c,FieldDataType.STRING);
		}

		tab.addRecord(newRecord);

		for(int i = 0, c = tab.getNumberOfFields(); i < newRecord.length; i++, c++)	{
			newRecord[i]=new Field("field" + c,FieldDataType.STRING);
		}

		tab.addRecord(newRecord);

		int currCard = tab.getCardinality();
		t.compare(1,"==",tab.deleteRowByKeyValue("field0"),"Removed First Row");
		t.compare(tab.getCardinality(),"==",currCard - 1,"Table cardinality has decreased by one");
		t.compare("field3","==",tab.getFieldValue("field3",0),"First field in first record is field3");
		t.compare("field4","==",tab.getFieldValue("field3",1),"Second field in first record is field4");
		t.compare("field5","==",tab.getFieldValue("field3",2),"Third field in first record is field5");
		t.exitSuite();
		return t;
	}

	public static Testing componentTest_ReadAndWriteTables(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableReader-TablerWriter Component Tests: Comparing Table I/O");
		TableReader tr = new TableReader("text","testTable_toRead");
		Table tab = tr.getTable();
		TableWriter tw = new TableWriter(tab,"text");
		tw.writeTable();

		StringBuffer readTable = new StringBuffer();
		StringBuffer writeTable = new StringBuffer();
		Scanner readScanner;
		Scanner writeScanner;
		try	{
			readScanner = new Scanner(new File("resources/text/testTable_toRead.txt"));
			writeScanner = new Scanner(new File("resources/text/testTable_toWrite.txt"));
			while(readScanner.hasNext())	{
				readTable.append(readScanner.nextLine());
			}

			while(writeScanner.hasNext())	{
				writeTable.append(writeScanner.nextLine());
			}
		t.compare(new String(writeTable),"==",new String(readTable),"Table has been read from file, stored in table, and written to file successfully");
		} catch(FileNotFoundException e)	{
			WhiteBoxTesting.catchFatalException(e,"File not found");
		}

		t.exitSuite();
		return t;

	}
}