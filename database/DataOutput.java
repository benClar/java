import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.* ;

public class DataOutput  {

	private ArrayList<Integer> charSize;

	public DataOutput()	{
		charSize = null;
	}

	public void printTableName(Table tableToPrint)	{
		try	{
			System.out.println(tableToPrint.getTableName());
		} catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Tried to print null table");
		}
	}

	public void printTable(Table tableToPrint)	{
		System.out.printf("\n");
		printTableName(tableToPrint);
		printTableColumnHeaders(tableToPrint);
		printTableRecords(tableToPrint);
	}

	private void printTableColumnHeaders(Table tableToPrint)	{
		System.out.printf("+ ");
		for(int columnHead = 0; columnHead < tableToPrint.getWidth(); columnHead++)	{
			System.out.printf("%s + ",formatField("",tableToPrint.getColumn(columnHead).getLongestFieldSize(),'-'));
		}
		System.out.printf("\n");
		System.out.printf("| ");
		for(int columnHead = 0; columnHead < tableToPrint.getWidth(); columnHead++)	{
			System.out.printf("%s | ",formatField(tableToPrint.getColumn(columnHead).getColumnName(),tableToPrint.getColumn(columnHead).getLongestFieldSize(),' '));
		}
		System.out.printf("\n");
		System.out.printf("+ ");
		for(int columnHead = 0; columnHead < tableToPrint.getWidth(); columnHead++)	{
			System.out.printf("%s + ",formatField("",tableToPrint.getColumn(columnHead).getLongestFieldSize(),'-'));
		}
		
		System.out.printf("\n");
	}

	private void printTableRecords(Table tableToPrint)	{
		StringBuffer recordToPrint = new StringBuffer();
		Set<Record> copyOfSet = tableToPrint.getRecordSet();

		for(Record rToPrint : copyOfSet)	{
			System.out.printf("| ");
			for(int field = 0; field < rToPrint.getNumberOfFields(); field++)	{
				System.out.printf("%s | ",formatField(rToPrint.getField(field).getValue(),tableToPrint.getColumn(field).getLongestFieldSize(),' '
					));
			}
			System.out.printf("\n");	
		}
		System.out.printf("+ ");
		for(int columnHead = 0; columnHead < tableToPrint.getWidth(); columnHead++)	{
			System.out.printf("%s + ",formatField("",tableToPrint.getColumn(columnHead).getLongestFieldSize(),'-'));
		}
		System.out.printf("\n");	
	}

	public void printString(String toPrint)	{
		System.out.println();
		System.out.println(toPrint);
	}

	public void printStringArray(String[] sArr)	{
		System.out.println();
		for(String stringElement : sArr)	{
			System.out.println(stringElement);
		}
	}	

	private String formatField(String fieldToPrint, int longest,char toApp)	{
		StringBuffer recordToPrint = new StringBuffer();
		recordToPrint.append(fieldToPrint);
		int spacesToAppend = (longest - recordToPrint.length());
		try	{
			if(spacesToAppend < 0)	{
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e)	{
			WhiteBoxTesting.catchFatalException(e,"Column has incorect field length stored");
			return null;
		}
		for(int i = 0;i < spacesToAppend; i++)	{
			recordToPrint.append(toApp);
		}

		return new String(recordToPrint);
	}

/*----------Testing----------*/

	public static void main( String[] args )    {
	 
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			DataOutput.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		DataOutput.unitTest_printTable(t);
		WhiteBoxTesting.startTesting();
		return t;
	}

	private static Testing unitTest_printTable(Testing t)	{
		t.enterSuite("DataOutput Unit Tests");
		TableReader tr = new TableReader("text","testTable_toRead");
		DataOutput d = new DataOutput();
		t.compare("val3      ","==",d.formatField("val3",10,' '),"Correct amount of spaces appended to string");
		t.exitSuite();
		return t;
	}
}