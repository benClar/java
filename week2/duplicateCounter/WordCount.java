public class WordCount	{

	String word;
	int count;

	public WordCount(String w)	{
		count = 1;
		word = w;
	}

	public void increaseCount()	{
		count++;
	}

	public String getWord()	{
		return word;
	}

	public int getCount()	{
		return count;
	}

}
