public class GradeListHead	{

	private int listLength;
	private GradeObj end;
	private GradeObj start;
	private double total;
	private double creditTotal;

	public GradeListHead()	{
		listLength = 0;
		end = null;
		start = null;
		total = 0;
	}

	public void addGrade(GradeObj nG)	{
		creditTotal = creditTotal + nG.getCreditP();
		total = total + nG.getMark();
		if(listLength == 0)	{
			setEnd(nG);
			setStart(nG);
		} else {
			getEnd().setNext(nG);
			setEnd(nG);
		}
		listLength++;
	}

	public double getTotal()	{
		return total;
	}

	public int length()	{
		try {
			if(listLength <= 0)	{
				throw new IllegalArgumentException("List Empty");
			}
		} catch ( IllegalArgumentException e ) {
			System.out.println(e);
			System.exit(1);
		}
		return listLength;
	}

	public double calcWeightedAv()	{
		GradeObj temp;
		double WAv;
		for(temp = start, WAv = 0; temp != null; temp = temp.getNext())	{
				WAv += (temp.getWeightedMark()/creditTotal);
		}
		return WAv;
	}

	public void checkNodeType(boolean expectedType, String errMsg)	{
		GradeObj temp;
		for(temp = start; temp != null; temp = temp.getNext())	{
			try	{
				if(temp.getCPMode() != expectedType)	{
					throw new IllegalStateException(errMsg);
				}
			} catch ( IllegalStateException e)	{
				System.out.println(e.getMessage());	
				System.exit(1);
			}
		}	
	}



	public GradeObj getEnd()	{
		return end;
	}

	private void setEnd(GradeObj nG)	{
		end = nG;
	}

	private void setStart(GradeObj nG)	{
		start = nG;
	}

	private void increaseGCount()	{
		listLength++;
	}

}
