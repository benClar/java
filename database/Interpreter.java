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
	private final String KEY_TYPE = "key";
	private final char FIELD_DELIMITER = '$';
	private final char ESCAPE_CHAR = '\\';

	public Interpreter(Database d, RelationStack rs)	{
		currentDatabase = d;
		currentRelStack = rs;
		interpreterOut = new DataOutput();
		currentLine = new StringToParse();
	}

	public void parse(String line)	{
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
		 		default:
		 			throw new SyntaxErrorException(token + " :Unrecognised Command");
		 	}
	 	} catch (SyntaxErrorException e)	{
	 		WhiteBoxTesting.catchException(e,"Syntax Error");
	 	}
	}

	private void parseNewTable(StringToParse cLine)	{
		Column[] newCols = parseTableType(cLine);
		String tableName = new String();
		for(Column c : newCols)	{
			if(c == newCols[newCols.length - 1])	{
				tableName += c.getColumnName();
			} else	{
				tableName += c.getColumnName() + ",";
			}
		}
		currentRelStack.push(new Table(newCols,tableName));
		interpreterOut.printTable(currentRelStack.peek());
	}

	private Column[] parseTableType(StringToParse cLine)	{
		ArrayList<Column> newColumns = new ArrayList<Column>();
		String columnName = new String();
		FieldDataType columnKeyType = null;
		FieldDataType columnType = null;
		String token = new String();
		try{
		cLine.parseCurrentChar(FIELD_DELIMITER);
		while(cLine.hasNext())	{  //!Refactor.
			columnName = getColumnName(cLine);
			cLine.parseCurrentChar(FIELD_DELIMITER);
			token = cLine.getNextTokenBy('$');
			while(trim(token).equals(KEY_TYPE) || trim(token).equals(STRING_TYPE))	{
				switch(trim(token))	{
					case KEY_TYPE:
						columnKeyType = FieldDataType.PKEY;
						token = cLine.getNextTokenBy('$');
						break;
					case STRING_TYPE:
						columnType = FieldDataType.STRING;
						token = cLine.getNextTokenBy('$');
						break;
					default:
						break;
				}
			}
			if(cLine.hasNext())	{
				cLine.stepBack(token.length() + 1); //!Stepping back as last chunk wasn't a token
			}
			if(columnKeyType == null)	{
				newColumns.add(new Column(columnName,columnType));
			} else	{
				newColumns.add(new Column(columnName,columnType,columnKeyType));
			}
			columnKeyType = null;
		}
		return newColumns.toArray(new Column[newColumns.size()]);
		} catch (Exception e)	{
			WhiteBoxTesting.catchException(e,"Syntax Error");
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
		interpreterOut.printTable(currentRelStack.peek());
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
				cNames.add(getColumnName(cLine));				
			}
			return cNames.toArray(new String[cNames.size()]);
		} catch (SyntaxErrorException e)	{
			WhiteBoxTesting.catchException(e,"Syntax error");
			return null;
		}
	}

	private String getColumnName(StringToParse cLine)	{
		StringBuffer columnName = new StringBuffer();
		while(cLine.hasNext() && cLine.getCurrentChar() != FIELD_DELIMITER)	{
			if(cLine.getCurrentChar() == ESCAPE_CHAR)	{
				cLine.next();
				columnName.append(cLine.getNext());
			} else	{
				columnName.append(cLine.getNext());
			}
		}

		if(columnName.charAt(columnName.length()-1) == ' ')	{
			columnName.deleteCharAt(columnName.length()-1);
		}
		return columnName.toString();
	}

	private void parseLoadTable()	{
		StringBuffer tName = new StringBuffer();
		while(currentLine.hasNext())	{
			tName.append(currentLine.getNextTokenBy(' ') + " ");
		}
		tName.deleteCharAt(tName.length() - 1);
		String tableName = new String(tName.toString());
		try	{
			interpreterOut.printTableName(currentRelStack.push(currentDatabase.getTable(tableName)));
			outputCurrentRelation();
		} catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Table does not exist");
		}
	}

	private int outputCurrentRelation()	{
		try{
			interpreterOut.printTable(currentRelStack.peek());
			return 1;
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
