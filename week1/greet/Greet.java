public class Greet	{

	public static void main( String[] args )	{
		Greeting g;
		if(args.length > 0)	{
			g = createG(args[0]);	
		} else 	{
			g = createG();	
		}
		g.printTarget();
	}


	public static Greeting createG(String t)	{
		Greeting g = new Greeting(t);
		return g;
	}

	public static Greeting createG()	{
		Greeting g = new Greeting();
		return g;
	}
}
