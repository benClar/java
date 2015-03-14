import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.* ;

public class Relation  {

	protected String tableName;
	protected Set<Record> rows;
	protected ArrayList<Column> columnNames;

	public Relation(Column[] newColumns, String tName)	{
		columnNames = new ArrayList<Column>();
		addNewColumnNames(newColumns);
		rows = new TreeSet<Record>();
		tableName = tName;
	}

	public Relation(String[] cNames, FieldDataType[] types, String tName)	{
		columnNames = new ArrayList<Column>();
		addNewColumnNames(cNames, types);
		rows = new TreeSet<Record>();
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
		return columnNames.get(targetColumn);
	}




	public void addColumn(String[] cNames, FieldDataType[] types)	{
		addNewColumnNames(cNames,types);
		extendRows(cNames.length,types);
	}

	public int getNumberOfFields()	{
		return getWidth()*getCardinality();
	}

	protected void addNewColumnNames(String[] cNames, FieldDataType[] types, FieldDataType[] keys)	{

		for(int i = 0; i < cNames.length; i++)	{
			addNewColumnNames(cNames[i],types[i],keys[i]);
		}
	}

	protected void addNewColumnNames(String[] cNames, FieldDataType[] types)	{

		for(int i = 0; i < cNames.length; i++)	{
			addNewColumnNames(cNames[i],types[i],FieldDataType.NONKEY);
		}
	}

	protected void addNewColumnNames(Column[] newColumns)	{
		for(int i = 0; i < newColumns.length; i++)	{
			columnNames.add(newColumns[i]);
		}
	}

	protected void addNewColumnNames(String cName,FieldDataType t)	{
		columnNames.add(new Column(cName,t,FieldDataType.NONKEY));
	}

	protected void addNewColumnNames(String cName,FieldDataType t,FieldDataType k)	{
			columnNames.add(new Column(cName,t,k));
	}


	protected int extendRows(int extension, FieldDataType[] t)	{
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


	/*
	 *Assumes that the fields passed correspond to the entire record: Not for inserting individual fields.
	 */
	public int addRecord(Field[] newFields)	{
		try	{
			// if(tableContainsKey(newFields[getKey()]) == true)	{
			// 	throw new IllegalArgumentException("Key Field not unique");
			// }
			if(newFields.length != getWidth())	{
				throw new IllegalArgumentException("Number of values supplied doesn't match columns in table");
			} else	{
				for(int i = 0; i < newFields.length; i++)	{
					updateFieldLength(newFields[i],i);
				}
				//addKey(newFields[getKey()].getValue(),new Record(newFields,getKey())); //! Add reference to record key index
				rows.add(new Record(newFields)); //! Add reference to set via lookup in key index
				
				return 1;
			}
		} catch(IllegalArgumentException e)	{
			return WhiteBoxTesting.catchException(e,"Failed to add record" );
		} catch(Exception e)	{
			return WhiteBoxTesting.catchFatalException(e,"Failed to add record to table ");
		}
	}

	protected int updateFieldLength(Field newField, int col)	{
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



	protected int deleteRow(Record r)	{	
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
			Relation.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Relation Unit Tests");
		/*Unit Tests Here*/
		t.exitSuite();
		return t;
	}
}

