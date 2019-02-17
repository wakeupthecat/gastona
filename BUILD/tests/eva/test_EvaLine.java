/**
   @file   test_EvaLine.java

   Created by Alejandro Xalabarder Aulet on 21.02.2005 21:17
*/

import de.elxala.Eva.*;
import de.elxala.langutil.*;

/**
    @class   test_EvaLine
    @author  Alejandro Xalabarder Aulet

*/
public class test_EvaLine extends TestClass
{
   public boolean run ()
   {
      TESTCLASS ("test_EvaLine");

      return testConstructors () &&
             testSetters () &&
             testSomeParsing () &&
             testSmallStress ();
   }

   /**
      Tests all EvaLine constructors.
      Indirectly the methods getColumnArray, get, getValue, cols and toString are also tested
   */
   public boolean testConstructors ()
   {
      METHOD ("testConstructors");

      String [] arrayIs = new String [] {""};
      String toStrIs = "\"\"";

      //
      //  Construction with empty or null elements should become in a EvaLine with one element! (Eva Specification)
      //
      POINT ("p1");
      checkEvaLine (new EvaLine (), arrayIs, toStrIs);
      checkEvaLine (new EvaLine ((String []) null), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (new Cadena ()), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (""), arrayIs, toStrIs);

      POINT ("p2");
      //
      //  Construction with one element
      //
      arrayIs = new String [] { "one" };
      toStrIs = "one";
      checkEvaLine (new EvaLine (arrayIs), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (toStrIs), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (new Cadena (toStrIs)), arrayIs, toStrIs);

      POINT ("p3");

      checkEvaLine (new EvaLine ("\t   one"), arrayIs, toStrIs);
      checkEvaLine (new EvaLine ("   \tone"), arrayIs, toStrIs);
      checkEvaLine (new EvaLine ("one,"), arrayIs, toStrIs);
      checkEvaLine (new EvaLine ("  \t\t \t \"one\","), arrayIs, toStrIs);
      checkEvaLine (new EvaLine ("  \t\t \t one,"), arrayIs, toStrIs);

      POINT ("p4");
      //
      //  Construction with two elements
      //
      arrayIs = new String [] { "one", "two" };
      toStrIs = "one,two";
      checkEvaLine (new EvaLine (arrayIs), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (toStrIs), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (new Cadena (toStrIs)), arrayIs, toStrIs);

      POINT ("p5");

      arrayIs = new String [] { "", "two" };
      toStrIs = "\"\",two";
      checkEvaLine (new EvaLine (arrayIs), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (toStrIs), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (new Cadena (toStrIs)), arrayIs, toStrIs);
      checkEvaLine (new EvaLine ("  \t ,   \t \t two"), arrayIs, toStrIs);

      POINT ("p6");

      //
      //  Construction with two elements
      //
      arrayIs = new String [] { "one", "two", "three", "four", "five" };
      toStrIs = "one,two,three,four,five";
      checkEvaLine (new EvaLine (arrayIs), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (toStrIs), arrayIs, toStrIs);
      checkEvaLine (new EvaLine (new Cadena (toStrIs)), arrayIs, toStrIs);
      checkEvaLine (new EvaLine ("    one,  two \t ,  \"three\"   ,   four,five"), arrayIs, toStrIs);

      return true;
   }


   /**
      Tests all EvaLine constructors.
      Indirectly the methods getColumnArray, get, getValue, cols and toString are also tested
   */
   public boolean testSetters ()
   {
      METHOD ("testSetters");

      EvaLine obj = new EvaLine ();

      POINT ("set1");

      PASS (obj.set("one",0)); // element 0 always exists!
      PASS (false == obj.set("other",1));
      PASS (false == obj.set("other",2));
      PASS (false == obj.set("other",3));
      PASS (null == obj.get(1));
      PASS (null == obj.get(2));
      PASS (null == obj.get(3));

      POINT ("set2");

      obj.setValue ("seis", 5);   // we create now the sixth element (0..5)
      PASS (obj.cols () == 6);
      PASS (obj.get(5).equals ("seis"));

      POINT ("set3");

      PASS (obj.set("two",1));
      PASS (obj.set("three",2));
      PASS (obj.set("four",3));
      PASS (obj.set("five",4));
      PASS (obj.set("six",5));
      PASS (false == obj.set("seven",6)); // only 0..5 indexes available!
      PASS (null == obj.get(6));

      String [] arrayIs = new String [] {"one", "two", "three", "four", "five", "six" };
      String toStrIs = "one,two,three,four,five,six";

      POINT ("set4");

      checkEvaLine (obj, arrayIs, toStrIs);

      POINT ("set5");

      String [] arrayWas = obj.getColumnArray();

      obj.set (new String [] {"erase", "old"});
      PASS (obj.cols () == 2);
      PASS (null == obj.get(2));
      PASS (null == obj.get(3));

      POINT ("set6");

      obj.set (arrayWas);
      checkEvaLine (obj, arrayIs, toStrIs);

      return true;
   }

   public boolean testSomeParsing ()
   {
      METHOD ("testSomeParsing");

      //
      // test some parsing (more of these are in testCadena and testEvaFile)
      //
      String difficult = "   \t\t  first,,   \"third, more third\", \"\"\"four\"\"\",,end";
      String [] arrayIs = new String [] {"first", "", "third, more third", "\"four\"", "", "end" };

      EvaLine obj = new EvaLine (difficult);
      checkEvaLine (obj, arrayIs, null);

      String [] asColArr = obj.getColumnArray();
      String asString = obj.toString();

      POINT ("checkEvaLine 2");

      obj = new EvaLine (asColArr);
      checkEvaLine (obj, arrayIs, null);

      POINT ("checkEvaLine 3");

      obj = new EvaLine (new Cadena (asString));
      checkEvaLine (obj, arrayIs, null);

      return true;
   }

   private final int MANY_COLUMNS = 1000;

   public boolean testSmallStress ()
   {
      if (false == STRESS_TESTS) return true;

      METHOD ("testSmallStress");

      EvaLine obj = new EvaLine ();

      //
      // make a big EvaLine with stress
      //
      for (int ii = 0; ii < MANY_COLUMNS; ii ++)
      {
         obj.setValue ("elem \"" + ii + "\" of the EvaLin", ii);
      }
      PASS (obj.cols () == MANY_COLUMNS);

      String [] asColArr = obj.getColumnArray();
      String asString = obj.toString();

      obj.set (new Cadena ());
      PASS (obj.cols () == 1);

      obj.set (new Cadena (asString));
      PASS (obj.cols () == MANY_COLUMNS);

      POINT ("checkEvaLine 1");
      checkEvaLine (obj, asColArr, null);

      return true;
   }

   /**
      Checks is the EvaLine 'obj' has the same elements as the array 'data' and
      the result of toString method is equal to 'toStringShould'
   */
   private void checkEvaLine (EvaLine obj, String [] data, String toStringShould)
   {
      PASS (obj.cols () == data.length);
      PASS (obj.cols () == data.length);
      PASS (obj.getColumnArray().length == data.length);

      for (int ii = 0; ii < data.length; ii ++)
      {
         // POINT ("at column ii = " + ii);
         PASS (obj.get(ii).equals(data[ii]));
         PASS (obj.getValue(ii).equals(data[ii]));
      }

      // POINT ("Check 2");
      if (toStringShould != null)
      {
         PASS (obj.toString().equals(toStringShould)); // result of toString [""]
      }
   }
}
