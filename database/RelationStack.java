import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.Stack;
public class RelationStack  {

	private Stack<Table> relStack;

	public RelationStack()	{
		relStack = new Stack<Table>();
	}

	public Table push(Table rel)	{
		try	{
			if( rel == null)	{
				throw new Exception();
			}
			return relStack.push(rel);
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Null table placed on relation stack");
			return null;
		}
	}

	public Table pop()	{
		try{
			if(!relStack.empty())	{
				return relStack.pop();
			} else	{
				throw new Exception();
			}
		}	catch (Exception e)	{
			WhiteBoxTesting.catchException(e,"No Tables in relation Stack");
			return null;
		}
	}


	public Table peek()	{
		return relStack.peek();
	}

/*----------Testing----------*/

	public static void main( String[] args )    {
	 
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			RelationStack.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		RelationStack.unitTest_usingRelStack(t);

		return t;
	}

	public static Testing unitTest_usingRelStack(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("RelationStack Unit Tests: Using Relation Stack");
		String[] cNames = new String[]{"ID","Vehicle","Manufacturer"};
		Field[] f = new Field[3];
		FieldDataType[] dtype = new FieldDataType[3];
		FieldDataType[] ktype = new FieldDataType[3];

		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
			ktype[i] = FieldDataType.NONKEY;
		}
		ktype[0] = FieldDataType.PKEY;

		Table tab=new Table(cNames,dtype,ktype,"testTable");
		RelationStack rs = new RelationStack();
		t.compare(tab,"==",rs.push(tab),"Pushing Table to relation stack");
		t.compare("testTable","==",rs.peek().getTableName(),"Table peeked at top of stack is called testTable");
		t.compare("testTable","==",rs.pop().getTableName(),"Table popped from top of stack is called testTable");
		t.compare(null,"==",rs.pop(),"Stack is empty: invalid pop");		
		t.exitSuite();
		return t;
	}
}

