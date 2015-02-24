import com.bclarke.testing.*;

public class Database  {

	public static void main( String[] args )    {
	Database main = new Database();
	Testing t = new Testing();
	if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
		main.unitTest(new Testing()).endTesting();
	} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.COMPONENT_TEST)) {
		main.componentTest(new Testing()).endTesting();
	} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.NOP)) {
		main.run(args);
	}
}

	public void run(String[] args)  {
		/*main Program*/
    }

	public Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("CLASSNAME unit tests");
		/*Unit Tests Here*/
		t.exitSuite();
		return t;
	}

	public Testing componentTest(Testing t) {
		/*Component Tests Here*/
		return t;
	}
}