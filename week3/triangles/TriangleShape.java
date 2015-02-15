public class TriangleShape {

	private long sideOne;	//! Side one of triangle
	private long sideTwo;	//! Side two of triangle 
	private long sideThree;	//! Side three of triangle;
	private int nSides; 	//!Number of sides of shapre
	private TriangleType tType;	//!Type of triangle

	private final long SIDE_UPPER_LIMIT =  (1 << 31) - 1;	//!Triangle sides cannot be longer than this
	private final long SIDE_LOWER_LIMIT = 0;	//!Triangle sides cannot be shorter than this
	private final int NUM_OF_SIDES = 3;		//!Number of sides of this shape

	public TriangleShape(long sOne, long sTwo, long sThree)	{
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
	
	/*
	 *Drives calculation of triangle type
	 */
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

	/*
	 *For comparing triangle shapes
	 */
	public boolean equals(TriangleShape t)	{
		if (t.getTriangleType() == this.getTriangleType())	{
			return true;
		} else {
			return false;
		}
	}

	/*
	 *Returns true is triangle is Equilateral
	 */
	private boolean checkEquilateral()	{
		if(sideOne == sideTwo && sideTwo == sideThree)	{
			return true;
		}
		return false;
	}

	/*
	 *Returns true is triangle is Isosceles
	 */
	private boolean checkIsosceles()	{
		if(sideOne == sideTwo || sideTwo == sideThree || sideOne == sideThree)	{
			return true;
		}
		return false;
	}

	/*
	 * Accessor for triangle type
	 */
	public TriangleType getTriangleType()	{
		return tType;
	}

	/*
	 *Returns true if triangle is righ angled
	 */
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

	/*
	 *Checking value is within triangle side bounds
	 */
	private long boundCheck(long n)	{
		try {
			if(n <= SIDE_LOWER_LIMIT || n > SIDE_UPPER_LIMIT)	{
				throw new IllegalArgumentException("Side length provided is out of bounds");
			}
		} catch (IllegalArgumentException e)	{
			return WhiteBoxTesting.catchException(e);
		}
		return n;
	}

	public static void main(String[] args)	{
		TriangleShape triangle = new TriangleShape();
        if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			triangle.testTriangleShape(new Testing()).endTesting();;
        }
	}

	/*
	 *Unit testing for TriangleShape class
	 */
	public Testing testTriangleShape(Testing t)	{
		TriangleShape tri = new TriangleShape();
		WhiteBoxTesting.startTesting();
		t.enterSuite("TriangleShape Unit Tests");
		t.compare(0,"==",WhiteBoxTesting.catchException(new IllegalArgumentException()),"Exception catch returns 0 in test mode");
		t.compare(0L,"==",tri.boundCheck((long) Math.pow(2,31)),"Invalid upper bounds of side length");
		t.compare(0L,"==",tri.boundCheck(0),"Invalid lower bounds of side length");
		t.compare(0L,"!=",tri.boundCheck((long) Math.pow(2,31)-1),"Valid upper bounds of side length");
		TriangleShape rA = new TriangleShape(3,4,5);
		t.compare(TriangleType.RIGHT_ANGLE,"==",rA.getTriangleType(),"Right Angled Triangle");
		TriangleShape eq = new TriangleShape(4,4,4);
		t.compare(TriangleType.EQUILATERAL,"==",eq.getTriangleType(),"Equilateral Triangle");
		TriangleShape iso = new TriangleShape(3,4,4);
		t.compare(TriangleType.ISOSCELES,"==",iso.getTriangleType(),"Isosceles Triangle");
		TriangleShape sca = new TriangleShape(4,5,6);
		t.compare(TriangleType.SCALENE,"==",sca.getTriangleType(),"Scalene Triangle");
		t.exitSuite();
		return t;
	}
}
