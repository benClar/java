import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.* ;
import java.io.File;
import java.io.FileNotFoundException;

public class TableReader  {

	private Scanner tableFile;
	private Table tableToRead;

	private final int TYPE_TAG_LENGTH = 4;
	public TableReader(String tableLocation)	{
		tableFile = openFile(tableLocation);
		//tableToRead = newTable(getColumns(tableFile.nextLine()));
		//while(tableFile.hasNext())	{
		// 	Field[] f = new Field[tableToRead.getWidth()];
		// 	tableToRead.addRecord();
		//}
	}

	private String[] returnLine(Scanner tFile)	{
		return clean(sliceLine(tFile.nextLine()));
	}

	private void closeFile()	{
		tableFile.close();
	}

	private Column[] getColumns(String firstLine)	{
		return interrogateColumns(sliceLine(firstLine));
	}	

	private Column[] interrogateColumns(StringBuffer[] cols)	{
		String[] colNames = new String[cols.length];
		FieldDataType[] colType = new FieldDataType[cols.length];
		Column[] columns = new Column[cols.length];
		for(int i = 0; i < cols.length; i++)	{
			colNames[i] = new String(parseColName(cols[i]));
			beginParseMetaData(new String(cols[i]),colNames[i].length());
			colType[i] = parseMetaDataTag(new String(cols[i]),colNames[i].length() + 1);
			columns[i] = new Column(new String(clean(new StringBuffer(colNames[i]))),colType[i]);
		}

		return columns;
	}

	private StringBuffer parseColName(StringBuffer columnLine)	{
		for(int i = 0; i < columnLine.length(); i++)	{
			if(columnLine.charAt(i) == '\\')	{
				i++;
			} else if (columnLine.charAt(i) == '[')	{
				return new StringBuffer(columnLine.substring(0,i));
			}
		}

		try {
			throw new Exception();
		} catch(Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Column name unable to be found");
			return null;
		}
	}


	private int beginParseMetaData(String line, int start)	{
		try {
			if(line.charAt(start)!= '[')	{

				throw new Exception();
			}
		} catch(Exception e)	{
			return WhiteBoxTesting.catchFatalException(e,"Expect [ to start metadata definition");	
		}

		return ++start;
	}

	/**!May make up part of recursive descent parser: will parse type tages*/
	private FieldDataType parseMetaDataTag(String line, int charToParse)	{
		charToParse = parseTagOpen(line, charToParse);
		try{
			switch(getTag(line,charToParse))	{
				case "type" :
					charToParse += TYPE_TAG_LENGTH;
					charToParse = parseTagDelimiter(line,charToParse);
					return parseType(line,charToParse);
				default:
					throw new Exception();
			}
		}	catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Unrecognised tag in meta-data");
		}

		return null;
	}

	private FieldDataType parseType(String line, int charToParse)	{
		try{
			switch(parseTagValue(line,charToParse))	{
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

	private String parseTagValue(String line, int charToParse)	{
		StringBuffer tagValue = new StringBuffer();
		for(int i = charToParse; line.charAt(i) != '}'; i++)	{
			tagValue.append(line.charAt(i));
		}
		return new String(tagValue);
	}

	private int parseTagDelimiter(String line, int charToParse)	{
		try{
			if(line.charAt(charToParse) != ':')	{
				throw new Exception();
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Expected : between tag and value");
		}

		return ++charToParse;
	}

	private String getTag(String line, int start)	{
		StringBuffer tag = new StringBuffer();
		for(int i = start; line.charAt(i) != ':'; i++)	{
			tag.append(line.charAt(i));
		}

		return new String(tag);
	}

	private int parseTagOpen(String line, int charToParse)	{
		try{
			if(line.charAt(charToParse) != '{')	{
				throw new Exception();
			}
		}catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Expected { to begin metaData Tag");
		}

		return ++charToParse;
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
			WhiteBoxTesting.catchFatalException(e,"File not found");
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
		return t;
	}

	private static Testing unitTest_OpeningFile(Testing t)	{
		WhiteBoxTesting.startTesting();
		TableReader tr = new TableReader("text/testTable.txt");
		tr.closeFile();
		t.enterSuite("TableReader Unit Tests: Opening file");
		t.compare(null,"!=",tr.openFile("text/testTable.txt"),"Test Table file has been opened");
		t.compare(null,"==",tr.openFile("text/fakeFile.txt"),"Test Table file does not exist");
		tr.closeFile();
		t.exitSuite();
		return t;
	}

	private static Testing unitTest_readingFile(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableReader Unit Tests: Reading file");

		TableReader tr = new TableReader("text/testTable.txt");
		tr.closeFile();
		Scanner testInput = tr.openFile("text/testTable.txt");

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

		cleaned = tr.returnLine(testInput);
		t.compare("col1","==",cleaned[0],"Token one read from file is col1");
		tr.closeFile();
		t.exitSuite();
		return t;
	}

	private static Testing unitTest_columnParsing(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableReader Unit Tests: Reading file");
		TableReader tr = new TableReader("text/testTable.txt");
		t.compare("col1","==",new String(tr.parseColName(new StringBuffer("col1[{type: string}]"))),"column name in this string is col1");
		t.compare("col,1","==",tr.clean(tr.parseColName(new StringBuffer("col\\,1[{type: string}]"))),
			"cleaned column name in this string is col,1");
		t.compare("type","==",tr.getTag("col1[{type:string}]",6),"Tag is type");
		t.compare(6,"==",tr.parseTagOpen("col1[{type:string}]",5),"Tag open symbol present at character 5");
		t.compare(11,"==",tr.parseTagDelimiter("col1[{type:string}]",10),"Tag Delimiter symbol present at character 6");
		t.compare("string","==",tr.parseTagValue("col1[{type:string}]",11),"Tag value is string");
		t.compare(FieldDataType.STRING,"==",tr.parseMetaDataTag("col1[{type:string}]",5),"MetaData Type in tag is string");
		Column[] testCol = new Column[3];
		testCol = tr.getColumns("co\\[l1[{type:string}],col2[{type:string}],col3[{type:string}]");
		t.compare("co[l1","==",testCol[0].getColumnName(),"Column 1 is named co\\[l1");
		t.compare("col2","==",testCol[1].getColumnName(),"Column 2 is named col2");
		t.compare("col3","==",testCol[2].getColumnName(),"Column 3 is named col3");
		t.compare(FieldDataType.STRING,"==",testCol[0].getColumnType(),"Column 1 type is STRING");
		t.compare(FieldDataType.STRING,"==",testCol[1].getColumnType(),"Column 2 type is STRING");
		t.compare(FieldDataType.STRING,"==",testCol[2].getColumnType(),"Column 3 type is STRING");

		t.exitSuite();
		return t;
	}
}
