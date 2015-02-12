public class NumberHead  {

	private NumberLib start;
	private NumberLib end;
	private int nNumbers;

	public NumberHead()	{
		start = null;
		end = null;
		nNumbers = 0;
	}	

	public NumberLib getStart()	{
		return start;
	}	
	public NumberLib getEnd()	{
		return end;
	}	

	public void setEnd(NumberLib nEnd)	{
		end = nEnd;
	}

	public void setStart(NumberLib nStart)	{
		start = nStart;
	}

	public void printQueue()	{
		NumberLib curr = start;	
		while(curr != null)	{
			System.out.printf("%d\n",curr.getNum());
			curr = curr.getNext();
		}

	}
}
