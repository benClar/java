public class Triangle extends WhiteBoxTesting	{

	public static void main( String[] args )	{
		Triangle main = new Triangle();
		if(main.checkMode(args))	{
			main.testTriangle(args);
		} else {
			main.run(args);
		}
	}

	public void run(String[] args)	{
		argCountTest(3,args);
		TriangleShape t = new TriangleShape(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
	}

	public void testTriangle(String[] args)	{
		Testing t = new Testing();
		startTesting();
		t.enterSuite("Triangle: main program");
		t.compare(1,"==",argCountTest(1,args),"Valid argument count");
		t.compare(0,"==",argCountTest(3,args),"Invalid argument count");
		t.exitSuite();
		t.endTesting();
	}

}
