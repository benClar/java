public class Bicycle implements Footprint	{

	private int manCost;  //! 530 pounds of CO2 per 1000
	private int age;
	private double footprint;

	public Bicycle(int mCost, int a)	{
		manCost = mCost;
		age = a;
		footprint = calcFootprint();
	}

	public int getCost()	{
		return manCost;
	}

	public int getAge()	{
		return age;
	}

	private double calcFootprint()	{
		return ((double) manCost / 530) - age * 300;
	}

	@Override
    public double getFootprint()    {
        return footprint;
    }

	public static Bicycle createBicycle(int c, int a)	{
		Bicycle b = new Bicycle(c,a);
		return b;
	}
}
