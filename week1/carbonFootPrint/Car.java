import java.util.ArrayList;

public class Car implements Footprint	{

	private double engineSize;
	private double mileage;
	private double footprint;
	private ArrayList <City> cList;

	public Car(double eSize, City nCity)	{
		engineSize = eSize;
		mileage = 0;
		addDestination(nCity);
	}

	public double getEngineSize()	{
		return engineSize;
	}
	public double getMileage()	{
		return mileage;
	}

	public void addDestination(City nCity)	{
		mileage = mileage +  Math.sqrt((nCity.getX()- cList.get(cList.size()-1).getX()) + (nCity.getY() - cList.get(cList.size()-1).getY()));
		cList.add(nCity);
		footprint = calcFootprint();
	}

	private double calcFootprint()	{
		return (mileage * engineSize) * 100;
	}
	
	@Override
	public double getFootprint()	{
		return footprint;
	}

}
