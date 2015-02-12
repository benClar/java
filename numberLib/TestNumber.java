public class TestNumber extends NumberLib	{

	private int divisor;

	public TestNumber(int n)	{

		super(n); 
		divisor = 2;
	}

    public int isPrime()    {
        if(divisor == this.getNum())   {
            return 1;
        }

        if((this.getNum()%divisor) == 0)   {
            return 0;
        }
        divisor++;
        if(isPrime() == 1)  {
            divisor = 2;
            return 1;
        } else {
            return 0;
        }
    }

}
