import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadTextFile	{

	private Scanner input;

	public void openFile(String file)	{
        try {
            input = new Scanner(new File(file));
        }
        catch(FileNotFoundException e)  {
            System.err.println("Error Opening File");
            System.exit(1);
        }
	}

	public String readWord()	{
		if(input.hasNext())	{
			return input.next();
		}

		return null;
	}


}
