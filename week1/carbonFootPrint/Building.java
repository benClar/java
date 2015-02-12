public class Building implements Footprint	{

	private double gas;	//!Multiply by 11.7 to get Co2
	private double electric; //! Multiply by 2.9 to get Co2
	private double footprint;

	public Building(double g, double e)	{
		gas = g;
		electric = e;		
		footprint = calcFootprint();
	}

	public double getGas()	{
		return gas;
	}

	public double getElectric()	{
		return electric;
	}
	
	private double calcFootprint()	{
		return (gas * 11.7) + (electric * 2.9);
	}

	@Override
	public double getFootprint()	{
		return footprint;
	}

	public static Building createBuilding(int g, int e)	{
		Building b = new Building(g,e);
		return b;
	}
}
