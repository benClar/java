import java.io.File;

public class Main	{

	public static void main(String[] args)	{
		Main m = new Main();
		m.run(args);
	}

	public void run(String[] args)	{
		ReadTextFile f = new ReadTextFile();
		WordGroup wg = new WordGroup();
		String tWord;
		try {
			f.openFile(args[0]);
		} catch (ArrayIndexOutOfBoundsException e)	{
			System.err.println("Please supply filename");
			System.exit(1);
		}

		while((tWord = f.readWord()) != null)	{
			if(wg.presenceCheck(tWord) == 0)	{
				wg.addWord(new WordCount(tWord));	
			} else	{
				wg.getStoredWord(wg.getWordIndex(tWord)).increaseCount();
			}	
		}

		wg.printGroup();
	}


}
