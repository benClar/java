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
		if(WhiteBoxTesting.checkMode(args))	{
			sg.testSafeGeneric(args).endTesting();
		}
	}	

	public Testing testSafeGeneric(String[] args)	{
		Testing t = new Testing();
        WhiteBoxTesting.startTesting();	
		t.enterSuite("SafeGeneric Unit Tests");
		t.compare(0,"==",SafeGeneric.stringToInt("Not An Int"),"Invalid string to integer conversion");
		t.compare(0,"!=",SafeGeneric.stringToInt("1000000"),"Valid string to integer conversion");
		t.exitSuite();
		return t;
	}
}
