public class GradeObj	{

	private GradeMark gMark;
	private GradeObj next;
	private double grade;
	private int CreditPoints;
	private double weightedMark;
	private boolean cp;

	public GradeObj(double m, int cPoints) {
		cp = true;
        grade = m;
        gMark = setGrade(m);
        next = null;
		CreditPoints = cPoints;
		weightedMark = calcWMark();
    }

	public GradeObj(double m) {
		cp = false;
        grade = m;
        gMark = setGrade(m);
        next = null;
    }

	public double getWeightedMark()	{
		return weightedMark;
	}

	public int getCreditP()	{
		return CreditPoints;
	}

	private double calcWMark()	{
		try {
			if(cp == true)	{
				return grade*CreditPoints;
			} else	{
				throw new IllegalStateException("Credit Points has not been provided");
			}
		} catch(IllegalStateException e)	{
			System.out.println(e);
			System.exit(1);
		}
		return 0;
	}

	private void setCreditPoints(int c)	{
		CreditPoints = c;
	}

	public boolean getCPMode()	{
		return cp;
	}

	public GradeObj getNext()	{
		return next;
	}

	public void setNext(GradeObj n)	{
		this.next = n;
	}

	public void getGrade(GradeMark g)	{
		if(g == GradeMark.FAIL)	{
			System.out.println("Fail");
		} else if (g == GradeMark.PASS)	{
			System.out.println("Pass");
		} else if (g == GradeMark.MERIT)	{
			System.out.println("Merit");
		} else if (g == GradeMark.DISTINCTION)	{
			System.out.println("Distinction");
		}
	}
	
    public double getMark() {
		return grade;
    }

    public GradeMark setGrade(double g) {
        try {
            if(g < 0 || g > 100)    {
                throw new IllegalArgumentException("Invalid Grade");
            }
        } catch(IllegalArgumentException e) {
            System.out.println(e);
			System.exit(1);
        }

        if(g <= GradeMark.FAIL.getBoundary() )  {
            return GradeMark.FAIL;
        } else if (g <= GradeMark.PASS.getBoundary() )  {
            return GradeMark.PASS;
        } else if (g <= GradeMark.MERIT.getBoundary())  {
            return GradeMark.MERIT;
        } else  {
            return GradeMark.DISTINCTION;
        }
    }

}
