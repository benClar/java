public class StackNode< T >	{

	T data;
	StackNode < T > prevNode;

	StackNode( T object )	{

		data = object;
		prevNode = null;

	}

	public void linkNode(StackNode < T > newNode)	{
		this.prevNode  = newNode;
	}

	public StackNode < T > getPrevNode()	{
		return this.prevNode;
	}

	public T getData()	{
		return this.data;
	}
}
