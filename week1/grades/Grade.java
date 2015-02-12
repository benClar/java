import java.io.PrintStream;

public class Grade
{
  public static void main(String[] paramArrayOfString)
  {
    Grade localGrade = new Grade();
    localGrade.run(paramArrayOfString);
  }

  void run(String[] paramArrayOfString) {
    GradeListHead localGradeListHead = new GradeListHead();

    for (int j = 0; j < paramArrayOfString.length; j++) {
      try
      {
        int i;
        if ((i = testForCharacter(':', paramArrayOfString[j])) > 0) {
          localGradeListHead.checkNodeType(true, "Include Credit Points");
          localGradeListHead.addGrade(new GradeObj(Double.parseDouble(paramArrayOfString[j].substring(i + 1, paramArrayOfString[j].length())), Integer.parseInt(paramArrayOfString[j].substring(0, i))));
        } else {
          localGradeListHead.checkNodeType(false, "Don't include Credit Points");
          localGradeListHead.addGrade(new GradeObj(Double.parseDouble(paramArrayOfString[j])));
        }
        //printGrade(localGradeListHead.getEnd());
      } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
        System.out.println("Please enter a grade");
        System.exit(1);
      } catch (NumberFormatException localNumberFormatException) {
        System.out.println("Please enter a number");
        System.exit(1);
      } catch (IllegalArgumentException e)	{
        System.out.println(e);
        System.exit(1);
	  }
    }
	printResult(localGradeListHead);
    //getAverage(localGradeListHead);
  }

  public void checkDecimal(String s)	{
	int point = testForCharacter('.',s);
   	if(s.length() - point > 1)	{
		throw new IllegalArgumentException("Only one decimal place allowed");
	}	
  }

  public void printGrade(GradeObj paramGradeObj) {
    double d = paramGradeObj.getMark();
    if (d == Math.ceil(d))
      System.out.printf("%.0f\t", new Object[] { Double.valueOf(d) });
    else {
      System.out.printf("%.1f\t", new Object[] { Double.valueOf(d) });
    }
    //paramGradeObj.getGrade();
  }

  public void getAverage(GradeListHead paramGradeListHead) {
    System.out.println("Average:  " + String.valueOf(paramGradeListHead.getTotal() / paramGradeListHead.length()));
    System.out.println("Weighted Average:  " + paramGradeListHead.calcWeightedAv());
  }

  public void printResult(GradeListHead gL)	{
	double average = gL.getTotal() / gL.length();	
	double wAverage = gL.calcWeightedAv();

	if(gL.getEnd().getCPMode() == false)	{
		gL.getEnd().getGrade(gL.getEnd().setGrade(average));
	} else {
		gL.getEnd().getGrade(gL.getEnd().setGrade(wAverage));	
	}
  }

  public static int testForCharacter(char paramChar, String paramString)
  {
    for (int i = 0; i < paramString.length(); i++) {
      if (paramString.charAt(i) == paramChar) {
        return i;
      }
    }
    return 0;
  }
}
