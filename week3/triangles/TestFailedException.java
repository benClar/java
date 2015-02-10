class TestFailedException extends Exception
{
      public TestFailedException() {}

      public TestFailedException(String errorMsg)
      {
         super(errorMsg);
      }
 }
