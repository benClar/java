package com.bclarke.testing;
/*
 *Class that holds information about current test suite
 */

public class TestingSuite	{

	private int testsRan; 	//@ Number of tests ran in current suite
	private int testsPassed; 	//@ Number of tests that have passed in current suite
	private int testsFailed;	//@Number of tests that have failed in current suite
	private String suiteName;	//@Name of current suite
	private TestingSuite previous;	//@Previous Suite

	public TestingSuite(String sName)	{
		testsRan = 0;
		testsPassed = 0;
		testsFailed = 0;
		suiteName = sName;
		previous = null;
	}

	/*
	 *Links Current Suite node to previous
	 */
	public void link(TestingSuite prev)	{
		previous = prev;
	}

	/*
	 *Accessor for number of tests ran
	 */
	public int getTestsRan()	{
		return testsRan;
	}

	/*
	 *Accessor for tests passed
	 */
	public int getTestsPassed()	{
		return testsPassed;
	}

	/*
	 *Accessor for tests failed
	 */
	public int getTestsFailed()	{
		return testsFailed;
	}

	/*
	 *Increments tests failed
	 */
	public void testFailed()	{
		testsFailed++;
	}

	/*
	 *Increments tests passed
	 */
	public void testPassed()	{
		testsPassed++;
	}

	/*
	 *Increments number of tests ran
	 */
	public void newTest()	{
		testsRan++;
	}

	/*
	 *Prints status of Suite
	 */
	public void suiteStatus()	{
        System.out.printf("\n--> %d checks, %d checks Suceeded, %d failed (%.2f)\n\n",getTestsRan(),getTestsPassed(),getTestsFailed(),(double) getTestsPassed()/ (double) getTestsRan() * 100);
	}
}
