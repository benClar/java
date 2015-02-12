public class City	{

	private int X;
	private int Y;

	public City(int x, int y)	{
		X = x;
		Y = y;
	}

	public int getX()	{
		return X;
	}

	public int getY()	{
		return Y;
	}

	public static City createCity(int x, int y)	{
		City c = new City(x,y);	
		return c;
	}
}
