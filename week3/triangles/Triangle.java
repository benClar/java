/*
 *Main program for determining type of triangles based on line lengths.
 */

import java.util.Arrays;

public class Triangle  {

	public static void main( String[] args )	{
		Triangle main = new Triangle();
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST))	{
			main.testTriangle(new Testing()).endTesting();
		} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.COMPONENT_TEST))	{
			main.componentTest(new Testing()).endTesting();
		} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.NOP)) {
			main.run(args);
		}
	}

	/*
	 *Drives program
	 */
	public void run(String[] args)	{
		WhiteBoxTesting.argCountTest(3,args);
		Arrays.stream(args).forEach(s -> SafeGeneric.checkForCharacter('.', s, "Please specify only integers"));
		Arrays.stream(args).forEach(s ->SafeGeneric.checkForCharacterAtIndex(0,'0',s,"Do not use leading zeros"));
		TriangleShape t = new TriangleShape(SafeGeneric.stringToLong(args[0]), SafeGeneric.stringToLong(args[1]), SafeGeneric.stringToLong(args[2]));
		System.out.println(t.getTriangleType().getType());
	}

	/*
	 *Unit tests for Triangle class
	 */
	public Testing testTriangle(Testing t)	{
		WhiteBoxTesting.startTesting();
		String[] s = new String[3];
		t.enterSuite("Triangle Unit Tests");
		t.compare(1,"==",WhiteBoxTesting.argCountTest(3,s),"Valid argument count");
		t.compare(0,"==",WhiteBoxTesting.argCountTest(1,s),"Invalid argument count");
		t.exitSuite();
		return t;
	}

	/*
	 *Component tests for all classes in this program
	 */
	public Testing componentTest(Testing t)	{
		Triangle testT = new Triangle();
		TriangleShape testS = new TriangleShape();
		SafeGeneric sg = new SafeGeneric();
		testT.testTriangle(t);
		testS.testTriangleShape(t);
		sg.testSafeGeneric(t);
		return t;
	}
}
