public class Fish extends Animal	{

	private Scales scales;

	public Fish( int n, Scales s )	{
		super( n );
		scales = s;
	}

	@Override
	public void moveForward()	{
			if(y >= 5)	{
				y =- 5;	
			} else	{
				throw new IllegalArgumentException(
								"Cant move forward any further");
			}
	}

	@Override
	public void moveBack()	{
		y =+ 5;	
	}
}
