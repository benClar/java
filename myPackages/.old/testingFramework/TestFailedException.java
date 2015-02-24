package com.bclarke.testing;

/*
 *Custom Exception thrown when test fails 
 */
class TestFailedException extends Exception
{
      public TestFailedException() {}

      public TestFailedException(String errorMsg)
      {
         super(errorMsg);
      }
 }
