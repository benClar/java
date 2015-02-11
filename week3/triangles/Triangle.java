public class Triangle  {

	public static void main( String[] args )	{
		Triangle main = new Triangle();
		if(WhiteBoxTesting.checkMode(args))	{
			main.testTriangle(args).endTesting();
		} else {
			main.run(args);
		}
	}

	public void run(String[] args)	{
		WhiteBoxTesting.argCountTest(3,args);
		TriangleShape t = new TriangleShape(SafeGeneric.stringToInt(args[0]), SafeGeneric.stringToInt(args[1]), SafeGeneric.stringToInt(args[2]));
		System.out.println(t.getTriangleType().getType());
	}

	public Testing testTriangle(String[] args)	{
		Testing t = new Testing();
		WhiteBoxTesting.startTesting();
		t.enterSuite("Triangle Unit Tests");
		t.compare(1,"==",argCountTest(1,args),"Valid argument count");
		t.compare(0,"==",argCountTest(3,args),"Invalid argument count");
		t.exitSuite();
		return t;
	}

}
