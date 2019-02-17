/**
   @file           suitEva.java

   Created by AXAulet on 17.02.2005 12:42:48
*/

import de.elxala.langutil.*;

/**
*/
public class suitEva
{
   public static void main (String [] aa)
   {
      TestClass.STRESS_TESTS = false;
      if (aa.length > 0)
      {
         TestClass.STRESS_TESTS = (1 == stdlib.atoi (aa[0]));
      }

      System.out.println ("==================");
      System.out.println ((TestClass.STRESS_TESTS) ? "Suit with stress": "Suit without stress");
      System.out.println ("==================");

      // ------------

      new test_Eva ().run ();
      new test_EvaLine ().run ();
      new test_Cadena ().run ();

      // ------------

      System.out.println ("\n\nPASSED!\n");
   }
}
