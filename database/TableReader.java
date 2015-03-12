import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.* ;
import java.io.File;
import java.io.FileNotFoundException;

public class TableReader  {

	private Scanner tableFile;
	private Table tableToRead;
	private StringToParse lineToParse;

	private final char ESCAPE_CHAR = '\\';
	private final char DELIMITER = ',';
	private final char START_META = '[';
	private final char END_META = ']';
	private final String DATABASE_ROOT_DIRECTORY = "resources";
	public TableReader(String databaseName, String tableName)	{

		tableFile = openFile(DATABASE_ROOT_DIRECTORY + "/" + databaseName + "/" + tableName + ".txt");
		lineToParse = new StringToParse(getNextLine(tableFile));
		tableToRead = new Table(createColumns(lineToParse),tableName);
		populateTable(tableToRead,tableFile);
		closeFile();

	}

	private String getNextLine(Scanner t)	{
		try{
			return t.nextLine();
		} catch (NoSuchElementException e)	{
			WhiteBoxTesting.catchException(e,"Tried to read from empty file");
			return null;
		}
	}

	public TableReader()	{
		tableFile = null;
		tableToRead = null;
		lineToParse = null;
	}

	public Table readTable(String databaseName, String tableName)	{
		tableFile = openFile(DATABASE_ROOT_DIRECTORY + "/" + databaseName + "/" + tableName + ".txt");
		lineToParse = new StringToParse(tableFile.nextLine());
		tableToRead = new Table(createColumns(lineToParse),tableName);
		populateTable(tableToRead,tableFile);
		closeFile();
		return getTable();
	}

	private void populateTable(Table targetTable, Scanner file)	{
		while(file.hasNext())	{
		 	Field[] f = new Field[targetTable.getWidth()];
		 	String[] rowValues= clean(sliceLine(file.nextLine()));

		 	for(int i = 0; i < rowValues.length; i++)	{
		 		f[i] = new Field(rowValues[i],targetTable.getColumn(i).getColumnType());
		 	}
		 	targetTable.addRecord(f);
		}
	}

	private void closeFile()	{
		tableFile.close();
	}

	public Table getTable()	{
		return tableToRead;
	}

	private Column[] createColumns(StringToParse cols)	{
		ArrayList<Column> columns = new ArrayList<Column>();

		while(cols.hasNext())	{
			columns.add(getColumn(cols));
			if(cols.hasNext())	{
				parseCurrentChar(cols,',');
			}
		}

		return columns.toArray(new Column[columns.size()]);
	}

	private Column getColumn(StringToParse cols)	{
		String colName;
		FieldDataType colType = null;
		FieldDataType key = FieldDataType.NONKEY;
		colName = parseColName(cols);
		parseCurrentChar(cols,'[');
		while(cols.getCurrentChar() != END_META)	{
			parseCurrentChar(cols,'{');
			switch(getTag(cols))	{
				case "type":
						colType = parseType(cols);
						break;
				case "key":
						key = parseKey(cols);
					break;
				default:
					break;
			}
			parseCurrentChar(cols,'}');
		}
		parseCurrentChar(cols,']');
		return new Column(new String(clean(new StringBuffer(colName))),colType,key);		
	}

	private int parseCurrentChar(StringToParse line, char expected)	{
		try{
			if(line.getCurrentChar() != expected)	{
				throw new Exception();
			}
		} catch (Exception e)	{
			return WhiteBoxTesting.catchFatalException(e,"Expected " + expected +  " in metadata");
		}
		line.next();
		return 1;
	}

	private String parseColName(StringToParse columnLine)	{
		int start = columnLine.getCurrentPosition();
		for( ;columnLine.hasNext(); columnLine.next())	{
			if(columnLine.getCurrentChar() == '\\')	{
				columnLine.next();
			} else if (columnLine.getCurrentChar() == '[')	{
				return columnLine.substring(start,columnLine.getCurrentPosition());
			}
		}

		try {
			throw new Exception();
		} catch(Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Column name unable to be found");
			return null;
		}
	}


	private int parseTagEnd(StringToParse line)	{
		try {
			if(line.getCurrentChar() != '}')	{
				throw new Exception();
			}
		} catch (Exception e)	{
			return WhiteBoxTesting.catchFatalException(e,"Expected } to end tag");
		}
		line.next();

		return 1;
	}

	/**!May make up part of recursive descent parser: will parse type tages*/
	private FieldDataType parseMetaDataTag(StringToParse line)	{
		parseTagOpen(line);
		try{
			switch(getTag(line))	{
				case "type" :
					
					return parseType(line);
				default:
					throw new Exception();
			}
		}	catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Unrecognised tag in meta-data");
		}

		return null;
	}

	private FieldDataType parseKey(StringToParse line)	{
		try{
			parseCurrentChar(line,':');	
			switch(parseTagValue(line))	{
				case "primaryKey":
					return FieldDataType.PKEY;
				default:
					throw new Exception();
			}
		} catch(Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Unknown Key Type");
		}
		return null;
	}

	private FieldDataType parseType(StringToParse line)	{
		try{
			parseCurrentChar(line,':');
			switch(parseTagValue(line))	{
				case "string":
					return FieldDataType.STRING;
				default:
					throw new Exception();
			}
		}catch(Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Unknown Data Type");
		}

		return null;
	}

	private String parseTagValue(StringToParse line)	{
		StringBuffer tagValue = new StringBuffer();
		for(; line.getCurrentChar() != '}'; line.next())	{
			tagValue.append(line.getCurrentChar());
		}
		return new String(tagValue);
	}

	private String getTag(StringToParse line)	{
		StringBuffer tag = new StringBuffer();
		for(; line.getCurrentChar() != ':'; line.next())	{
			tag.append(line.getCurrentChar());
		}
		return new String(tag);
	}

	private int parseTagOpen(StringToParse line)	{
		try{
			if(line.getCurrentChar() != '{')	{
				throw new Exception();
			}
		}catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Expected { to begin metaData Tag");
		}

		line.next();
		return 1;
	}

	/*
	 * Slices line into tokens
	 */
	private StringBuffer[] sliceLine(String line)	{

		ArrayList<StringBuffer> chunks = new ArrayList<StringBuffer>();
		
		int i, chunkStart;
		for(i = 0, chunkStart = 0; i < line.length(); i++)	{
			if(line.charAt(i) == '\\' && i != line.length() - 1)	{
				i++;
			} else	if(line.charAt(i) == ',')	{
				chunks.add(new StringBuffer(line.substring(chunkStart, i)));
				chunkStart = i + 1;
			}
		}
		chunks.add(new StringBuffer(line.substring(chunkStart, i)));
		StringBuffer[] finalChunks = new StringBuffer[chunks.size()]; 
		finalChunks = chunks.toArray(finalChunks);
		return finalChunks;
	}

	private String[] clean(StringBuffer[] line)	{
		String[] cleanedLine = new String[line.length];
		for(int s = 0; s < line.length; s++)	{
			cleanedLine[s] = clean(line[s]);
		}
		return cleanedLine;	
	}

	private String clean(StringBuffer line)	{
		for(int c = 0; c < line.length(); c++)	{
			if(line.charAt(c) == '\\')	{
				if(c != (line.length() - 1))	{
					if(line.charAt(c + 1) != '\\')	{
						line.deleteCharAt(c);	
					}
				}
			}
		}
		return line.toString();
	}

	private Scanner openFile(String f)	{
		Scanner newFile;

		try	{
			newFile = new Scanner(new File(f));	
		} catch(FileNotFoundException e)	{
			WhiteBoxTesting.catchException(e,"File not found");
			return null;
		}
		return newFile;
	}

/*----------Testing----------*/

	public static void main( String[] args )    {
	 
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			TableReader.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		TableReader.unitTest_OpeningFile(t);
		TableReader.unitTest_readingFile(t);
		TableReader.unitTest_columnParsing(t);
		TableReader.unitTest_creatingTableReader(t);
		return t;
	}

	private static Testing unitTest_OpeningFile(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableReader Unit Tests: Opening file");
		TableReader tr = new TableReader("text","testTable_types");
		tr.closeFile();
		t.compare(null,"!=",tr.openFile(tr.DATABASE_ROOT_DIRECTORY + "/" +  "text/testTable.txt"),"Test Table file has been opened");
		t.compare(null,"==",tr.openFile("text/fakeFile"),"Test Table file does not exist");
		tr.closeFile();
		t.exitSuite();
		return t;
	}

	private static Testing unitTest_readingFile(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableReader Unit Tests: Reading file");

		TableReader tr = new TableReader("text","testTable_types");
		tr.closeFile();

		StringBuffer[] tokenized = tr.sliceLine("col1,col2,col3");

		String[] testLine = new String[tokenized.length];
		for (int i = 0; i < tokenized.length; i++)	{
			testLine[i] = tokenized[i].toString();
		}

		t.compare("col1","==",testLine[0],"Token one of Comma Delimited Line is col1");
		t.compare("col2","==",testLine[1],"Token one of Comma Delimited Line is col2");
		t.compare("col3","==",testLine[2],"Token one of Comma Delimited Line is col3");

		tokenized = tr.sliceLine("col\\,1,co\\,l2,col3\\");
		for (int i = 0; i < tokenized.length; i++)	{
			testLine[i] = tokenized[i].toString();
		}
		t.compare("col\\,1","==",testLine[0],"Token one of Comma Delimited Line is col\\,1");
		t.compare("co\\,l2","==",testLine[1],"Token one of Comma Delimited Line is co\\,l2");
		t.compare("col3\\","==",testLine[2],"Token one of Comma Delimited Line is col3\\");

		String[] cleaned = tr.clean(tokenized);
		t.compare("col,1","==",cleaned[0],"Token one is cleaned to col,1");
		t.compare("co,l2","==",cleaned[1],"Token two cleaned to co,l2");
		t.compare("col3\\","==",cleaned[2],"Token Three cleaned to col3\\");

		tokenized = tr.sliceLine("\\\\\\,\\,,col2,col3");
		for (int i = 0; i < tokenized.length; i++)	{
			testLine[i] = tokenized[i].toString();
		}
		t.compare("\\\\\\,\\,","==",testLine[0],"Token one of Comma Delimited Line is \\\\\\,\\,");
		t.compare("col2","==",testLine[1],"Token two of Comma Delimited Line is col2");
		t.compare("col3","==",testLine[2],"Token three of Comma Delimited Line is col3\\");
		
		cleaned = tr.clean(tokenized);
		t.compare("\\\\,,","==",cleaned[0],"Token one is cleaned to \\\\,,");
		t.compare("col2","==",cleaned[1],"Token two is cleaned to col2");
		t.compare("col3","==",cleaned[2],"Token three is cleaned to col3");

		tr.closeFile();
		t.exitSuite();
		return t;
	}

	private static Testing unitTest_columnParsing(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableReader Unit Tests: Reading file");
		TableReader tr = new TableReader("text","testTable_types");
		StringToParse testS = new StringToParse("col1[{type: string}]");
		t.compare("col1","==",new String(tr.parseColName(testS)),"column name in this string is col1");
		testS.setString("col\\,1[{type: string}]");
		t.compare("col,1","==",tr.clean(new StringBuffer(tr.parseColName(testS))),"cleaned column name in this string is col,1");
		testS.setString("col1[{type:string}]");
		testS.setCharToParse(5);
		t.compare(1,"==",tr.parseTagOpen(testS),"Tag open symbol present");
		t.compare("type","==",tr.getTag(testS),"Tag is type");
		t.compare(1,"==",tr.parseCurrentChar(testS,':'),"Tag Delimiter symbol present");
		t.compare("string","==",tr.parseTagValue(testS),"Tag value is string");
		testS.setCharToParse(5);
		t.compare(FieldDataType.STRING,"==",tr.parseMetaDataTag(testS),"MetaData Type in tag is string");
		testS.setString("co\\[l1[{type:string}],col2[{type:string}],col3[{type:string}]");
		Column[] testCol = new Column[3];
		testCol = tr.createColumns(testS);
		t.compare("co[l1","==",testCol[0].getColumnName(),"Column 1 is named co\\[l1");
		t.compare("col2","==",testCol[1].getColumnName(),"Column 2 is named col2");
		t.compare("col3","==",testCol[2].getColumnName(),"Column 3 is named col3");
		t.compare(FieldDataType.STRING,"==",testCol[0].getColumnType(),"Column 1 type is STRING");
		t.compare(FieldDataType.STRING,"==",testCol[1].getColumnType(),"Column 2 type is STRING");
		t.compare(FieldDataType.STRING,"==",testCol[2].getColumnType(),"Column 3 type is STRING");
		t.exitSuite();
		return t;
	}

	private static Testing unitTest_creatingTableReader(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableReader Unit Tests: Creating and populating Table Reader Object");
		TableReader tr = new TableReader("text","testTable_types");
		Table tab = tr.getTable();
		t.compare("c,ol1","==",tab.getColumnName(0),"Column 1 is called c,ol1");
		t.compare("c[o]l2","==",tab.getColumnName(1),"Column 2 is called c[o]l2");
		t.compare("col3","==",tab.getColumnName(2),"Column 3 is called col3");
		t.compare("ERROR","==",tab.getColumnName(3),"Column 4 doesn't exist");
		int fVal = 1;
		for(int field = 0; field < tab.getWidth(); field++, fVal++)	{
			t.compare("f"+fVal,"==",tab.getFieldValue("f1",field),"Field" + fVal + " has value f" + fVal);
		}
		for(int field = 0; field < tab.getWidth(); field++, fVal++)	{
			t.compare("f"+fVal,"==",tab.getFieldValue("f4",field),"Field" + fVal + " has value f" + fVal);
		}
		for(int field = 0; field < tab.getWidth(); field++, fVal++)	{
			t.compare("f"+fVal,"==",tab.getFieldValue("f7",field),"Field" + fVal + " has value f" + fVal);
		}		
		t.exitSuite();
		return t;
	}
}
