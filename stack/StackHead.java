public class StackHead < T > {

	StackNode < T > start;

	public StackHead()	{

		start = null;

	}

	public void push(T object)	{
		StackNode < T > nNode = new StackNode< T >( object );
		nNode.linkNode(nNode);
		start = nNode;
	}

	public StackNode  < T > pop()	{
			StackNode < T > cNode =  start;
			start = start.getPrevNode();
			return cNode;
	}
}
