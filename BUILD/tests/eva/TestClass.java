/**
   @file           testEvaLine.java

   Created by AXAulet on 21.02.2005 20:38
*/


/**
 @class   test
 @author  Alejandro Xalabarder

 */
public class TestClass
{
   public static boolean STRESS_TESTS = true;

   private String className = "";
   private String methodName = "";
   private String pointName = "";
   private int points = 0;

   public boolean run ()
   {
      System.err.println ("ERROR: Test has to implement boolean ::run ()");
      return false;
   }

   public void TESTCLASS (String claName)
   {
      className = claName;
      methodName = "";
      pointName = "";
      points = 0;
      System.out.println ("\n\n****** <<" + className + ">> ****");
   }

   public void METHOD (String methName)
   {
      methodName = methName;
      pointName = "";
      points = 0;
      System.out.print ("\n[[test " + methodName + "]] ");
   }

   public void POINT (String strPoint)
   {
      System.out.print (" ");
      pointName = strPoint;
      points ++;
      if (points >= 10)
      {
         System.out.println ("");
         points = 0;
      }
   }

   public void PASS (boolean verdad)
   {
      if (verdad)
      {
         System.out.print (".");
         return;
      }

      System.err.println ("FAIL !!!!");
      System.err.println ("TEST NOT PASSED ON: " + className + "::" + methodName + " (" + pointName + ")");
      System.exit (1);
   }

   public void PASS (boolean verdad, String comment)
   {
      if ( ! verdad)
      {
         System.err.print (comment);
      }
      PASS (verdad);
   }
}

