public class WhiteBoxTesting	{

	protected static boolean testMode;

	public final static String UNIT_TEST = "uTest";
	public final static String COMPONENT_TEST = "uTest";
	public final static String NOP = "NOP";

	public WhiteBoxTesting()	{

	}

	/*
	 *Catching custom exceptions
	 */
    protected static int catchException(Exception e) {
        if(testMode == false)   {
            System.out.println("Fatal Error: " + e);
            System.exit(1);
        }
        return 0;
    }
	
	/*
	 *Catching Standard Exceptions and giving custom error message
	 */
    protected static int catchException(Exception e,String errMsg) {
        if(testMode == false)   {
            System.out.println("Fatal Error: " + errMsg);
            System.exit(1);
        }
        return 0;
    }

	/*
	 *Turns Test mode off: Program will exit with non zero exit code if exception is thrown
	 */
	protected static void stopTesting()	{
		testMode = false;
	}

	/*
	 *Turns Test Mode on: Exceptions will be thrown but not cause the program to exit
	 */
	protected static void startTesting() {
        testMode = true;
    }

	/*
	 *Tests String Array is expected length
	 */
	protected static int argCountTest(int expected, String[] args)	{
		try	{
			if(args.length != expected)	{
				throw new IllegalArgumentException("Please supply " + Integer.toString(expected) + " values");
			}
		} catch(IllegalArgumentException e)	{
			return catchException(e);
		}

		return 1;
	}

	/*
	 * Sets Program operating mode.
	 */
	protected static OperatingMode checkMode( String[] args )	{
		try	{
			if(args[0].equals(OperatingMode.UNIT_TEST.mode()) && args.length == 1)	{
				startTesting();
				return OperatingMode.UNIT_TEST;
			} else if(args[0].equals(OperatingMode.COMPONENT_TEST.mode()) && args.length == 1) {
				startTesting();
				return OperatingMode.COMPONENT_TEST;
			} else {
				stopTesting();
				return OperatingMode.NOP;
			}	
		} catch(ArrayIndexOutOfBoundsException e)	{
			catchException(e, "Enter cTest, uTest or supply required command line arguments values");
			return OperatingMode.ERROR_MODE;
		}
	}
}
