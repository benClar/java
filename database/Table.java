import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.* ;

/*to DO:
	-Validate Column Names*/

public class Table  {

	String tableName;
	private Set<Record> rows;
	private ArrayList<Column> columnNames;
	private HashMap<String, Record> keyMap;
	private int keyField;  //! Tracks which column is the primary key
	private boolean hasKey;

	public Table(Column[] newColumns, String tName)	{
		columnNames = new ArrayList<Column>();
		addNewColumnNames(newColumns);
		rows = new TreeSet<Record>();
		// rows = new ArrayList<Record>();
		try{
			keyField = validateKeyField();
		}catch(NullPointerException e)	{

		}
		if(hasKey)	{
			keyMap = new HashMap<String, Record>();
		} else {
			keyMap = null;
		}
		tableName = tName;
	}

	public boolean hasKey()	{
		return hasKey;
	}

	public Table(Column[] newColumns, Record[] newRecords, String tName){


		columnNames = new ArrayList<Column>();
		addNewColumnNames(newColumns);
		rows = new TreeSet<Record>();

		try{
			keyField = validateKeyField();
		}catch(NullPointerException e)	{
			
		}
		if(hasKey)	{
			keyMap = new HashMap<String, Record>();
		} else {
			keyMap = null;
		}
		for(Record r : newRecords)	{
			addRecord(r);
		}
		tableName = tName;
	}

	public Table(String[] cNames, FieldDataType[] types, FieldDataType[] keys,String tName)	{

		columnNames = new ArrayList<Column>();
		addNewColumnNames(cNames, types, keys);

		// rows = new ArrayList<Record>();	
		rows = new TreeSet<Record>();
		keyField = validateKeyField();
		if(hasKey)	{
			keyMap = new HashMap<String, Record>();
		} else	{
			keyMap = null;
		}
		tableName = tName;
	}

	public Set<Record> getRecordSet()	{
		return rows;
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
		try {
			return columnNames.get(targetColumn);
		}catch(IndexOutOfBoundsException e)	{
			WhiteBoxTesting.catchException(e,"Column Doesn't Exist");
			return null;
		}
	}

	public Integer getKey()	{
		if(hasKey)	{
		return keyField;
		} else	{
			return null;
		}
	}

	private void setKey(int newKey)	{
		keyField = newKey;
	}

	public int validateKeyField()	{
		setKey(0);
		hasKey = false;
		for(int i = 0; i < getWidth(); i++)	{
			if(getColumn(i).getKeyType() == FieldDataType.PKEY)	{
				if(hasKey == false)	{
					setKey(i);
					hasKey = true;
				}
			}
		}
		return getKey();
	}


	public void addColumn(String[] cNames, FieldDataType[] types)	{
		try {
		for(FieldDataType t: types)	{
			if(t == FieldDataType.PKEY)	{
				throw new IllegalArgumentException();
			}
		}
		addNewColumnNames(cNames,types);
		extendRows(cNames.length,types);
		} catch (IllegalArgumentException e)	{
			WhiteBoxTesting.catchException(e,"Cannot Add Key as empty column");
		}
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
		 	} else {
		 		for( Record rToCheck : rows)	{
			 		if(getWidth() !=  extension + rToCheck.getNumberOfFields())	{
				 		throw new IllegalArgumentException("Mistmatch between column headings and rows");
				 	}
			 	}
		 	}
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchException(e);
		}

		Field[] emptyFields = new Field[extension];

		for(int i = 0; i < emptyFields.length; i++)	{
			emptyFields[i] = new Field("", t[i]);
		}

		for(Record recToExtend : rows)	{
			recToExtend.addNewFields(emptyFields);
		}
		return 1;
	}

	public int getCardinality()	{
		return rows.size();
	}

	public int getWidth()	{
		return columnNames.size();
	}

	public Record getRecordByKey(String keyToGet)	{
		try	{
			return keyMap.get(keyToGet);
		} catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Key value " + keyToGet + " doesn't exist");
			return null;
		}
	}

	public boolean tableContainsKey(Field f)	{
		return keyMap.containsKey(f.getValue());
	}

	public boolean tableContainsKey(String f)	{
		return keyMap.containsKey(f);
	}

	/*
	 *Assumes that the fields passed correspond to the entire record: Not for inserting individual fields.
	 */
	public int addRecord(Field[] newFields)	{

		Record r;
		if(hasKey)	{
			r =new Record(newFields,getKey());
		} else	{
			r = new Record(newFields);
		}
		return 
		addRecord(r);
		// try	{
		// 	if(hasKey && tableContainsKey(newFields[getKey()]) == true)	{
		// 		throw new IllegalArgumentException("Key Field not unique");
		// 	}

		// 	if(newFields.length != getWidth())	{
		// 		throw new IllegalArgumentException("Number of values supplied doesn't match columns in table");
		// 	} else	{
		// 		for(int i = 0; i < newFields.length; i++)	{
		// 			updateFieldLength(newFields[i],i);
		// 		}
		// 	if(hasKey)	{
		// 		addKey(newFields[getKey()].getValue(),new Record(newFields,getKey())); //! Add reference to record key index
		// 	}
		// 	rows.add(getRecordByKey(newFields[getKey()].getValue())); //! Add reference to set via lookup in key index
				
		// 		return 1;
		// 	}
		// } catch(IllegalArgumentException e)	{
		// 	return WhiteBoxTesting.catchException(e,"Failed to add record" );
		// } catch(Exception e)	{
		// 	return WhiteBoxTesting.catchFatalException(e,"Failed to add record to table ");
		// }
	}

	/*
	 *Adding completed Record to database.
	 */
	public Integer addRecord(Record r)	{
		if(validateRecord(r) != null)	{
			if(hasKey)	{
				addKey(r.getField(getKey()).getValue(),r);
			}
			for(int i = 0; i < r.getNumberOfFields(); i++)	{
				updateFieldLength(r.getField(i).getValue()
					,i);
			}
			rows.add(r);
			return 1;
		}

		return 0;

	}

	private Record validateRecord(Record r)	{

		try {
			if(hasKey && tableContainsKey(r.getField(getKey())) == true)	{
				throw new IllegalArgumentException("Key Field not unique");
			}
			if(r.getNumberOfFields() != getWidth())	{
				throw new Exception("Number of values supplied doesn't match columns in table");
			}
			for(int i = 0; i < r.getNumberOfFields(); i++)	{
				if(r.getField(i).getFieldType() != columnNames.get(i).getColumnType())	{
					throw new Exception("Type Mismatch.  Field is of type :" + r.getField(i).getFieldType().toString() + "Column is of type :" + columnNames.get(i).getColumnType().toString());
				}
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchException(e,"Field Validation Error");
			return null;
		}
		return r;
	}

	public Table getMatchingRecords(String colName, String searchTerm)	{
		ArrayList<Record> matchingRecords = new ArrayList<Record>();
		Column[] matchingColumns = new Column[columnNames.size()];
		for(Record r : rows)	{
			if (r.getField(getColumnIndex(colName)).getValue().equals(searchTerm))	{
				matchingRecords.add(r.copyOf());
			}	
		}

		int i = 0;
		for( Column c: columnNames)	{
			matchingColumns[i] = c.copyOf();
			i++;
		}

		return (new Table(matchingColumns,
			matchingRecords.toArray(new Record[matchingRecords.size()]),
			this.toString()));
	}


	private void addKey(String key, Record recordToAdd)	{
		try	{
			if(tableContainsKey(key) == false)	{
				keyMap.put(key,recordToAdd);
			} else	{
				throw new Exception("Attempted to add Duplicate Key to table");
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchException(e,"Error Adding Key ");
		}
	}

	public String toString()	{
		String tableString = new String("");
		for(int i = 0; i < columnNames.size(); i++)	{
			tableString += getColumnName(i);
			if(i != columnNames.size() -1)	{
				tableString += ",";
			}
		}
		return tableString;
	}

	public String contentsToString()	{
		String tableContentString = new String();
		for(Record r : rows)	{
			for(int f = 0; f < r.getNumberOfFields(); f++)	{
				if( f != r.getNumberOfFields() - 1)	{
					tableContentString += r.getField(f).getValue() + ",";
				} else	{
					tableContentString += r.getField(f).getValue();
				}	
			}
		}

		return tableContentString;
	}

	private int updateFieldLength(String newField, int col)	{
		// System.out.println("new length" + newField.getValue().length() + "curr length" + columnNames.get(col).getLongestFieldSize());
		if(newField.length() > columnNames.get(col).getLongestFieldSize())	{
			columnNames.get(col).setLongestFieldSize(newField.
				length());
			// System.out.println("LONGER : UPDATED : " + columnNames.get(col).getLongestFieldSize());
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

	//! Could use refactoring.
	public Table pullColumns(String[] columnsToPull)	{
		Column[] newCols = new Column[columnsToPull.length];
		ArrayList<Record> newRecords = new ArrayList<Record>();
		Field[] newFields = new Field[columnsToPull.length];
		int newKey = 0;
		boolean newHasKey = false;
		String newTableName = new String("");
		// try {
			//!Constructing Column Structures
			for(int c = 0; c < columnsToPull.length; c++)	{
				if(getColumnIndex(columnsToPull[c]) == getKey())	{
					newKey = c;
					newHasKey = true;
				}
				newCols[c] = getColumn(getColumnIndex(columnsToPull[c])).copyOf();
				//constructing new table name from columns
				if(c == columnsToPull.length - 1)	{
					newTableName += columnsToPull[c];
				} else	{
					newTableName += columnsToPull[c] + ",";
				}
			}
			//Getting all records
			for(Record r : rows)	{
				for(int i = 0; i < columnsToPull.length; i++)	{
					newFields[i] = r.getField(getColumnIndex(columnsToPull[i])).copyOf();
				}
				if(!newHasKey)	{
					newRecords.add(new Record(newFields));
				} else	{
					newRecords.add(new Record(newFields,newKey));
				}
			}
			return (new Table(newCols, 
				newRecords.toArray(new Record[newRecords.size()]),
				newTableName));
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
			return getRecordByKey(key).getField(col).getValue();
		} catch(IndexOutOfBoundsException e)	{
			WhiteBoxTesting.catchException(e,"Field does not exist");
			return null;
		}	catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Key Does not exist");
			return null;
		}
	}

	public String getFieldValueByColumnName(String key, String col)	{
		return getFieldValue(key,getColumnIndex(col));
	}

	public int changeFieldValue(Record rToChange, String colName, String newValue)	{
		int mappedValue;
		try	{
	 		if(hasKey && getColumnIndex(colName) == keyField)	{
	 			 addKey(newValue,removeKeyMapping(rToChange.getField(getColumnIndex(colName)).getValue()));
	 			 getRecordByKey(newValue).changeField(newValue,getColumnIndex(colName));
	 		} else	{
	 			rToChange.changeField(newValue,getColumnIndex(colName));
	 		}
	 		
	 	} catch (NullPointerException e)	{
	 		return WhiteBoxTesting.catchException(e,"Field Does not exist");
	 	}
	 	return 1;
	}


	private Record removeKeyMapping(String keyToRemove)	{
		Record mapValue = keyMap.get(keyToRemove);
		keyMap.remove(keyToRemove);
		return mapValue;
	}

	public int changeColumnName(String oldName, String newName)	{
		try	{
		if(newName == null)	{
		 	throw new IllegalArgumentException();
		} else	{
			updateFieldLength(newName,getColumnIndex(oldName));
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

	private int deleteRow(Record r)	{	
		try	{	
		rows.remove(r);
		} catch(IndexOutOfBoundsException e)	{
			return WhiteBoxTesting.catchException(e,"Row does not exist");
		}	catch (NullPointerException e)	{
			return WhiteBoxTesting.catchException(e,"Attempt to delete does not exist");
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
		Table.unitTest_treeSet(t);
		Table.unitTest_searchingTable(t);
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
		t.compare(1,"==",tab.deleteRow(tab.getRecordByKey("field0")),"Removed First Row");
		t.compare(tab.getCardinality(),"==",currCard - 1,"Table cardinality has decreased by one");
		t.compare("field3","==",tab.getFieldValue("field3",0),"First field in first record is field3");
		t.compare("field4","==",tab.getFieldValue("field3",1),"Second field in first record is field4");
		t.compare("field5","==",tab.getFieldValue("field3",2),"Third field in first record is field5");
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
		t.compare(6,"==",tab.getRecordByKey("field0").getNumberOfFields(),"row 0 now has 6 fields");
		t.compare(2,"==",tab.getColumnIndex("col3"),"Col3 is name of third column");
		t.compare(null,"==",tab.getColumnIndex("test"),"test is not column name in table");
		t.compare(0,"==",tab.changeColumnName("col3",null),"Column name cannot be null");
		t.compare(1,"==",tab.changeColumnName("col3","COL3"),"Col 3 name changed");
		t.compare("COL3","==",tab.getColumnName(2),"The third column is now COL3");
		
		t.compare(0,"==",tab.deleteRow(tab.getRecordByKey("field10")),"Invalid attempt to delete row that does not exist");
		tab.changeFieldValue(tab.getRecordByKey("field0"),"col1","field1");
		t.compare("field1","==",tab.getRecordByKey("field1").getField(0).getValue(),"Field0 key has been updated to field1");
		tab.changeFieldValue(tab.getRecordByKey("field1"),"col1","field2");
		t.compare("field2","==",tab.getRecordByKey("field2").getField(0).getValue(),"Field1 key has been updated to field2");
		tab.changeFieldValue(tab.getRecordByKey("field2"),"col2","field2");
		t.compare("field2","==",tab.getRecordByKey("field2").getField(1).getValue(),"Field1 non-key has been updated to field2");
		t.compare("testTable","==",tab.getTableName(),"Table name is testtable");
		t.compare(0,"==",tab.setTableName(""),"Invalid Table name: Empty");
		t.compare(0,"==",tab.setTableName(null),"Invalid Table name: null");
		t.compare(1,"==",tab.setTableName("renamedTestTable"),"Valid Table name: renamedTestTable");
		t.compare("renamedTestTable","==",tab.getTableName(),"Table name now renamedTestTable");
		t.exitSuite();
		return t;
	}

	public static Testing unitTest_treeSet(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table Unit Tests: Tree Set record structure");
		String[] cNames = new String[]{"ID","Vehicle","Manufacturer"};
		Field[] f = new Field[3];
		FieldDataType[] dtype = new FieldDataType[3];
		FieldDataType[] ktype = new FieldDataType[3];

		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
			ktype[i] = FieldDataType.NONKEY;
		}
		ktype[0] = FieldDataType.PKEY;

		Table tab=new Table(cNames,dtype,ktype,"testTable");
		t.compare("ID","==",tab.getColumnName(0),"Column 1 named ID");
		f[0] = new Field("1",FieldDataType.STRING);
		f[1] = new Field("Car",FieldDataType.STRING);
		f[2] = new Field("Honda",FieldDataType.STRING);
		t.compare(1,"==",tab.addRecord(f),"Record successfully added");
		t.compare(0,"==",tab.addRecord(f),"Duplicate Record not added");
		f[0] = new Field("3",FieldDataType.STRING);
		f[1] = new Field("Bus",FieldDataType.STRING);
		f[2] = new Field("Ford",FieldDataType.STRING);
		t.compare(1,"==",tab.addRecord(f),"Record successfully added");
		t.compare(2,"==",tab.getCardinality(),"Table Cardinality is 2");
		f[0] = new Field("2",FieldDataType.STRING);
		f[1] = new Field("Bike",FieldDataType.STRING);
		f[2] = new Field("Suzuki",FieldDataType.STRING);
		t.compare(1,"==",tab.addRecord(f),"Record successfully added");
		Integer key = 1;
		for(Record r : tab.rows)	{
			t.compare(key.toString(),"==",r.getField(0).getValue(),"TreeSet sorting in ascending order on primary key " + key + ":" + r.getField(0).getValue());
			key++;
		}
		t.compare("ID,Vehicle,Manufacturer","==",tab.toString()," table string value is" +  tab.toString());
		t.exitSuite();
		return t;
	}

	public static Testing unitTest_searchingTable(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Table Unit Tests: Searching Tables");
		String[] cNames = new String[]{"ID","Vehicle","Manufacturer"};
		Field[] f = new Field[3];
		FieldDataType[] dtype = new FieldDataType[3];
		FieldDataType[] ktype = new FieldDataType[3];

		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
			ktype[i] = FieldDataType.NONKEY;
		}
		ktype[0] = FieldDataType.PKEY;

		Table tab=new Table(cNames,dtype,ktype,"testTable");
		f[0] = new Field("1",FieldDataType.STRING);
		f[1] = new Field("Car",FieldDataType.STRING);
		f[2] = new Field("Honda",FieldDataType.STRING);
		tab.addRecord(f);
		f[0] = new Field("3",FieldDataType.STRING);
		f[1] = new Field("Bus",FieldDataType.STRING);
		f[2] = new Field("Ford",FieldDataType.STRING);
		tab.addRecord(f);
		f[0] = new Field("2",FieldDataType.STRING);
		f[1] = new Field("Bike",FieldDataType.STRING);
		f[2] = new Field("Suzuki",FieldDataType.STRING);
		tab.addRecord(f);
		f[0] = new Field("4",FieldDataType.STRING);
		f[1] = new Field("Van",FieldDataType.STRING);
		f[2] = new Field("Suzuki",FieldDataType.STRING);
		tab.addRecord(f);
		DataOutput DO = new DataOutput();
		Table searchResults = tab.getMatchingRecords("Manufacturer","Suzuki");
		String[] colsToPull = new String[]{"Vehicle","Manufacturer"};
		Table columnPull = tab.pullColumns(colsToPull);
		// DO.printTable(tab);
		// DO.printTable(searchResults);
		// DO.printTable(columnPull);
		t.compare(2,"==",searchResults.getCardinality(),"2 fields in resultant table");
		t.compare("Van","==",searchResults.getRecordByKey("4").getField(1).getValue(),"Results table contains Van");
		t.compare("Bike","==",searchResults.getRecordByKey("2").getField(1).getValue(),"Results table contains Van");
		searchResults.changeFieldValue(searchResults.getRecordByKey("4"),"Vehicle","Plane");
		t.compare("Plane","==",searchResults.getRecordByKey("4").getField(1).getValue(),"Van field changed to" + searchResults.getRecordByKey("4").getField(1).getValue());
		t.compare("Van","==",tab.getRecordByKey("4").getField(1).getValue(),"Record Van unchanged in previous table" + tab.getRecordByKey("4").getField(1).getValue());
		t.compare(2,"==",columnPull.getWidth(),"Pulled two columns to make this table");
		t.compare(4,"==",columnPull.getCardinality(),"All rows still present");
		t.compare("Van,Suzuki","==",columnPull.getMatchingRecords("Vehicle","Van").contentsToString(),"Contents of table is Suzuki,van after a search and column pull");
		f[0] = new Field("5",FieldDataType.STRING);
		f[1] = new Field("Bus",FieldDataType.STRING);
		f[2] = new Field("Ford",FieldDataType.STRING);
		tab.addRecord(f);

		t.compare(5,"==",tab.getCardinality(),"New Unique Row added to table");
		t.compare(4,"==",tab.pullColumns(colsToPull).getCardinality(),"Only four unique rows in table after column pull");
		colsToPull = new String[]{"Manufacturer","Vehicle"};
		t.compare(4,"==",tab.pullColumns(colsToPull).getCardinality(),"Only four unique rows in table after column pull");
		t.exitSuite();
		return t;
	}

	public static Table spawnTestDB(String name)	{
		String[] cNames = new String[]{"ID","Vehicle","Manufacturer"};
		Field[] f = new Field[3];
		FieldDataType[] dtype = new FieldDataType[3];
		FieldDataType[] ktype = new FieldDataType[3];

		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
			ktype[i] = FieldDataType.NONKEY;
		}
		ktype[0] = FieldDataType.PKEY;
		dtype[0] = FieldDataType.INTEGER;

		Table tab=new Table(cNames,dtype,ktype,name);
		f[0] = new Field("1",FieldDataType.INTEGER);
		f[1] = new Field("Car",FieldDataType.STRING);
		f[2] = new Field("Honda",FieldDataType.STRING);
		tab.addRecord(f);
		f[0] = new Field("3",FieldDataType.INTEGER);
		f[1] = new Field("Bus",FieldDataType.STRING);
		f[2] = new Field("Ford",FieldDataType.STRING);
		tab.addRecord(f);
		f[0] = new Field("2",FieldDataType.INTEGER);
		f[1] = new Field("Bike",FieldDataType.STRING);
		f[2] = new Field("Suzuki",FieldDataType.STRING);
		tab.addRecord(f);
		f[0] = new Field("4",FieldDataType.INTEGER);
		f[1] = new Field("Van",FieldDataType.STRING);
		f[2] = new Field("Suzuki",FieldDataType.STRING);
		tab.addRecord(f);
		f[0] = new Field("5",FieldDataType.INTEGER);
		f[1] = new Field("Plane",FieldDataType.STRING);
		f[2] = new Field("Suzuki",FieldDataType.STRING);
		tab.addRecord(f);	
		return tab;	
	}
}
