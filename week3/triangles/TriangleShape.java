public class TriangleShape extends WhiteBoxTesting	{

	private int sideOne;
	private int sideTwo;
	private int sideThree;
	private int nSides;
	TriangleType tType;

	private final int SIDE_UPPER_LIMIT = (int) Math.pow(2,31) - 1;
	private final int SIDE_LOWER_LIMIT = 0;
	private final int NUM_OF_SIDES = 3;

	public TriangleShape(int sOne, int sTwo, int sThree)	{
		sideOne = boundCheck(sOne);
		sideTwo = boundCheck(sTwo);
		sideThree = boundCheck(sThree);
		nSides = NUM_OF_SIDES;
		stopTesting();
	}

	public TriangleShape()	{
		/*Empty Constructor*/
	}

	private int boundCheck(int n)	{
		try {
			if(n <= SIDE_LOWER_LIMIT || n > SIDE_UPPER_LIMIT)	{
				throw new IllegalArgumentException("Side length provided is out of bounds");
			}
		} catch (IllegalArgumentException e)	{
			return catchException(e);
		}
		return n;
	}

	/*private int catchException(Exception e)	{
		if(testMode == false)	{
			System.out.println(e);
			System.exit(1);
		}
			return 0;
	}*/

	public static void main(String[] args)	{
		TriangleShape triangle = new TriangleShape();
		triangle.testTriangleShape(args,triangle);
	}

	public void testTriangleShape(String[] args, TriangleShape triangle)	{
		Testing t = new Testing();
		triangle.startTesting();
		t.enterSuite("TriangleShape");
		t.compare(0,"==",triangle.catchException(new IllegalArgumentException()),"Exception catch returns 0 in test mode");
		t.compare(0,"==",triangle.boundCheck((int) Math.pow(2,31)),"Invalid upper bounds of side length");
		t.compare(0,"==",triangle.boundCheck(0),"Invalid lower bounds of side length");
		t.compare(0,"!=",triangle.boundCheck((int) Math.pow(2,31)-1),"Valid upper bounds of side length");
		t.exitSuite();
		t.endTesting();
	}
}
