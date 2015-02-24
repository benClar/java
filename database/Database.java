import com.bclarke.testing.*;
import com.bclarke.general.*;

public class Database  {

	public static void main( String[] args )    {
	
	Testing t = new Testing();

	if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
		Database.unitTest(new Testing()).endTesting();
	} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.COMPONENT_TEST)) {
		Database.componentTest(new Testing()).endTesting();
	} else if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.NOP)) {
		Database main = new Database();
		main.run(args);
	}
}

	public void run(String[] args)  {
		/*main Program*/
    }

	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Database unit tests");
		/*Unit Tests Here*/
		t.exitSuite();
		return t;
	}

	public static Testing componentTest(Testing t) {
		Field.unitTest(t);
		Record.unitTest(t);
		return t;
	}
}