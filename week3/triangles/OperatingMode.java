/*
 *Describes current operating mode of program for use with WhiteBoxTesting class
 */
public enum OperatingMode	{

	UNIT_TEST("uTest"),
	COMPONENT_TEST("cTest"),
	NOP("NOP"),
	ERROR_MODE("ERR");

	private final String opMode;

	OperatingMode (String o)  {
        opMode = o;
    }

	public String mode()	{
		return opMode;
	}

	public boolean equals(OperatingMode m)	{
		if(this.mode().equals(m.mode()))	{
			return true;
		}

		return false;

	}

}
