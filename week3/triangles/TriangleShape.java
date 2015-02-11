public class TriangleShape extends WhiteBoxTesting	{

	private int sideOne;
	private int sideTwo;
	private int sideThree;
	private int nSides;
	private TriangleType tType;

	private final int SIDE_UPPER_LIMIT = (int) Math.pow(2,31) - 1;
	private final int SIDE_LOWER_LIMIT = 0;
	private final int NUM_OF_SIDES = 3;

	public TriangleShape(int sOne, int sTwo, int sThree)	{
		sideOne = boundCheck(sOne);
		sideTwo = boundCheck(sTwo);
		sideThree = boundCheck(sThree);
		nSides = NUM_OF_SIDES;
		tType = calculateTriangleType();
		WhiteBoxTesting.stopTesting();
	}

	public TriangleShape()	{
		/*Empty Constructor*/
	}

	private TriangleType calculateTriangleType()	{
		if(checkEquilateral())	{
			return TriangleType.EQUILATERAL;
		} else if(checkIsosceles())	{
			return TriangleType.ISOSCELES;
		} else if(checkRightAngled())	{
			return TriangleType.RIGHT_ANGLE;
		}
		return TriangleType.SCALENE;
	}

	public boolean compare(TriangleShape t)	{
		if (t.getTriangleType() == this.getTriangleType())	{
			return true;
		} else {
			return false;
		}
	}

	public boolean compare(TriangleType t)	{
		if (t == this.getTriangleType())	{
			return true;
		} else {
			return false;
		}
	}

	private boolean checkEquilateral()	{
		if(sideOne == sideTwo && sideTwo == sideThree)	{
			return true;
		}
		return false;
	}

	private boolean checkIsosceles()	{
		if(sideOne == sideTwo || sideTwo == sideThree || sideOne == sideThree)	{
			return true;
		}
		return false;
	}

	public TriangleType getTriangleType()	{
		return tType;
	}

	private boolean checkRightAngled()	{
		if(Math.pow(sideOne,2) + Math.pow(sideTwo,2) == Math.pow(sideThree,2)) {
			return true;
		} else if(Math.pow(sideOne,2) + Math.pow(sideThree,2) == Math.pow(sideTwo,2))	{
			return true;
		} else if(Math.pow(sideThree,2) + Math.pow(sideTwo,2) == Math.pow(sideOne,2))	{
			return true;
		}
		return false;
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

	public static void main(String[] args)	{
		TriangleShape triangle = new TriangleShape();
		if(WhiteBoxTesting.checkMode(args))	{
			triangle.testTriangleShape(args,triangle).endTesting();;
		}
	}

	public Testing testTriangleShape(String[] args, TriangleShape triangle)	{
		Testing t = new Testing();
		WhiteBoxTesting.startTesting();
		t.enterSuite("TriangleShape Unit Tests");
		t.compare(0,"==",triangle.catchException(new IllegalArgumentException()),"Exception catch returns 0 in test mode");
		t.compare(0,"==",triangle.boundCheck((int) Math.pow(2,31)),"Invalid upper bounds of side length");
		t.compare(0,"==",triangle.boundCheck(0),"Invalid lower bounds of side length");
		t.compare(0,"!=",triangle.boundCheck((int) Math.pow(2,31)-1),"Valid upper bounds of side length");
		t.exitSuite();
		return t;
	}
}
