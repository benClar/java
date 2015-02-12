public class Main	{

	public static void main( String[] args )	{
		Main m = new Main();
		m.run(args);
	}

	public void run(String[] args)	{
		Footprint[] ftprint = new Footprint[3];
		ftprint[0] = new Bicycle(2000,1);
		ftprint[1] = new Car(3.2,new City(10,20));
		ftprint[2] = new Building(400,200);
		System.out.printf("%.2f\n",ftprint[0].getFootprint());
		System.out.printf("%.2f\n",ftprint[1].getFootprint());
		System.out.printf("%.2f\n",ftprint[2].getFootprint());
	}

}
