public class WhiteBoxTesting	{

	protected static boolean testMode;

	private final static String START_TESTING = "test";

	public WhiteBoxTesting()	{

	}

	/*
	 *Catching custom exceptions
	 */
    protected static int catchException(Exception e) {
        if(testMode == false)   {
            System.out.println("Fatal Error: " + e + '\n');
            System.exit(1);
        }
        return 0;
    }
	
	/*
	 *Catching Standard Exceptions and giving custom error message
	 */
    protected static int catchException(Exception e,String errMsg) {
        if(testMode == false)   {
            System.out.println("Fatal Error: " + errMsg + '\n');
            System.exit(1);
        }
        return 0;
    }

	protected static void stopTesting()	{
		testMode = false;
	}

	protected static void startTesting() {
        testMode = true;
    }

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

	protected static boolean checkMode( String[] args )	{
		try	{
			if(args[0].equals(START_TESTING) && args.length == 1)	{
				startTesting();
				return true;
			} else {
				stopTesting();
				return false;
			}	
		} catch(ArrayIndexOutOfBoundsException e)	{
			return ((catchException(e, "Enter test or supply 3 integer values") == 1) ? true : false);
		}
	}
}
