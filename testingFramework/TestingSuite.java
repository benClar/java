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

	public int getTestsRan()	{
		return testsRan;
	}

	public int getTestsPassed()	{
		return testsPassed;
	}

	public int getTestsFailed()	{
		return testsFailed;
	}

	public void testFailed()	{
		testsFailed++;
	}

	public void testPassed()	{
		testsPassed++;
	}

	public void newTest()	{
		testsRan++;
	}

	public void suiteStatus()	{
        System.out.printf("\n--> %d checks, %d checks Suceeded, %d failed (%.2f)\n\n",getTestsRan(),getTestsPassed(),getTestsFailed(),(double) getTestsPassed()/ (double) getTestsRan() * 100);
	}
}
