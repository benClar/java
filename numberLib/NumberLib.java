public class NumberLib	{

	private int number;
	NumberLib next;

	public NumberLib(int n)	{
		if(n >= 0)	{
			number = n;
		} else	{
			throw new IllegalArgumentException(	
						"number must be positive");
		}
		next = null;	
	}

	public void setNum(int newNum)	{
		number = newNum;
	}	

	public void incremNum()	{
		number++;
	}

	public void setNext(NumberLib nNumLib)	{
		next = nNumLib;
	}

	public NumberLib getNext()	{
		return next;
	}

	public int getNum()	{
		return number;
	}

	public static int getMax(int a, int b)	{
		if(a > b)	{ 
			return a;
		} else {
			return b;
		}

	}

}
