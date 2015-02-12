public enum GradeMark	{

	FAIL(49),
	PASS(59),
	MERIT(69),
	DISTINCTION(100);

	private final int upperBound;

	GradeMark(int n)	{
		upperBound = n;
	}

	public int getBoundary()	{
		return upperBound;
	}


}
