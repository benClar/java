public class SafeGeneric {

	public static int stringToInt(String s)	{
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return WhiteBoxTesting.catchException(e,"Enter Valid Number");
		}
	}

	public static void main(String[] args)	{
		SafeGeneric sg = new SafeGeneric();
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST))	{
			sg.testSafeGeneric(new Testing()).endTesting();
		}
	}

	public static boolean checkForCharacter(char c, String s, String errMsg)	{
		int i;
		try {
			for(i = 0; i < s.length(); i++)	{
				if(s.charAt(i) == c)	{
					throw new IllegalArgumentException("Please provide integer numbers only");
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) { 
			WhiteBoxTesting.catchException(e,errMsg);
			return true;
		} catch (IllegalArgumentException e)	{
			WhiteBoxTesting.catchException(e,errMsg);
			return true;
		}
		return false;
	}	

	public Testing testSafeGeneric(Testing t)	{
        WhiteBoxTesting.startTesting();	
		t.enterSuite("SafeGeneric Unit Tests");
		t.compare(0,"==",SafeGeneric.stringToInt("Not An Int"),"Invalid string to integer conversion");
		t.compare(0,"!=",SafeGeneric.stringToInt("1000000"),"Valid string to integer conversion");
		t.compare(true,"==",SafeGeneric.checkForCharacter('.',"1.1",". detected:"),". exists in String");
		t.compare(false,"==",SafeGeneric.checkForCharacter('.',"11",". detected:"),". does not exist in String");
		t.exitSuite();
		return t;
	}
}
