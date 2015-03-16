import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.*;

public class Interpreter	{

	private StringToParse currentLine;
	private Database currentDatabase;
	private RelationStack currentRelStack;
	private DataOutput interpreterOut;

	private final String LOAD_TABLE = "table";
	private final String KEEP_COLUMNS = "column";
	private final String NEW_TABLE = "new";
	private final String STRING_TYPE = "string";
	private final String INTEGER_TYPE = "integer";
	private final String KEY_TYPE = "key";
	private final String FKEY_TYPE = "refers";
	private final String LIST_TABLES = "schema";
	private final String KEEP_ROWS = "rows";
	private final char FIELD_DELIMITER = '$';
	private final char ESCAPE_CHAR = '\\';
	private final char EQUIVALENCE = '=';
	private final String NEW_RECORD = "insert";
	private final String SAVE_TABLE = "save";
	private final String CLEAR_STACK = "clear";
	private final String RENAME_TABLE = "tableName";
	private final String WRITE_DATABASE = "write";
	private final String DELETE_TABLE = "delete";
	private final String ADD_COLUMN = "append";
	private final String JOIN_TABLES = "join";
	private final String PRINT = "print";
	private final String ADD_COLUMNS="append";
	private final String RENAME_COLUMN = "rename";
	private final String CATALOG = "describe";
	private final String QUIT="bye";
	public Interpreter(Database d, RelationStack rs)	{
		currentDatabase = d;
		currentRelStack = rs;
		interpreterOut = new DataOutput();
		currentLine = new StringToParse();
	}

	public Integer parse(String line)	{
	 	currentLine.addLine(line);
	 	String token = new String(currentLine.getNextTokenBy(' '));
	 	try {
		 	switch(token){
		 		case LOAD_TABLE:
		 			parseLoadTable();
		 			break;
		 		case KEEP_COLUMNS:
		 			parseKeepColumns();
		 			break;
		 		case NEW_TABLE:
		 			parseNewTable(currentLine);
		 			break;
		 		case LIST_TABLES:
		 			parseListTables();
		 			break;
		 		case KEEP_ROWS:
		 			parseMatchingRows(currentLine);
		 			break;
		 		case NEW_RECORD:
		 			parseNewRecord(currentLine);
		 			break;
		 		case SAVE_TABLE:
		 			//!Adds top of stack to DB.
		 			parseSave(currentLine);
		 			break;
		 		case CLEAR_STACK:
		 			//!Removes top of stack
		 			parseClear(currentLine);
		 			break;
		 		case RENAME_TABLE:
		 			//! Renames Top of Stack
		 			parseRename(currentLine);
		 			break;
		 		case WRITE_DATABASE:
		 			parseWrite(currentLine);
		 			break;
		 		case DELETE_TABLE:
		 			parseDelete(currentLine);
		 			break;
		 		case JOIN_TABLES:
		 			parseJoin(currentLine);
		 			break;
		 		case PRINT:
		 			break;
				case ADD_COLUMNS:
		 			parseAddColumns(currentLine);
		 			break;
		 		case RENAME_COLUMN:
		 			parseRenameColumn(currentLine);
		 			break;
		 		case QUIT:
		 			currentDatabase.writeDatabase();
		 			return null;
		 		case CATALOG:
		 			parseCatalogTable(currentLine);
		 			break;
		 		case "":
		 			break;
		 		default:
		 			throw new SyntaxErrorException(token + " :Unrecognised Command");
		 	}
	 	} catch (SyntaxErrorException e)	{
	 		return WhiteBoxTesting.catchException(e,"Syntax Error");
	 	} catch (Exception e)	{
	 		return WhiteBoxTesting.catchException(e,"Syntax Error:");
	 	}
	 	if(!WhiteBoxTesting.getMode() && !currentLine.isEmpty())	{
	 		outputCurrentRelation();
	 	}
	 	return 1;
	}

	private void parseCatalogTable(StringToParse cLine)	{
		try {
		cLine.parseCurrentChar(FIELD_DELIMITER);
		Table t = currentDatabase.getTable(cLine.getDelimitedToken(cLine,FIELD_DELIMITER));
		for(int i = 0; i < t.getWidth(); i++)	{
			interpreterOut.printString(t.getColumn(i).toString());
		}
		} catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Table does not exist");
		}
	}

	private void parseJoin(StringToParse cLine)	{
		try {
			cLine.parseCurrentChar(FIELD_DELIMITER);
			Table table1; 
			Table table2;
			ArrayList<String> columnsToJoin = new ArrayList<String>();
			while(cLine.hasNext())	{
				columnsToJoin.add(cLine.getDelimitedToken(cLine,FIELD_DELIMITER));
			}
			if(columnsToJoin.size() > 1)	{
				throw new Exception("You can only join on one condition for now");
			}
			if(currentRelStack.getSize() >= 2)	{
				table1 = currentRelStack.pop();
				table2
				 = currentRelStack.pop();	
				currentRelStack.push(joinRecords(table1,table2,columnsToJoin.toArray(new String[columnsToJoin.size()])));
			} else	{
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e)	{
			WhiteBoxTesting.catchException(e,"Must be two relations in stack to join");
		} catch (Exception e)	{
			WhiteBoxTesting.catchException(e,"Join Error");
		}
	}

	private Column[] joinColumns(Table t1, Table t2)	{
		ArrayList<Column> joinedColumns = new ArrayList<Column>();

		for(int i = 0; i < t1.getWidth(); i++)	{
			joinedColumns.add(removeKeyState(t1.getColumn(i).copyOf()));
		}
		for(int i = 0; i < t2.getWidth(); i++)	{
			joinedColumns.add(removeKeyState(t2.getColumn(i).copyOf()));
		}

		return joinedColumns.toArray(new Column[joinedColumns.size()]);
	}

	private Column removeKeyState(Column c)	{
		if(c.getKeyType() ==FieldDataType.PKEY)	{
			c.setKeyType(FieldDataType.NONKEY);
		}
		return c;
	}

	private String generateTableName(Column cols[])	{
		StringBuffer sb = new StringBuffer();
		for( int i =0; i < cols.length; i++)	{
			if(i != cols.length -1)	{
				sb.append(cols[i].getColumnName() + ",");

			} else	{
				sb.append(cols[i].getColumnName());
			}
		}

		return sb.toString();
	}

	private Table joinRecords(Table t1, Table t2, String[] column)	{
		Set<Record> t1Records = t1.getRecordSet();
		Set<Record> t2Records = t2.getRecordSet();
		ArrayList<Record> matchedRecords = new ArrayList<Record>();
		Field[] matchedFields = new Field[t1.getWidth() + t2.getWidth()];
		Column[] joinedColumns = joinColumns(t1, t2);
		Table result = new Table(joinedColumns,generateTableName(joinedColumns));;
		int i;
		for(Record r1: t1Records)	{
			for(Record r2: t2Records)	{
				for(int f = 0; f < column.length; f++)	{
					if(r1.getField(t1.getColumnIndex(column[f])).getValue().equals(r2.getField(t2.getColumnIndex(column[f])).getValue()))	{
						i = 0;
						for(int t1Field = 0; t1Field < t1.getWidth() ; t1Field++, i++)	{
							matchedFields[i] = r1.getField(t1Field).copyOf();
						}
						for(int t2Field = 0; t2Field < t2.getWidth() ; t2Field++, i++)	{
							matchedFields[i] = r2.getField(t2Field).copyOf();
						}
						matchedRecords.add(new Record(matchedFields));
					}
				}
			}
		}
		for(Record r : matchedRecords)	{
			result.addRecord(r);
		}
		return result;
	}

	private void parseAddColumns(StringToParse cLine)	{

	 	ArrayList<String> colName = new ArrayList<String>();
	 	ArrayList<FieldDataType> type = new ArrayList<FieldDataType>();
	 	while(cLine.hasNext())	{
	 		cLine.parseCurrentChar(FIELD_DELIMITER);
	 		colName.add(trim(cLine.getDelimitedToken(cLine,FIELD_DELIMITER)));
	 		cLine.parseCurrentChar(FIELD_DELIMITER);
	 		type.add(translateTypeField(trim(cLine.getDelimitedToken(cLine,FIELD_DELIMITER))));
	 	}
	 	currentRelStack.peek().addColumn(colName.toArray(new String[colName.size()]), type.toArray(new FieldDataType[type.size()]));
	}


	private void parseRenameColumn(StringToParse cLine)	{
		String oldname;
		String newName;
		while(cLine.hasNext())	{
			cLine.parseCurrentChar(FIELD_DELIMITER);
			oldname = trim(cLine.getDelimitedToken(cLine,EQUIVALENCE));
			cLine.parseCurrentChar(EQUIVALENCE);
			cLine.parseCurrentChar(FIELD_DELIMITER);
			newName = trim(cLine.getDelimitedToken(cLine,FIELD_DELIMITER));
			currentRelStack.peek().changeColumnName(oldname,newName);
		}
	}

	private void parseWrite(StringToParse cLine)	{	
		currentDatabase.writeDatabase();
		currentRelStack.pop();
	}

	private void parseDelete(StringToParse cLine)	{
		cLine.parseCurrentChar(FIELD_DELIMITER);
		currentDatabase.removeTable(cLine.getDelimitedToken(cLine,'$'));
	}

	private void parseRename(StringToParse cLine)	{
		cLine.parseCurrentChar('$');
		currentDatabase.changeTableName(currentRelStack.peek().getTableName(),cLine.getDelimitedToken(cLine,'$'));
	}

	private void parseSave(StringToParse cLine)	{
		if(currentDatabase.addTable(currentRelStack.peek()) == 1 )	{
			currentRelStack.pop();
		}
	}
	private void parseClear(StringToParse cLine)	{
		currentRelStack.pop();
	}

	private void parseNewRecord(StringToParse cLine)	{
		if(currentRelStack.peek().addRecord(parseField(cLine)) == 0)	{
			throw new SyntaxErrorException("Record - Field Mismatch");
		}
	}

	private Field[] parseField(StringToParse cLine)	{
		String fieldValue;
		ArrayList<Field> fieldsToAdd = new ArrayList<Field>();
		for(int i = 0; cLine.hasNext();i++)	{
			cLine.parseCurrentChar('$');
			fieldValue = trim(cLine.getDelimitedToken(cLine,'$'));
			try{
				if(validateFieldType(fieldValue,currentRelStack.peek().getColumn(i).getColumnType()) != null)	{
					if(currentRelStack.peek().getColumn(i).getReference() != null)	{
						if(!currentDatabase.validateFieldReference(fieldValue,currentRelStack.peek().getColumn(i)))	{
							throw new Exception();
						}
					}
					fieldsToAdd.add(new Field(fieldValue,currentRelStack.peek().getColumn(i).getColumnType()));
				} else	{
					throw new Exception();
				}
			} catch(Exception e)	{
				return null;
			}

		}
		return fieldsToAdd.toArray(new Field[fieldsToAdd.size()]);
	}



	private FieldDataType validateFieldType(String value, FieldDataType expected)	{
		try {
			switch(expected)	{
				case STRING:
					return FieldDataType.STRING;
				case INTEGER:
					if(ifInteger(value))	{
						return FieldDataType.INTEGER;
					} else	{
							throw new Exception("Expected integer");
					}
				default:
					throw new Exception("Unknown type to compare field against");
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchException(e,"Field has wrong type");
		}
		return null;
	}

	 private boolean ifInteger(String val)	{
	 	try	{
	 		Integer.parseInt(val);
	 	} catch (NumberFormatException e){
	 		return false;
	 	}
	 	return true;
	 }

	private void parseKeepRows(StringToParse cLine)	{
		try{
			String columnName;
			String expression;
			while(cLine.hasNext())	{
				cLine.parseCurrentChar(FIELD_DELIMITER);
				columnName = cLine.getDelimitedToken(cLine,EQUIVALENCE);
				cLine.parseCurrentChar(EQUIVALENCE);
				cLine.parseCurrentChar(FIELD_DELIMITER);
				expression = cLine.getDelimitedToken(cLine,FIELD_DELIMITER);
				currentRelStack.push(currentRelStack.pop().getMatchingRecords(columnName,expression));
			}
		} catch (SyntaxErrorException e)	{
			WhiteBoxTesting.catchException(e,"Syntax error");
		}
	}

	private void parseMatchingRows(StringToParse cLine)	{
		parseKeepRows(cLine);
	}

	private void parseListTables()	{
		interpreterOut.printString(currentDatabase.getSchema());
	}

	private void parseNewTable(StringToParse cLine)	{
		String tableName = parseTableName(cLine);
		cLine.consumeWhiteSpace();
		Column[] newCols = parseTableType(cLine);

		if(tableName.equals(""))	{
			for(Column c : newCols)	{
				if(c == newCols[newCols.length - 1])	{
					tableName += c.getColumnName();
				} else	{
					tableName += c.getColumnName() + ",";
				}
			}
		}
		currentRelStack.push(new Table(newCols,tableName));
		
	}

	private String parseTableName(StringToParse cLine)	{
		String tName = new String("");

		if(cLine.getCurrentChar() == '\"')	{
			cLine.parseCurrentChar('\"');
			tName = cLine.getDelimitedToken(cLine,'\"');
			cLine.parseCurrentChar('\"');
		}
		
		return tName;
	}

	private Column[] parseTableType(StringToParse cLine)	{
		ArrayList<Column> newColumns = new ArrayList<Column>();
		String columnName = new String();
		FieldDataType columnKeyType = null;
		FieldDataType columnType = null;
		String token = new String();
		String references = null;
		try{
			cLine.parseCurrentChar(FIELD_DELIMITER);
			while(cLine.hasNext())	{  //!Refactor.
				columnName = cLine.getDelimitedToken(cLine,FIELD_DELIMITER);
				cLine.parseCurrentChar(FIELD_DELIMITER);
				token = cLine.getNextTokenBy('$');
				while(translateTypeField(trim(token)) != null)	{
					switch(trim(token))	{
						case KEY_TYPE:
							columnKeyType = FieldDataType.PKEY;
							break;
						case STRING_TYPE:
							columnType = FieldDataType.STRING;
							break;
						case INTEGER_TYPE:
							columnType = FieldDataType.INTEGER;
							break;
						case FKEY_TYPE:
							columnKeyType = FieldDataType.FKEY;
							if((references = validateReferences(trim(cLine.getNextTokenBy('$')))) == null){
								throw new Exception("Reference failure: Table or reference field does not exist");
							}
							break;
						default:
							break;
					}
					token = cLine.getNextTokenBy('$');
				}
				if(cLine.hasNext())	{
					cLine.stepBack(token.length() + 1); //!Stepping back as last chunk wasn't a token
				}
				if(columnKeyType == null)	{
					newColumns.add(new Column(columnName,columnType));
				} else	{
					if(references == null)	{
						newColumns.add(new Column(columnName,columnType,columnKeyType));
					} else{
						newColumns.add(new Column(columnName,columnType,columnKeyType,references));
					}
				}
				columnKeyType = null;
				references = null;
			}
		return newColumns.toArray(new Column[newColumns.size()]);
		} catch (Exception e)	{
			WhiteBoxTesting.catchException(e,"Syntax Error");
			return null;
		}
	}

	private String validateReferences(String reference)	{
		try {
			if(currentDatabase.getTable(reference) != null && currentDatabase.getTable(reference).hasKey())	{
				return reference;
			} else	{
				throw new Exception();
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchException(e,"Cannot make Foreign key Reference");
			return null;
		}
	}

	private FieldDataType translateTypeField(String token)	{
		switch(trim(token))	{
			case KEY_TYPE:
				return FieldDataType.PKEY;
			case STRING_TYPE:
				return FieldDataType.STRING;
			case INTEGER_TYPE:
				return FieldDataType.INTEGER;
			case FKEY_TYPE:
				return FieldDataType.FKEY;
			default:
				return null;
			}
	}

	private String trim(String stringToTrim)	{
		StringBuffer sb = new StringBuffer(stringToTrim);
		if(stringToTrim.length() > 0)	{
			while(sb.charAt(sb.length() - 1) == ' ')	{
				sb.deleteCharAt(sb.length() - 1);
			}
			while(sb.charAt(0) == ' ')	{
				sb.deleteCharAt(0);
			}
		}
		return sb.toString();
	}


	private void parseKeepColumns()	{
		try {
		String[] cNames = parseColumnNames(currentLine);
		currentRelStack.push(currentRelStack.peek().pullColumns(cNames));
		} catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Syntax error");
		}
	}

	private String[] parseColumnNames(StringToParse cLine) {
		ArrayList<String> cNames = new ArrayList<String>();
		StringBuffer columnName = new StringBuffer();
		try { 
			 while(cLine.hasNext())	{
			 	cLine.parseCurrentChar(FIELD_DELIMITER);
				cNames.add(cLine.getDelimitedToken(cLine,FIELD_DELIMITER));				
			}
			return cNames.toArray(new String[cNames.size()]);
		} catch (SyntaxErrorException e)	{
			WhiteBoxTesting.catchException(e,"Syntax error");
			return null;
		}
	}

	private void parseLoadTable()	{
		StringBuffer tName = new StringBuffer();
		while(currentLine.hasNext())	{
			tName.append(currentLine.getNextTokenBy(' ') + " ");
		}
		tName.deleteCharAt(tName.length() - 1);
		String tableName = new String(tName.toString());
		try	{
			currentRelStack.push(currentDatabase.getTable(tableName));
		} catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Table does not exist");
		}
	}

	private int outputCurrentRelation()	{
		try{
			if(currentRelStack.getSize() > 0)	{
				interpreterOut.printTable(currentRelStack.peek());
				return 1;
			}
			return 0;
		} catch (NullPointerException e)	{
			return WhiteBoxTesting.catchException(e,"No relation In Stack");
		}
	}

	/*----------Testing----------*/

	public static void main( String[] args )    {
	 
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			Interpreter.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		Interpreter.unitTest_parseLoadTable(t);
		Interpreter.unitTest_parseKeepColumn(t);
		Interpreter.unitTest_parseKeepRows(t);
		Interpreter.unitTest_parseAddRecord(t);
		Interpreter.unitTest_addingColumns(t);
		Interpreter.unitTest_foreignKeys(t);
		Interpreter.unitTest_updateTableName(t);
		return t;
	}

	public static Testing unitTest_parseAddRecord(Testing t)	{
	WhiteBoxTesting.startTesting();
	t.enterSuite("Interpreter Unit Tests: Parse Adding Record");
	Database db = new Database("InterpreterDB");
	RelationStack rs = new RelationStack();
	Interpreter i = new Interpreter(db,rs);
	String[] cNames = new String[3];
	StringToParse stp = new StringToParse();
	Table tab = Table.spawnTestDB("testTable");

	db.addTable(tab);

	i.parse("table testTable");
	i.parse("insert $6 $boat $ferr\\$ari");
	t.compare("6,INTEGER,boat,STRING,ferr$ari,STRING","==",rs.peek().getRecordByKey("6").toString(),"Newly added record is 6,boat,ferrari");
	t.compare(0,"==",i.parse("insert $7 $boat $ferrari $errorCOlumn"),"Invalid record attempted to be added");
	t.compare(0,"==",i.parse("insert $7 $errorCOlumn"),"Invalid record attempted to be added");
	tab = Table.spawnTestDB("testTable2");
	db.addTable(tab);
	i.parse("table testTable2");
	i.parse("insert $testing $boat $ferrari");
	DataOutput dO = new DataOutput();
	
	i.parse("insert $8 $2 $3");
	t.exitSuite();
	return t;
	}

	public static Testing unitTest_updateTableName(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Interpreter Unit Tests: Parse Renaming Table");
		Database db = new Database("testFK");
		RelationStack rs = new RelationStack();
		Interpreter i = new Interpreter(db,rs);
		String[] cNames = new String[3];
		StringToParse stp = new StringToParse();
		t.compare("Region","==",db.getTable("Product").getColumn(1).getReference(),"Table references Region Table");
		i.parse("table Region");
		i.parse("tableName $Country");
		t.compare("Country","==",db.getTable("Product").getColumn(1).getReference(),"Table references Country Table: Change has cascaded");
		i.parse("table Country");
		i.parse("tableName $Region");
		t.compare("Region","==",db.getTable("Product").getColumn(1).getReference(),"Table references Region Table");
		//i.parse("write");
		t.exitSuite();
		return t;	
	}

	public static Testing unitTest_parseKeepRows(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Interpreter Unit Tests: Parse Keep Rows");
		Database db = new Database("InterpreterDB");
		RelationStack rs = new RelationStack();
		Interpreter i = new Interpreter(db,rs);
		String[] cNames = new String[3];
		StringToParse stp = new StringToParse();
		Table tab = Table.spawnTestDB("testTable");
		db.addTable(tab);
		i.parse("table testTable"); 
		stp.addLine("$Vehicle=$Car");
		i.parseKeepRows(stp);
		t.compare(1,"==",rs.peek().getCardinality(),"One row remaining");
		t.compare("Honda","==",rs.peek().getFieldValueByColumnName("1","Manufacturer"),"Remaining row has Manufacturer Honda");
		i.parse("table testTable"); 
		i.parse("rows $Manufacturer=$Suzuki $Vehicle=$Van");
		t.exitSuite();
		return t;		
	}

	public static Testing unitTest_parseKeepColumn(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Interpreter Unit Tests: Parse Keep Column");
		Database db = new Database("InterpreterDB");
		RelationStack rs = new RelationStack();
		Interpreter i = new Interpreter(db,rs);
		String[] cNames = new String[3];
		StringToParse stp = new StringToParse();
		stp.addLine("$colu\\$mn1 $col umn2 $colum \\$n3");
		cNames = i.parseColumnNames(stp);
		t.compare(cNames[0],"==","colu$mn1","column 1 name parsed correctly");
		t.compare(cNames[1],"==","col umn2","column 2 name parsed correctly");
		t.compare(cNames[2],"==","colum $n3","column 3 name parsed correctly");
		stp.addLine("$c\\$1 $key $string $\\$c 2 $string $column3 $string");
		Column[] c = i.parseTableType(stp);
		t.compare("c$1","==",c[0].getColumnName(),"Column name 1 is c$1");
		t.compare("$c 2","==",c[1].getColumnName(),"Column name 1 is $c 2");
		t.compare("column3","==",c[2].getColumnName(),"Column name 1 is $column3");
		stp.addLine("c\\$1 $ky $sring $\\$c 2 $string $column3 $string");
		t.compare(null,"==",i.parseTableType(stp),"Invalid syntax for table creation");
		stp.addLine("$c\\$1 $key $string $\\$c 2 $string $column3 $string");
		i.parseNewTable(stp);
		stp.addLine("\"THISISMYT\\\\ABLENAME\" $col2");
		t.compare("THISISMYT\\ABLENAME","==",i.parseTableName(stp),"table name is THISISMYT\\ABLENAME");
		i.parse("new \"myTable\" $c1 $key $string $c2 $string $c3 $string");
		t.compare("myTable","==",rs.peek().getTableName(),"Created table called myTable");
		i.parse("new $c1 $key $string $c2 $string $c3 $string");
		t.compare("c1,c2,c3","==",rs.peek().getTableName(),"Created table called c1,c2,c3");
		t.exitSuite();
		return t;
	}

	public static Testing unitTest_foreignKeys(Testing t)	{

		WhiteBoxTesting.startTesting();
        t.enterSuite("Interpreter Unit Tests: Parse Joining Table");
		Database db = new Database("testDB");
		RelationStack rs = new RelationStack();
		Interpreter i = new Interpreter(db,rs);
		DataOutput dOut = new DataOutput();
		i.parse("table table1");	
		i.parse("table table1");
		i.parse("new \"Countries\" $ID $key $string $Manu_ID $refers $table1 $string $Country $string");
		t.compare("table1","==",rs.peek().getColumn(rs.peek().getColumnIndex("Manu_ID")).getReference(),"New table refers to table1");
		t.compare(0,"==",i.parse("insert $1 $10 $UK"),"Tried to add foreign key record that doesn't exist");
		t.compare(1,"==",i.parse("insert $5 $1 $UK"),"Tried to add foreign key record that exists");
		t.compare(1,"==",i.parse("insert $6 $2 $Japan"),"Tried to add foreign key record that exists");
		t.compare(1,"==",i.parse("insert $7 $3 $USA"),"Tried to add foreign key record that exists");
		t.compare(1,"==",i.parse("insert $8 $4 $France"),"Tried to add foreign key record that exists");
        t.exitSuite();
        return t;
	}

	public static Testing unitTest_addingColumns(Testing t)	{
        WhiteBoxTesting.startTesting();
        t.enterSuite("Interpreter Unit Tests: Adding columns to table");
		Database db = new Database("testDB");
		RelationStack rs = new RelationStack();
		Interpreter i = new Interpreter(db,rs);
		i.parse("table table1");
		t.compare(2,"==",rs.peek().getWidth(),"Table width is 2");
		i.parse("append $col3 $string $col4 $integer");
		
		t.compare(4,"==",rs.peek().getWidth(),"Table width is 4");
		i.parse("append $col5 $key");
		t.compare(4,"==",rs.peek().getWidth(),"Table width is still 4: Can't add key Field");
		t.compare(2,"==",rs.peek().getColumnIndex("col3"),"col3 is at index 2");
		t.compare(3,"==",rs.peek().getColumnIndex("col4"),"col4 is at index 3");
		i.parse("rename $col4=$newName $col3=$nextNewName");
		t.compare(2,"==",rs.peek().getColumnIndex("nextNewName"),"Renamed col3 as nextNewName is at index 2");
		t.compare(3,"==",rs.peek().getColumnIndex("newName"),"Renamed col4 as newNameis at index 3");
        t.exitSuite();
        return t;
	}

	public static Testing unitTest_parseLoadTable(Testing t)	{

        WhiteBoxTesting.startTesting();
        t.enterSuite("Interpreter Unit Tests: Parse Load Table");
        Database db = new Database("InterpreterDB");
        RelationStack rs = new RelationStack();

        db.addTable(Table.spawnTestDB("InterpreterTest"));
        Interpreter i = new Interpreter(db,rs);
        DataOutput dOut = new DataOutput();
        t.compare(0,"==",i.outputCurrentRelation(),"No relation to output");
        i.parse("table InterpreterTest");
        t.compare("InterpreterTest","==",rs.peek().getTableName(),"Table name at top of stack is InterpreterTest");
        t.compare(1,"==",i.outputCurrentRelation(),"Valid output of current relation");
        i.parse("table fakeTable");
        t.exitSuite();
        return t;
	}

}
