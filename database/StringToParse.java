import com.bclarke.testing.*;
import com.bclarke.general.*;

public class StringToParse  {

	String line;
	int charToParse;

	public StringToParse(String l)	{
		line = validateString(l);
		charToParse = 0;
	}

	public StringToParse()	{
		line = "";
		charToParse = 0;
	}

	public void addLine(String l)	{
		line = l;
		charToParse = 0;
	}
	public String getString()	{
		return line;
	}

		public String getLine()	{
		return line;
	}

	public String getNextTokenBy(char delimiter)	{
		StringBuffer token = new StringBuffer();
		while(hasNext() && getCurrentChar() != delimiter )	{
			token.append(getCurrentChar());
			next();
		}
		next();
		return new String(token);
	}

	public String validateString(String l)	{
		try	{
			if(l == null || l == "")	{
				throw new Exception();
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Empty string passed to read");
			return null;
		}

		return l;
	}

	public int parseCurrentChar(char expected)	{
			if(getCurrentChar() != expected)	{
				throw new SyntaxErrorException("expected" + expected);
			}
			next();
		return 1;
	}

	public char getCurrentChar()	{
		try	{
			return line.charAt(charToParse);
		} catch	(IndexOutOfBoundsException e)	{
			WhiteBoxTesting.catchFatalException(e,"No more characters in line");
			return '\0';
		}
	}

	public char getNext()	{
		char curr = getCurrentChar();
		next();
		return curr;
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
		if(charToParse < length())	{
			charToParse++;
		}
	}

	public void stepBack(int steps)	{
		charToParse = charToParse - steps;
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
		StringToParse.unitTest_manipulatingStringLine(t);
		/*Unit Tests Here*/
		t.exitSuite();
		return t;
	}

	public static Testing unitTest_manipulatingStringLine(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("StringToParse Unit Tests");
		StringToParse sp = new StringToParse();
		sp.addLine("Word1 Word2 Word3");
		t.compare("Word1","==",sp.getNextTokenBy(' '),"First token in line is Word1");
		t.compare("Word2","==",sp.getNextTokenBy(' '),"First token in line is Word2");
		t.compare("Word3","==",sp.getNextTokenBy(' '),"First token in line is Word3");
		t.exitSuite();
		return t;
	}
}

