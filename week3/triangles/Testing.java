public class Testing	{

	private int suiteCount;
	private int totalTestsRan;
	private int totalTestsPassed;
	private int totalTestsFailed;

	TestingSuite currentSuite;

	public Testing()	{
		totalTestsRan = 0;
		totalTestsPassed = 0;
		totalTestsFailed = 0;
		suiteCount = 0;
		currentSuite = null;
	}

	public void enterSuite(String sName)	{
		suiteCount++;
		TestingSuite newSuite = new TestingSuite(sName);
		newSuite.link(currentSuite);
		currentSuite = newSuite;
	}

	public void is(Object x, Object y) {

   		tests++;
   		if (x == y)	{
			return;
		}
   		if (x != null && x.equals(y))	{
			   	return;
		}
    	throw new TestFailedException("Test failed: " + x + ", " + y);
	}

	public void newTest()	{
		totalTestsRan++;
	}	

	public void testPassed()	{
		totalTestsPassed++;
	}

	public void totalTestsFailed()	{
		totalTestsFailed++;
	}
}
