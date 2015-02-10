public class TestingSuite	{

	private int testsRan;
	private int testsPassed;
	private int testsFailed;
	private String suiteName;
	private TestingSuite previous;	

	public TestingSuite(String sName)	{
		testsRan = 0;
		testsPassed = 0;
		testsFailed = 0;
		suiteName = sName;
		previous = null;
	}

	public void link(TestingSuite prev)	{
		previous = prev;
	}

	public void newTest()	{
		testsRan++;
	}

	public void testPassed()	{
		testsPassed++;
	}

	public void testFailed()	{
		testsFailed++;
	}
}
