public enum TriangleType	{

	RIGHT_ANGLE("Right-angled"),
	EQUILATERAL("Equilateral"),
	ISOSCELES("Isosceles"),
	SCALENE("Scalene");

	private final String triType;

	TriangleType(String t)	{
		triType = t;
	}
	public String getType()	{
		return triType;
	}
}
