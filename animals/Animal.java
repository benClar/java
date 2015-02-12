public class Animal	{

	private int nLegs;
	public int x;
	public int y;

	public Animal( int n)	{
		nLegs = n;
		x = 0;
		y = 0;
	}

	public void moveForward()	{
			if(y >= 10)	{
				y =- 10;	
			} else	{
				throw new IllegalArgumentException(
								"Cant move forward any further");
			}
	}

	public void moveBack()	{
		y =+ 10;	
	}

	public void printPosition()	{
		System.out.printf("x: %d\n y: %d \n",x,y);
	}
}
