package com.bclarke.testing;

public class WhiteBoxTesting	{

	private static boolean testMode;
	private static boolean verbose;

	public final static String UNIT_TEST = "uTest";
	public final static String COMPONENT_TEST = "uTest";
	public final static String NOP = "NOP";

	public WhiteBoxTesting()	{

	}

	/*
	 *Catching non fatal custom exceptions
	 */
    public static int catchException(Exception e) {
        if(testMode == false || verbose == true)   {
            System.out.println("Error: " + " " + e.getMessage());
        }
        return 0;
    }

    /*
     *Catching Fatal Exceptions
     */

    public static int catchFatalException(Exception e,String errMsg) {
        if(testMode == false)   {
            System.out.println("Fatal Error: " + errMsg + " " + e.getMessage());
            System.exit(1);
        } else if (verbose)	{
			System.out.println("Fatal Error: " + errMsg + " " + e.getMessage());

        }
        return 0;
    } 
	
	/*
	 *Catching nonfatal Standard Exceptions and giving custom error message
	 */
    public static int catchException(Exception e,String errMsg) {
        if(testMode == false || verbose == true)   {
            System.out.println("Error: " + errMsg + " " + e.getMessage());
        }
        return 0;
    }

	/*
	 *Turns Test mode off: Program will exit with non zero exit code if exception is thrown
	 */
	public static void stopTesting()	{
		testMode = false;
	}
	/*
	 *Returns true if program is currently in test mode
	 */
	public static boolean getMode()	{
		return testMode;
	}

	public static void Verbose()	{
		verbose  = true;
	}

	public static void noVerbose()	{
		verbose = false;
	}

	/*
	 *Turns Test Mode on: Exceptions will be thrown but not cause the program to exit
	 */
	public static void startTesting() {
        testMode = true;
    }

	/*
	 *Tests String Array is expected length
	 */
	public static int argCountTest(int expected, String[] args)	{
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
	public static OperatingMode checkMode( String[] args )	{
		try	{
			if(args[0].equals(OperatingMode.UNIT_TEST.mode()) && args.length >= 1)	{
				startTesting();
				if(args.length > 1)	{
					if(args[1].equals("-v"))	{
						Verbose();
					}
				}
				return OperatingMode.UNIT_TEST;
			} else if(args[0].equals(OperatingMode.COMPONENT_TEST.mode()) && args.length >= 1) {
				startTesting();
				if(args.length > 1)	{

					if(args[1].equals("-v"))	{

						Verbose();
					}
				}
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
