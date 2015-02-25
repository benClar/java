/**
 *To do:
 *-Replace with catchFatalException where required.
 */

import com.bclarke.testing.*;
import com.bclarke.general.*;

public class Database  {

	public static void main( String[] args )    {
	
	Testing t = new Testing();

	if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
		Database.unitTest(new Testing()).endTesting();
	} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.COMPONENT_TEST)) {
		Database.componentTest(new Testing()).endTesting();
	} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.NOP)) {
		Database main = new Database();
		main.run(args);
	}
}

	public void run(String[] args)  {
		/*main Program*/
    }

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
		Database.componentTests_tableRecordField(t);
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
		Table tab=new Table(cNames);
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
		t.compare(3,"==",tab.getRow(0).getNumberOfFields(),"row 0 has 3 fields");

		fieldCount = 0;
		for(int r = 0; r < tab.getCardinality(); r++)	{
			for(int c = 0; c < tab.getWidth(); c++)	{
				fieldCount++;
				t.compare("field"+fieldCount,"==",tab.getFieldValue(r,c),"Field value is " + "field"+fieldCount);
			}
		}
		t.exitSuite();
		return t;
	}

	public static Testing componentTests_DeletingRows(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table-Record-Field Component Tests: Deleting Rows");

		String[] cNames = new String[]{"col1","col2","col3"};
		Table tab=new Table(cNames);
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
		t.compare(1,"==",tab.deleteRow(0),"Removed First Row");
		t.compare(tab.getCardinality(),"==",currCard - 1,"Table cardinality has decreased by one");
		t.compare("field3","==",tab.getFieldValue(0,0),"First field in first record is field3");
		t.compare("field4","==",tab.getFieldValue(0,1),"Second field in first record is field4");
		t.compare("field5","==",tab.getFieldValue(0,2),"Third field in first record is field5");
		t.exitSuite();
		return t;
	}
}