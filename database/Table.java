import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.* ;

/*to DO:
	-Validate Column Names*/

public class Table  {

	private ArrayList<Record> rows;
	private ArrayList<String> columnNames;

	public Table(String[] cNames)	{
		columnNames = new ArrayList<String>();
		addNewColumnNames(cNames);
		rows = new ArrayList<Record>();
	}

	public void addColumn(String[] cNames)	{
		addNewColumnNames(cNames);
		extendRows(cNames.length);
	}

	public int getNumberOfFields()	{
		return getWidth()*getCardinality();
	}

	private void addNewColumnNames(String[] cNames)	{

		for(int i = 0; i < cNames.length; i++)	{
			addNewColumnNames(cNames[i]);
		}
	}

	private void addNewColumnNames(String cName)	{

			columnNames.add(cName);
	}

	private int extendRows(int extension)	{
		try	{
			if(getCardinality() == 0)	{
				throw new IllegalArgumentException("Table is empty.");
			} else if(getWidth() !=  extension + this.getRow(0).getNumberOfFields())	{
				throw new IllegalArgumentException("Mistmatch between column headings and rows");
			}
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchException(e);
		}

		Field[] emptyFields = new Field[extension];

		for(int i = 0; i < emptyFields.length; i++)	{
			emptyFields[i] = new Field(null, FieldDataType.STRING);
		}

		for(int i = 0; i < rows.size(); i++ )	{
			getRow(i).addNewFields(emptyFields);
		}

		return 1;
	}

	public int getCardinality()	{
		return rows.size();
	}

	public int getWidth()	{
		return columnNames.size();
	}

	public Record getRow(int r)	{
		return rows.get(r);
	}

	public int addRecord(Field[] newFields)	{
		try	{
			if(newFields.length != getWidth())	{
				throw new IllegalArgumentException();
			} else	{
				rows.add(new Record(newFields));
				return 1;
			}
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchException(e,"Number of values supplied doesn't match columns in table");
		}
	}

	public String getColumnName(int targetColumn)	{
		try	{
			return columnNames.get(targetColumn);
		} catch (IndexOutOfBoundsException e)	{
			WhiteBoxTesting.catchException(e,"Column does not exist");
			return "ERROR";
		}
	}

	public int getColumnName(String colName)	{
		try{
			for(int i = 0; i < getWidth(); i++){
				if(colName == getColumnName(i))	{
					return i;
				}
			}
			throw new IllegalArgumentException();
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchException(e,"Column Name does not exist");
		}
	}

	public String getFieldValue(int row, int col)	{
		try	{
			return getRow(row).getField(col).getValue();
		} catch(IndexOutOfBoundsException e)	{
			WhiteBoxTesting.catchException(e,"Field does not exist");
			return "ERROR";
		}
	}

	public int changeColumnName(String oldName, String newName)	{
		try	{
		if(newName == null)	{
		 	throw new IllegalArgumentException();
		} else	{	
			columnNames.set(getColumnName(oldName),newName);
		}
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchFatalException(e,"Column name cannot be null");
		}
		 return 1;
	}

	public int deleteRow(int r)	{	
		try	{	
		rows.remove(r);
		} catch(IndexOutOfBoundsException e)	{
			return WhiteBoxTesting.catchException(e,"Row does not exist");
		}
		return 1;
	}



/*----------Testing----------*/

	public static void main( String[] args )    {
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			Table.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		Table.unitTest_DeletingRows(t);
		Table.unitTest_AlteringTable(t);
		return t;
	}

	public static Testing unitTest_DeletingRows(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table Unit Tests: Deleting rows from table");

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

	public static Testing unitTest_AlteringTable(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table Unit Tests: alerting table structure");
		String[] cNames = new String[]{"col1","col2","col3"};
		Table tab=new Table(cNames);
		t.compare("col1","==",tab.getColumnName(0),"Column 1 named col1");
		t.compare("ERROR","==",tab.getColumnName(4),"Column 4 does not exist");
		tab.addNewColumnNames("col4");
		t.compare("ERROR","==",tab.getColumnName(4),"Column 4 has been created");
		t.compare(4,"==",tab.getWidth(),"Table has four columns");
		t.compare(0,"==",tab.extendRows(2),"Invalid attempt to extend rows of empty table");

		Field[] newRecord = new Field[4];
		for(int i = 0; i < newRecord.length; i++)	{
			newRecord[i]=new Field("field" + i,FieldDataType.STRING);
		}
		tab.addRecord(newRecord);

		for(int i = 0, c = tab.getNumberOfFields(); i < newRecord.length; i++, c++)	{
			newRecord[i]=new Field("field" + tab,FieldDataType.STRING);
		}

		t.compare(0,"==",tab.extendRows(2),"invalid attempt to extend rows due to column row length mismatch");
		tab.addNewColumnNames(new String[]{"col5","col6"});
		t.compare(1,"==",tab.extendRows(2),"Table columns successfully extended");
		t.compare(6,"==",tab.getRow(0).getNumberOfFields(),"row 0 now has 6 fields");
		t.compare(2,"==",tab.getColumnName("col3"),"Col3 is name of third column");
		t.compare(0,"==",tab.getColumnName("test"),"test is not column name in table");
		t.compare(0,"==",tab.changeColumnName("col3",null),"Column name cannot be null");
		t.compare(1,"==",tab.changeColumnName("col3","COL3"),"Col 3 name changed");
		t.compare("COL3","==",tab.getColumnName(2),"The third column is now COL3");
		t.compare(0,"==",tab.deleteRow(10),"Invalid attempt to delete row that does not exist");
		t.exitSuite();
		return t;
	}

}

