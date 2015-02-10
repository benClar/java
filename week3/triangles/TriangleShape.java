public class TriangleShape	{

	private int sideOne;
	private int sideTwo;
	private int sideThree;
	TriangleType tType;

	boolean testMode;

	final int SIDE_UPPER_LIMIT = (int) Math.pow(2,31) - 1;
	final int SIDE_LOWER_LIMIT = 0;


	public TriangleShape(int sOne, int sTwo, int sThree)	{
		sideOne = boundCheck(sOne);
		sideTwo = boundCheck(sTwo);
		sideThree = boundCheck(sThree);
		testMode = false;
	}

	public TriangleShape()	{

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

	private int catchException(Exception e)	{
		if(testMode == false)	{
			System.out.println(e);
			System.exit(1);
		}
			return 0;
	}

	private void startTesting()	{
		testMode = true;
	}

	//private static TriangleShape triangle;

	public static void main(String[] args)	{
		TriangleShape triangle = new TriangleShape();
		triangle.startTesting();
	}

}
