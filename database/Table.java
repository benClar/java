import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.* ;

/*to DO:
	-Validate Column Names*/

public class Table  {

	String tableName;
	private ArrayList<Record> rows;
	private ArrayList<Column> columnNames;
	private HashMap<String, Integer> keyMap;
	private int keyField;  //! Tracks which column is the primary key

	public Table(Column[] newColumns, String tName)	{
		columnNames = new ArrayList<Column>();
		addNewColumnNames(newColumns);
		rows = new ArrayList<Record>();
		keyField = validateKeyField();
		keyMap = new HashMap<String, Integer>();
		tableName = tName;
	}

	public Table(String[] cNames, FieldDataType[] types, FieldDataType[] keys,String tName)	{
		columnNames = new ArrayList<Column>();
		addNewColumnNames(cNames, types, keys);
		rows = new ArrayList<Record>();	
		keyField = validateKeyField();
		keyMap = new HashMap<String, Integer>();
		tableName = tName;
	}

	public String getTableName()	{
		try	{
			if(tableName != null)	{
				return tableName;
			}
			throw new Exception();
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Table has invalid name");
		}
		return null;

	}

	public int setTableName(String newName)	{
		try{
			if(newName != null && newName.length() > 0)	{
				tableName = newName;
				return 1;
			} else {
				throw new Exception();
			}
		}	catch(Exception e)	{
			return WhiteBoxTesting.catchException(e,"Invalid Name for table");
		}
	}

	public Column getColumn(int targetColumn)	{
		return columnNames.get(targetColumn);
	}
	public int getKey()	{
		return keyField;
	}

	private void setKey(int newKey)	{
		keyField = newKey;
	}
	public int validateKeyField()	{
		setKey(0);
		boolean keyDetected = false;
		try{
			for(int i = 0; i < getWidth(); i++)	{
				if(getColumn(i).getKeyType() == FieldDataType.PKEY)	{
					if(keyDetected == false)	{
						setKey(i);
						keyDetected = true;
					} else	{
						throw new Exception("Table may only have one primary key");
					}
				}
			}

			if(keyDetected == false)	{
				throw new Exception("Key Field not found");
			}

		} catch(Exception e)	{
			return WhiteBoxTesting.catchException(e);
		}
		return getKey();
	}


	public void addColumn(String[] cNames, FieldDataType[] types)	{
		addNewColumnNames(cNames,types);
		extendRows(cNames.length,types);
	}

	public int getNumberOfFields()	{
		return getWidth()*getCardinality();
	}

	private void addNewColumnNames(String[] cNames, FieldDataType[] types, FieldDataType[] keys)	{

		for(int i = 0; i < cNames.length; i++)	{
			addNewColumnNames(cNames[i],types[i],keys[i]);
		}
	}

	private void addNewColumnNames(String[] cNames, FieldDataType[] types)	{

		for(int i = 0; i < cNames.length; i++)	{
			addNewColumnNames(cNames[i],types[i],FieldDataType.NONKEY);
		}
	}

	private void addNewColumnNames(Column[] newColumns)	{
		for(int i = 0; i < newColumns.length; i++)	{
			columnNames.add(newColumns[i]);
		}
	}

	private void addNewColumnNames(String cName,FieldDataType t)	{
		columnNames.add(new Column(cName,t,FieldDataType.NONKEY));
	}

	private void addNewColumnNames(String cName,FieldDataType t,FieldDataType k)	{
			columnNames.add(new Column(cName,t,k));
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
			emptyFields[i] = new Field("", t[i]);
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
		try	{
			return rows.get(r);
		} catch (IndexOutOfBoundsException e)	{
			WhiteBoxTesting.catchException(e,"Row does not exist");
			return null;
		}
	}

	public Record getRecordByKey(String keyToGet)	{
		try	{
			return getRecord(keyMap.get(keyToGet));
		} catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Key value " + keyToGet + " doesn't exist");
			return null;
		}
	}

	private boolean tableContainsKey(Field f)	{
		return keyMap.containsKey(f.getValue());
	}

	private boolean tableContainsKey(String f)	{
		return keyMap.containsKey(f);
	}

	/*
	 *Assumes that the fields passed correspond to the entire record: Not for inserting individual fields.
	 */
	public int addRecord(Field[] newFields)	{
		try	{
			if(tableContainsKey(newFields[getKey()]) == true)	{
				throw new IllegalArgumentException("Key Field not unique");
			}
			if(newFields.length != getWidth())	{
				throw new IllegalArgumentException("Number of values supplied doesn't match columns in table");
			} else	{
				for(int i = 0; i < newFields.length; i++)	{
					updateFieldLength(newFields[i],i);
				}
				rows.add(new Record(newFields));
				addKey(newFields[getKey()].getValue(),getCardinality()-1);
				return 1;
			}
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchException(e,"Failed to add record" );
		} catch(Exception e)	{
			return WhiteBoxTesting.catchFatalException(e,"Failed to add record to table ");
		}
	}

	private void addKey(String key, int value)	{
		try	{
			if(tableContainsKey(key) == false)	{
				keyMap.put(key,value);
			} else	{
				throw new Exception("Attempted to add Duplicate Key to table");
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchException(e,"Error Adding Key ");
		}
	}

	private int updateFieldLength(Field newField, int col)	{
		if(newField.getValue().length() > columnNames.get(col).getLongestFieldSize())	{
			columnNames.get(col).setLongestFieldSize(newField.getValue().length());
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

	public Integer getColumnIndex(String colName)	{
		try{
			for(int i = 0; i < getWidth(); i++){
				if(colName.equals(getColumnName(i)))	{
					return i;
				}
			}
			throw new IllegalArgumentException();
		} catch(IllegalArgumentException e)	{
			WhiteBoxTesting.catchException(e,"Column Name does not exist");
			return null;
		}
	}

	public String getFieldValue(String key, int col)	{
		try	{
			return getRecord(keyMap.get(key)).getField(col).getValue();
		} catch(IndexOutOfBoundsException e)	{
			WhiteBoxTesting.catchException(e,"Field does not exist");
			return "ERROR";
		}	catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Key Does not exist");
			return "ERROR";
		}
	}

	public String getFieldValueByColumnName(String key, String col)	{
		return getFieldValue(key,getColumnIndex(col));
	}

	public String getFieldValue(int row, int col)	{
		try	{
			return getRecord(row).getField(col).getValue();
		} catch(IndexOutOfBoundsException e)	{
			WhiteBoxTesting.catchException(e,"Field does not exist");
			return "ERROR";
		}
	}


	public int changeFieldValue(String key, String colName, String newValue)	{
		int mappedValue;
		try	{
	 		if(getColumnIndex(colName) == keyField)	{
	 			 addKey(newValue,removeKeyMapping(key));
	 		}
	 		getRecordByKey(newValue).changeField(newValue,getColumnIndex(colName));
	 	} catch (NullPointerException e)	{
	 		return WhiteBoxTesting.catchException(e,"Field Does not exist");
	 	}
	 	return 1;
	}

	private int removeKeyMapping(String keyToRemove)	{
		int mapValue = keyMap.get(keyToRemove);
		keyMap.remove(keyToRemove);
		return mapValue;
	}

	public int changeColumnName(String oldName, String newName)	{
		try	{
		if(newName == null)	{
		 	throw new IllegalArgumentException();
		} else	{	
			columnNames.get(getColumnIndex(oldName)).setColumnName(newName);
		}
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchFatalException(e,"Column name cannot be null");
		}
		 return 1;
	}

	public int deleteRowByKeyValue(String key)	{
		try	{
			deleteRow(keyMap.get(key));
			keyMap.remove(key);
		} catch (NullPointerException e)	{
			return WhiteBoxTesting.catchException(e,"Key to delete does not exist");
		}
		return 1;
	}

	private int deleteRow(int r)	{	
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
		Table.unitTest_hashMap(t);
		return t;
	}

	public static Testing unitTest_hashMap(Testing t)	{
		WhiteBoxTesting.startTesting();

		Column[] cols = new Column[3];
		Field[] f = new Field[3];
		cols[0] = new Column("Col1",FieldDataType.STRING,FieldDataType.PKEY);
		cols[1] = new Column("Col2",FieldDataType.STRING,FieldDataType.NONKEY);
		cols[2] = new Column("Col3",FieldDataType.STRING,FieldDataType.NONKEY);
		Table tab = new Table(cols,"TestTable");

		t.enterSuite("Table Unit Tests: HashMapping keys to rows");
		f[0] = new Field("1",FieldDataType.STRING);
		f[1] = new Field("blue",FieldDataType.STRING);
		f[2] = new Field("car",FieldDataType.STRING);
		tab.addRecord(f);
		f[0] = new Field("2",FieldDataType.STRING);
		f[1] = new Field("red",FieldDataType.STRING);
		f[2] = new Field("car",FieldDataType.STRING);
		tab.addRecord(f);
		t.compare("red","==",tab.getRecordByKey("2").getField(1).getValue(),"Key 2 field 2 is red");
		t.compare("blue","==",tab.getRecordByKey("1").getField(1).getValue(),"Key 1 field 2 is blue");
		t.compare(null,"==",tab.getRecordByKey("3"),"Value Does not exist");
		t.compare(2,"==",tab.getCardinality(),"Table has 2 records");
		tab.deleteRowByKeyValue("2");
		t.compare(1,"==",tab.getCardinality(),"Table has 1 record: one deleted by keyfield reference");
		t.exitSuite();
		return t;		
	}

	public static Testing unitTest_AddingRows(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table Unit Tests: Adding rows to table");
		
		Column[] cols = new Column[3];
		Field[] f = new Field[3];
		cols[0] = new Column("Col1",FieldDataType.STRING,FieldDataType.PKEY);
		cols[1] = new Column("Col2",FieldDataType.STRING,FieldDataType.NONKEY);
		cols[2] = new Column("Col3",FieldDataType.STRING,FieldDataType.NONKEY);
		Table tab = new Table(cols,"testTable");
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

		t.compare(true,"==",tab.tableContainsKey(new Field("val1",FieldDataType.STRING)),"Val1 is not unique in the keyfield column of this table");
		t.compare(false,"==",tab.tableContainsKey(new Field("val2",FieldDataType.STRING)),"Val2 is unique in the keyfield column of this table");

		t.exitSuite();
		return t;
	}

	public static Testing unitTest_DeletingRows(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table Unit Tests: Deleting rows from table");

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
		FieldDataType[] ktype = new FieldDataType[3];

		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
			ktype[i] = FieldDataType.NONKEY;
		}
		ktype[0] = FieldDataType.PKEY;

		Table tab=new Table(cNames,dtype,ktype,"testTable");
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
		t.compare(2,"==",tab.getColumnIndex("col3"),"Col3 is name of third column");
		t.compare(null,"==",tab.getColumnIndex("test"),"test is not column name in table");
		t.compare(0,"==",tab.changeColumnName("col3",null),"Column name cannot be null");
		t.compare(1,"==",tab.changeColumnName("col3","COL3"),"Col 3 name changed");
		t.compare("COL3","==",tab.getColumnName(2),"The third column is now COL3");
		t.compare(0,"==",tab.deleteRow(10),"Invalid attempt to delete row that does not exist");
		tab.changeFieldValue("field0","col1","field1");
		t.compare("field1","==",tab.getRecordByKey("field1").getField(0).getValue(),"Field0 key has been updated to field1");
		tab.changeFieldValue("field1","col1","field2");
		t.compare("field2","==",tab.getRecordByKey("field2").getField(0).getValue(),"Field1 key has been updated to field2");
		tab.changeFieldValue("field2","col2","field2");
		t.compare("field2","==",tab.getRecordByKey("field2").getField(1).getValue(),"Field1 non-key has been updated to field2");
		t.compare("testTable","==",tab.getTableName(),"Table name is testtable");
		t.compare(0,"==",tab.setTableName(""),"Invalid Table name: Empty");
		t.compare(0,"==",tab.setTableName(null),"Invalid Table name: null");
		t.compare(1,"==",tab.setTableName("renamedTestTable"),"Valid Table name: renamedTestTable");
		t.compare("renamedTestTable","==",tab.getTableName(),"Table name now renamedTestTable");
		t.exitSuite();
		return t;
	}

}

