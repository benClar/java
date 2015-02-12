public class Greeting	{

	private String target;

	public Greeting( String t )	{
		target = t;	
	}

	public Greeting()	{
		target = null;	
	}

	public void printTarget()	{
		if(target == null)	{
			System.out.println("Hello");
		} else	{
			System.out.println("Hello " + target);
		}
	}
}
