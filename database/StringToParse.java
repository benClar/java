import com.bclarke.testing.*;
import com.bclarke.general.*;

public class StringToParse  {

	String line;
	int charToParse;

	public StringToParse(String l)	{
		line = l;
		charToParse = 0;
	}

	public String getString()	{
		return line;
	}

	public String getLine()	{
		return line;
	}

	public char getCurrentChar()	{
		try	{
			return line.charAt(charToParse);
		} catch	(IndexOutOfBoundsException e)	{
			WhiteBoxTesting.catchFatalException(e,"No more characters in line");
			System.out.println(getCurrentPosition());
			return '\0';
		}
	}

	public int getCurrentPosition()	{
		return charToParse;
	}

	public void setString(String s)	{
		line = s;
		charToParse = 0;
	}

	public int length()	{
		return line.length();
	}

	public void next()	{
		charToParse++;
	}

	public boolean hasNext()	{
		if(charToParse < length())	{
			return true;
		}

		return false;
	}

	public void setCharToParse(int i)	{
		charToParse = i;
	}

	public String substring(int start, int end)	{
		return line.substring(start,end);
	}

/*----------Testing----------*/

	public static void main( String[] args )    {
	 
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			StringToParse.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("StringToParse Unit Tests");
		/*Unit Tests Here*/
		t.exitSuite();
		return t;
	}
}

