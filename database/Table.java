import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.* ;

/*to DO:
	-Validate Column Names*/

public class Table  {

	private ArrayList<Record> rows;
	private ArrayList<Column> columnNames;

	public Table(Column[] newColumns)	{
		columnNames = new ArrayList<Column>();
		addNewColumnNames(newColumns);
		rows = new ArrayList<Record>();
	}

	public Table(String[] cNames, FieldDataType[] types)	{
		columnNames = new ArrayList<Column>();
		addNewColumnNames(cNames, types);
		rows = new ArrayList<Record>();	
	}

	public Column getColumn(int targetColumn)	{
		return columnNames.get(targetColumn);
	}


	public void addColumn(String[] cNames, FieldDataType[] types)	{
		addNewColumnNames(cNames,types);
		extendRows(cNames.length,types);
	}

	public int getNumberOfFields()	{
		return getWidth()*getCardinality();
	}

	private void addNewColumnNames(String[] cNames, FieldDataType[] types)	{

		for(int i = 0; i < cNames.length; i++)	{
			addNewColumnNames(cNames[i],types[i]);
		}
	}

	private void addNewColumnNames(Column[] newColumns)	{
		for(int i = 0; i < newColumns.length; i++)	{
			columnNames.add(newColumns[i]);
		}
	}

	private void addNewColumnNames(String cName,FieldDataType t)	{

			columnNames.add(new Column(cName,t));
	}

	private int extendRows(int extension, FieldDataType[] t)	{
		try	{
			if(getCardinality() == 0)	{
				throw new IllegalArgumentException("Table is empty.");
			} else if(getWidth() !=  extension + this.getRecord(0).getNumberOfFields())	{
				throw new IllegalArgumentException("Mistmatch between column headings and rows");
			}
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchException(e);
		}

		Field[] emptyFields = new Field[extension];

		for(int i = 0; i < emptyFields.length; i++)	{
			emptyFields[i] = new Field(null, t[i]);
		}

		for(int i = 0; i < rows.size(); i++ )	{
			getRecord(i).addNewFields(emptyFields);
		}

		return 1;
	}

	public int getCardinality()	{
		return rows.size();
	}

	public int getWidth()	{
		return columnNames.size();
	}

	public Record getRecord(int r)	{
		return rows.get(r);
	}

	public int addRecord(Field[] newFields)	{
		try	{
			if(newFields.length != getWidth())	{
				throw new IllegalArgumentException();
			} else	{
				for(int i = 0; i < newFields.length; i++)	{
					updateFieldLength(newFields[i],i);
				}
				rows.add(new Record(newFields));
				return 1;
			}
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchException(e,"Number of values supplied doesn't match columns in table");
		} catch(Exception e)	{
			return WhiteBoxTesting.catchFatalException(e,"Failed to add record to table");
		}
	}

	private int updateFieldLength(Field newField, int col)	{
		if(newField.getValue().length() > columnNames.get(col).getLongestFieldSize())	{
			columnNames.get(col).setLongestFieldSize(newField.getValue().length());
			// System.out.println(newField.getValue() + " : "  + newField.getValue().length());
			return columnNames.get(col).getLongestFieldSize();
		}
		return 0;
	}

	public String getColumnName(int targetColumn)	{
		try	{
			return columnNames.get(targetColumn).getColumnName();
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
			return getRecord(row).getField(col).getValue();
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
			columnNames.get(getColumnName(oldName)).setColumnName(newName);
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
		Table.unitTest_AddingRows(t);
		return t;
	}

	public static Testing unitTest_AddingRows(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table Unit Tests: Adding rows to table");
		
		Column[] cols = new Column[3];
		Field[] f = new Field[3];
		cols[0] = new Column("Col1",FieldDataType.STRING);
		cols[1] = new Column("Col2",FieldDataType.STRING);
		cols[2] = new Column("Col3",FieldDataType.STRING);
		Table tab = new Table(cols);
		t.compare(4,"==",tab.getColumn(0).getLongestFieldSize(),"Longest String in column 1 is length 4");
		t.compare(4,"==",tab.getColumn(1).getLongestFieldSize(),"Longest String is column 2 is length 4");
		t.compare(4,"==",tab.getColumn(2).getLongestFieldSize(),"Longest String is column 3 is length 4");

		f[0] = new Field("val1",FieldDataType.STRING);
		f[1] = new Field("value2",FieldDataType.STRING);
		f[2] = new Field("ValueThree",FieldDataType.STRING);
		tab.addRecord(f);

		t.compare(4,"==",tab.getColumn(0).getLongestFieldSize(),"Longest String in column 1 is length 4");
		t.compare(6,"==",tab.getColumn(1).getLongestFieldSize(),"Longest String in column 2 is length 6");
		t.compare(10,"==",tab.getColumn(2).getLongestFieldSize(),"Longest String in column 3 is length 10");


		t.exitSuite();
		return t;
	}

	public static Testing unitTest_DeletingRows(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table Unit Tests: Deleting rows from table");

		String[] cNames = new String[]{"col1","col2","col3"};
		FieldDataType[] dtype = new FieldDataType[3];
		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
		}
		Table tab=new Table(cNames,dtype);
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
		t.enterSuite("Table Unit Tests: altering table structure");
		String[] cNames = new String[]{"col1","col2","col3"};
		FieldDataType[] dtype = new FieldDataType[3];
		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
		}

		Table tab=new Table(cNames,dtype);
		t.compare("col1","==",tab.getColumnName(0),"Column 1 named col1");
		t.compare("ERROR","==",tab.getColumnName(4),"Column 4 does not exist");
		tab.addNewColumnNames("col4",FieldDataType.STRING);
		t.compare("ERROR","==",tab.getColumnName(4),"Column 4 has been created");
		t.compare(4,"==",tab.getWidth(),"Table has four columns");

		FieldDataType[] dtype_2 = new FieldDataType[4];
		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
		}

		t.compare(0,"==",tab.extendRows(2,dtype_2),"Invalid attempt to extend rows of empty table");

		Field[] newRecord = new Field[4];
		for(int i = 0; i < newRecord.length; i++)	{
			newRecord[i]=new Field("field" + i,FieldDataType.STRING);
		}

		tab.addRecord(newRecord);

		for(int i = 0, c = tab.getNumberOfFields(); i < newRecord.length; i++, c++)	{
			newRecord[i]=new Field("field" + tab,FieldDataType.STRING);
		}

		FieldDataType[] dtype_3 = new FieldDataType[2];
		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
		}

		t.compare(0,"==",tab.extendRows(2,dtype_2),"invalid attempt to extend rows due to column row length mismatch");
		tab.addNewColumnNames(new String[]{"col5","col6"},dtype_3);
		t.compare(1,"==",tab.extendRows(2,dtype_3),"Table columns successfully extended");
		t.compare(6,"==",tab.getRecord(0).getNumberOfFields(),"row 0 now has 6 fields");
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

