package com.bclarke.testing;
/*
 *Controls testing functionality and stores data on all testing suites
 */

public class Testing	{

	private int suiteCount; 	//! Number of suites that have been entered
	private int totalTestsRan;	//! Total number of tests that have been ran accross all suites
	private int totalTestsPassed; //! Total number of tests that have passed accross all suites;
	private int totalTestsFailed; //! Total number of tests that have failed accross all suites;

	private final String EQUALS = "==";	//!Equality operator
	private final String NOT_EQUALS = "!="; //!Inequality Operator

	TestingSuite currentSuite;

	public Testing()	{
		totalTestsRan = 0;
		totalTestsPassed = 0;
		totalTestsFailed = 0;
		suiteCount = 0;
		currentSuite = null;
	}

	/*
	 *Enters new testing suite
	 */
	public void enterSuite(String sName)	{
		//!Do test for null
		suiteCount++;
		System.out.println("== Entering suite #" + suiteCount + ",\"" + sName + "\""+ "==" + '\n'); 
		TestingSuite newSuite = new TestingSuite(sName);
		newSuite.link(currentSuite);
		currentSuite = newSuite;
	}

	/*
	 *Exits current testing suite
	 */
	public void exitSuite()	{
		currentSuite.suiteStatus();
	}

	/*
	 *Comparisons of objects passed into suite.  Based on Lecture slides.
	 */
	public void compare(Object x, String Operator, Object y, String testMsg) {
		newTest();
		boolean testResult = false;
		if(Operator.equals(EQUALS))	{
			testResult = equals(x, y, testMsg);
		} else if(Operator.equals(NOT_EQUALS))	{
			testResult = notEquals(x,y,testMsg);
		}

		if(testResult) {
			testPassed(testMsg);
		} else {
			testFailed(testMsg);
		}
	}

	/*
	 *Returns true if objects are not equal
	 */
	private boolean notEquals(Object x, Object y, String testmsg)	{
   		if (x != y || (x != null && !x.equals(y)))	{
			return true;
		}

		return false;
	}

	/*
	 *Returns true if objects are equal
	 */
	private boolean equals(Object x, Object y, String testmsg)	{
   		if (x == y || (x != null && x.equals(y)))	{
			return true;
		}

		return false;
	}


	/*
	 *Increments value of passed in integer
	 */
	private int recordEvent(int dataField)	{
		return ++dataField;
	}

	/*
	 *Prints Information about passed tests
	 */
	private void testPassed(String testMsg)	{
		totalTestsPassed = recordEvent(totalTestsPassed);
		currentSuite.testPassed();
		System.out.println("[" + suiteCount + ":" + currentSuite.getTestsRan() + "] "  + testMsg + ": Pass");
	}

	/*
	 *Ends testing and print summary information
	 */
	public void endTesting()	{
		System.out.printf("==> %d checks in %d Suites finished\n",totalTestsRan,suiteCount);
		System.out.printf("    %d checks Suceeded, %d failed (%.2f)\n\n",totalTestsPassed,totalTestsFailed,((double) totalTestsRan/ (double) totalTestsRan) * 100);

        if(totalTestsFailed == 0)  {
            System.out.printf("[SUCCESS]\n\n");
        } else {
            System.out.printf("[FAILURE]\n\n");
        }
	}

	/*
	 *Prints information about failed tests
	 */
	private void testFailed(String testMsg)	{
		try {
    		throw new TestFailedException(testMsg + ": FAILURE");
		} catch (TestFailedException e)	{
			System.out.println("[" + suiteCount + ":" + currentSuite.getTestsRan() + "] "  + e);
			totalTestsFailed = recordEvent(totalTestsFailed);
			currentSuite.testFailed();
		}
	}

	/*
	 *Starts a new test
	 */
	private void newTest()	{
		totalTestsRan = recordEvent(totalTestsRan);
		currentSuite.newTest();
	}

	public static void main(String[] args)	{
		Testing t = new Testing();
		t.run(t);
	}

	private void run(Testing t)	{
		t.enterSuite("Test Testing Suite 1");
		t.compare(1,"==",2,"TestFailed should print failure message");
		t.compare(1,"==",1,"TestPassed should print success message");
		t.exitSuite();
		t.enterSuite("Test Testing Suite 2");
		t.compare(1,"==",2,"TestFailed should print failure message");
		t.compare(1,"==",1,"TestPassed should print success message");
		t.exitSuite();
		t.enterSuite("Test Testing Suite 3");
		t.compare("test","==","test","TestPassed should print success message");
		t.compare("test","==","test","TestPassed should print success message");
		t.exitSuite();
		t.endTesting();
	}
}
