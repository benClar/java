import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.io.*;

public class TableWriter  {

	private Table tableToWrite;
	private BufferedWriter saveTable;

	private final char ESCAPE_CHAR = '\\';
	private final char DELIMITER = ',';
	private final char START_META = '[';
	private final char END_META = ']';

	public TableWriter(Table tab, String savePath)	{
		tableToWrite = tab;
		try	{
			saveTable = new BufferedWriter(new FileWriter(savePath));
		} catch (IOException e)	{
			WhiteBoxTesting.catchFatalException(e,"IOException");
		}
	}

	private int writeChar(char c)	{
		try	{
			saveTable.write(c);
		} catch (IOException e)	{
			return WhiteBoxTesting.catchFatalException(e,"IOException");
		}
			return 1;
	}

	private int writeString(String s)	{
		try	{
			saveTable.write(s);
			saveTable.newLine();
		} catch (IOException e)	{
			return WhiteBoxTesting.catchFatalException(e,"IOException");
		}
			return 1;
	}

	private void closeFile()	{
		try	{
			saveTable.close();
		} catch (IOException e)	{
			WhiteBoxTesting.catchFatalException(e,"IOException");
		}
	}

	private String transformColumn(Column col)	{
			return new String(escapeSpecialCharacters(col.getColumnName()) + '[' + formatMetaDataType(col) + formatMetaDataKey(col) + ']');
	}

	public void writeTable()	{
		writeString(formatColumnData(tableToWrite));
		for(int i = 0; i < tableToWrite.getCardinality(); i++)	{
			writeString(formatRow(tableToWrite.getRecord(i)));
		}
		closeFile();
	}

	private String formatMetaDataKey(Column col)	{
		try	{
			switch(col.getKeyType())	{
				case PKEY:
					return "{key:primaryKey}";
				case NONKEY:
					return "";
				default:
					throw new Exception("Unrecognised Key Type stored in column");
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Error Formating MetaData Key");
			return null;
		}
	}

	private String formatMetaDataType(Column col)	{
		try	{
			switch(col.getColumnType())	{
				case STRING:
					return "{type:string}";
				default:
					throw new Exception();
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Unknown datafield type stored in column");
			return null;
		}
	}

	private String escapeSpecialCharacters(String s)	{
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < s.length(); i++)	{
			if(s.charAt(i) == '[' || 
				s.charAt(i) == ']' ||
				s.charAt(i) == ',' || 
				s.charAt(i) == '\\')	{
					sb.append(ESCAPE_CHAR);
			}
			sb.append(s.charAt(i));
		}
		return new String(sb);
	}

	private String formatColumnData(Table tabToWrite)	{
		StringBuffer colData = new StringBuffer();
		for(int i = 0; i < tabToWrite.getWidth(); i++)	{
			colData.append(transformColumn(tabToWrite.getColumn(i)));
			if(i != tabToWrite.getWidth() -1)	{
				colData.append(DELIMITER);
			}
		}
		return new String(colData);
	}

	private String formatRow(Record recToWrite)	{
		StringBuffer row = new StringBuffer();

		for(int fie = 0; fie < recToWrite.getNumberOfFields(); fie++)	{
			row.append(escapeSpecialCharacters(recToWrite.getField(fie).getValue()));
			if(fie != recToWrite.getNumberOfFields() -1)	{
				row.append(DELIMITER);
			}
		}

		return new String(row);
	}



/*----------Testing----------*/

	public static void main( String[] args )    {
	 
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			TableWriter.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		TableWriter.unitTest_writingToFile(t);
		TableWriter.unitTest_transformingMetadata(t);
		return t;
	}

	public static Testing unitTest_writingToFile(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableWriter Unit Tests: Writing to file");
		TableReader tr = new TableReader("text/testTable_writing.txt");
		Table tab = tr.getTable();
		TableWriter tw = new TableWriter(tab, "text/testTable_written.txt");
		t.compare("c\\,ol1","==",tw.escapeSpecialCharacters(tab.getColumn(0).getColumnName()),"Transformed Column name is c\\,ol1");
		t.compare("{type:string}","==",tw.formatMetaDataType(tab.getColumn(0)),"Metadata format for this column is {type:string}");
		t.compare("c\\,ol1[{type:string}{key:primaryKey}]","==",tw.transformColumn(tab.getColumn(0)),"Column data to be written c\\,ol1[{type:string}{key:primaryKey}]");
		t.compare("c\\,ol1[{type:string}{key:primaryKey}],c\\[o\\]l2[{type:string}],col3[{type:string}]","==",tw.formatColumnData(tab),"Column data to write is c\\,ol1[{type:string}{key:primaryKey}],c\\[o\\]l2[{type:string}],col3[{type:string}]");
		t.compare("f1,f2,f3","==",tw.formatRow(tab.getRecord(0)),"first row is formated to string f1,f2,f3");
		t.compare("f\\,7,f\\[8,f\\]9\\,","==",tw.formatRow(tab.getRecord(2)),"first row is formated to string f\\,7,f\\[8,f\\]9\\,");
		tw.writeTable();
		tw.closeFile();
		t.exitSuite();
		return t;
	}

	public static Testing unitTest_transformingMetadata(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("TableWriter Unit Tests");
		TableReader tr = new TableReader("text/testTable_readingMetadata.txt");
		Table tab = tr.getTable();
		TableWriter tw = new TableWriter(tab, "text/testTable_writingMetadata.txt");
		t.compare("col1[{type:string}{key:primaryKey}]","==",tw.transformColumn(new Column("col1",FieldDataType.STRING, FieldDataType.PKEY)),"Formated Primary key column for writing is col1[{type:string}{key:primaryKey}]");
		t.compare("col1[{type:string}]","==",tw.transformColumn(new Column("col1",FieldDataType.STRING, FieldDataType.NONKEY)),"Formated Non key column for writing is col1[{type:string}]");
		tw.writeTable();
		tw.closeFile();
		t.exitSuite();
		return t;
	}
}

