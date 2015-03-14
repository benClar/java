@SuppressWarnings("serial")
public class SyntaxErrorException extends RuntimeException
{

	public SyntaxErrorException() {}

	public SyntaxErrorException(String errorMsg)
	{
		super(errorMsg);
	}
 }