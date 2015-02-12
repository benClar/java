public class Dog extends Animal	{

	private int nLegs;
	public int x;
	public int y;
	Fur fur;

	public Dog( int n, Fur f)	{
		super( n ); 
		fur = f;
	}

	@Override
	public void moveForward()	{
			if(y >= 30)	{
				y =- 30;	
			} else	{
				throw new IllegalArgumentException(
								"Cant move forward any further");
			}
	}
	
	@Override
	public void moveBack()	{
		y =+ 30;	
	}

}
