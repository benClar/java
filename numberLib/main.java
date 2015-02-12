public class main	{

	public static void main( String[] args )	{

	NumberHead head = new NumberHead();	
	addNode(-1,head);
	addNode(10,head);
	head.printQueue();

	}

	public static void addNode(int number, NumberHead head)	{
	try	{	
		TestNumber nLib = new TestNumber( number );

		if(head.getStart() == null)	{
			head.setStart(nLib);
			head.setEnd(nLib);
		} else {
			head.getEnd().setNext(nLib);
			head.setEnd(head.getEnd().getNext());	
		}
	}
	catch	(IllegalArgumentException e)	
	{
		System.out.printf("Exception: %s\n",e.getMessage());	
	}

	}

}
