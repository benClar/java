public class Interpreter	{

	private StringToParse currentLine;
	private Database currentDatabase;
	private RelationStack currentRelStack;
	private DataOutput interpreterOut;
	private final String LOAD_TABLE = "table";
	private final String KEEP_COLUMNS = "column";
	public Interpreter(Database d, RelationStack rs)	{
		currentDatabase = d;
		rs = currentRelStack;
		interpreterOut - new DataOutput();
	}

	public void parse(String line)	{
	 	currentLine.addLine(line);
	 	switch(currentLine.getNextTokenBy(' ')){
	 		case LOAD_TABLE:
	 			parseLoadTable();
	 			break;
	 		case KEEP_COLUMNS:
	 			parseKeepColumns();
	 		default:
	 			break;
	 	}
	}

	private void parseKeepColumns()	{
		String cNames = parseColumnNames();

	}

	private String[] parseColumnNames() {
		ArrayList<String> cNames = new ArrayList<String>();
		while(currentLine.hasNext())	{
			cNames.add(currentLine.getNextTokenBy(' '));
		}
		return cName.toArray();

	}

	private void parseLoadTable()	{
		try	{
			interpreterOut.printTableName(currentRelStack.push(currentDatabase.getTable(currentLine.getNextTokenBy(' ')));
		} catch (NullPointerException e)	{
			WhiteBoxTesting.catchException(e,"Table does not exist");
		}
	}

	/*----------Testing----------*/

	public static void main( String[] args )    {
	 
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			Interpreter.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Interpreter Unit Tests");
		/*Unit Tests Here*/
		t.exitSuite();
		return t;
	}

}
