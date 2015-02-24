package com.bclarke.testing;

public abstract class TestMode
{
  protected boolean testMode;

  protected int catchException(Exception paramException)
  {
    if (!this.testMode) {
      System.out.println(paramException);
      System.exit(1);
    }
    return 0;
  }

  protected void stopTesting() {
    this.testMode = false;
  }

  protected void startTesting() {
    this.testMode = true;
  }
}
