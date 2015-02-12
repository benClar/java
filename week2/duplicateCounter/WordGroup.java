import java.util.ArrayList;

public class WordGroup	{

	private ArrayList <WordCount> Group;

	public WordGroup()	{
		 Group = new ArrayList <WordCount>();
	}

	public int presenceCheck(String word)	{
		int i;
		for(i = 0; i < Group.size(); i++)	{
			if(Group.get(i).getWord().equals(word))	{
				return 1;
			}
		}
		return 0;
	}

	public int getWordIndex(String word)	{
		int i;
		for(i = 0; i < Group.size(); i++)	{
			if(Group.get(i).getWord().equals(word))	{
				return i;
			}
		}
		System.err.println("Tried to get non-existent word");
		return 0;
	}

	public void addWord(WordCount nWord)	{
		Group.add(nWord);
	}

	public void printGroup()	{
		int i;

		for(i = 0; i < Group.size(); i++)	{
			System.out.printf("Word: %s\n",Group.get(i).getWord());
			System.out.printf("Count: %d\n",Group.get(i).getCount());	
		}
	}

	public WordCount getStoredWord(int i)	{
		return Group.get(i);
	}

}
