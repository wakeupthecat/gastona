/**
   @file   test_Eva.java

   Created by Alejandro Xalabarder Aulet on 21.02.2005 21:17
*/

import de.elxala.Eva.*;

/**
    @class   testEva
    @author  Alejandro Xalabarder Aulet

*/
public class test_Eva extends TestClass
{
   public boolean run ()
   {
      TESTCLASS ("test_Eva");

      return testCreation () &&
             testAddSet ();
   }

   private String [][] arrayIs
      = new String [][]
     {
        { "one", "two", "three", "four", "five", "six" },
        { "",      "2",     "3",    "4", " 5, 6, 7, 8" },
        { "primera", "segunda" }
     };

   private String [] textArrayIs
      = new String []
     {
        "onetwothreefourfivesix",
        "234 5, 6, 7, 8",
        "primerasegunda"
     };

   /**
      Tests all EvaLine constructors and creator methods.
      Indirectly many other methods like get, getValue, rows, cols and toString are also tested
   */
   public boolean testCreation ()
   {
      METHOD ("testCreation");

      Eva obj = new Eva ();
      PASS (obj.rows () == 0);

      obj = new Eva ("myEva");
      PASS (obj.rows () == 0);
      PASS (obj.get (0) == null);
      PASS (obj.getName ().equals ("myEva"));

      POINT ("p1");

      obj = new Eva ("myEva2", "this is, an example");
      PASS (obj.rows () == 1);
      PASS (obj.cols (0) == 1);
      PASS (obj.get (0) != null);
      PASS (obj.get (1) == null);
      PASS (obj.get (0).cols () == 1);
      PASS (obj.getName ().equals ("myEva2"));
      PASS (obj.getValue().equals ("this is, an example"));
      obj.setName ("myEva22");
      PASS (obj.getName ().equals ("myEva22"));

      POINT ("p2");

      obj = new Eva ("myEva3", new String [][] {{ "first row" }, { "one" , "two", "three", "four, 4, cuatro"}} );
      PASS (obj.rows () == 2);
      PASS (obj.cols (0) == 1);
      PASS (obj.cols (1) == 4);
      PASS (obj.getName ().equals ("myEva3"));
      PASS (obj.getValue(0,0).equals ("first row"));
      PASS (obj.getValue(1,0).equals ("one"));
      PASS (obj.getValue(1,1).equals ("two"));
      PASS (obj.getValue(1,2).equals ("three"));
      PASS (obj.getValue(1,3).equals ("four, 4, cuatro"));

      POINT ("p3");

      PASS (obj.get (0) != null);
      PASS (obj.get (1) != null);
      PASS (obj.get (2) == null);
      PASS (obj.get (0).cols () == 1);
      PASS (obj.get (1).cols () == 4);
      PASS (obj.get (0).getValue(0).equals ("first row"));
      PASS (obj.get (1).getValue(0).equals ("one"));
      PASS (obj.get (1).getValue(1).equals ("two"));
      PASS (obj.get (1).getValue(2).equals ("three"));
      PASS (obj.get (1).getValue(3).equals ("four, 4, cuatro"));

      POINT ("p4");

      obj.clear();
      obj.addLine(new EvaLine ("one,  two,  three   ,four,five,six"));
      obj.addLine(new EvaLine (",2,3,       4      ,   \" 5, 6, 7, 8\""));
      obj.addLine(new EvaLine ("    primera, segunda"));
      PASS (obj.rows () == 3);

      checkEva (obj, arrayIs, textArrayIs);

      obj.addLine(0, new EvaLine ("ins0"));
      obj.addLine(1, new EvaLine ("ins1"));
      obj.addLine(3, new EvaLine ("ins3"));
      obj.addLine(5, new EvaLine ("ins5"));
      obj.addLine(20, new EvaLine ("ins20"));

      POINT ("p5");

      PASS (obj.rows () == 8);
      PASS (obj.getValue().equals ("ins0"));
      PASS (obj.getValue(0).equals ("ins0"));
      PASS (obj.getValue(0,0).equals ("ins0"));
      PASS (obj.getValue(1,0).equals ("ins1"));
      PASS (obj.getValue(2,0).equals ("one"));
      PASS (obj.getValue(3,0).equals ("ins3"));
      PASS (obj.getValue(5).equals ("ins5"));
      PASS (obj.getValue(5,0).equals ("ins5"));
      PASS (obj.getValue(6,0).equals ("primera"));
      PASS (obj.getValue(7,0).equals ("ins20"));

      POINT ("p6");

      obj.addLine(2, new EvaLine ("new ins2"));
      PASS (obj.getValue(4,0).equals ("ins3"));

      obj.removeLine (2);
      PASS (false == obj.getValue(4,0).equals ("ins3"));
      PASS (obj.getValue(3,0).equals ("ins3"));

      while (obj.removeLine (3));
      PASS (obj.rows () == 3);

      while (obj.removeLine (0));
      PASS (obj.rows () == 0);

      return true;
   }

   /**
      Tests all EvaLine constructors and creator methods.
      Indirectly many other methods like get, getValue, rows, cols and toString are also tested
   */
   public boolean testAddSet ()
   {
      METHOD ("testAddSet");

      Eva obj = new Eva ();

      PASS (-1 == obj.indexOf ("oneXX"));

      obj.addRow ("oneXX");
      obj.addRow ("");
      obj.addRow ("primeraXX");

      PASS (3 == obj.rows ());
      PASS (0 == obj.indexOf ("oneXX"));
      PASS (-1 == obj.indexOf ("fourXX"));

      obj.addCol ("two");
      obj.addCol ("three");
      obj.addCol ("fourXX");
      obj.addCol ("five", 0);
      obj.addCol ("six", 0);

      POINT ("p1");

      PASS (6 == obj.cols (0));
      PASS (0 == obj.indexOf ("oneXX"));
      PASS (2 == obj.indexOf ("primeraXX"));
      PASS (3 == obj.colOf ("fourXX"));

      obj.addCol ("2", 1);
      obj.addCol ("3", 1);
      obj.setValue ("one", 0, 0);
      obj.setValue ("four", 0, 3);
      obj.setValue (" 5, 6, 7, 8", 1, 4);
      obj.setValue ("4", 1, 3);

      obj.setValue ("segunda", 2, 1);
      obj.setValue ("primera", 2, 0);

      POINT ("p2");

      checkEva (obj, arrayIs, textArrayIs);

      POINT ("p3");

      PASS (2 == obj.rowOf ("primera"));
      PASS (1 == obj.rowOf ("3", 2));
      PASS (1 == obj.rowOf (" 5, 6, 7, 8", 4));

      PASS (4 == obj.colOf ("five"));
      PASS (5 == obj.colOf ("six", 0));
      PASS (4 == obj.colOf (" 5, 6, 7, 8", 1));
      PASS (1 == obj.colOf ("segunda", 2));
      PASS (-1 == obj.colOf ("segunda ", 2));

      PASS (6 == obj.maxCols ());
      PASS (2 == obj.minCols ());

      return true;
   }

   /**
      Checks is the EvaLine 'obj' has the same elements as the array 'data' and
      the result of toString method is equal to 'toStringShould'
   */
   private void checkEva (Eva obj, String [][] data, String [] asArrayShould)
   {
      PASS (obj.rows () == data.length);
      for (int rr = 0; rr < data.length; rr ++)
      {
         POINT ("at row rr = " + rr);
         PASS (obj.cols(rr) == data[rr].length);

         EvaLine oLine = obj.get(rr);

         PASS (oLine != null);

         for (int cc = 0; cc < data[rr].length; cc ++)
         {
            String str1 = oLine.get(cc);
            String str2 = obj.getValue(rr,cc);

            POINT ("at row rr = " + rr + " col = " + cc);
            PASS (str1 != null);
            PASS (str1.equals (str2));
            PASS (str1.equals (data[rr][cc]));
         }
      }
      POINT ("");

      if (asArrayShould != null)
      {
         String [] asTextArr = obj.getAsArray ();

         String formText = "";
         String RET = de.elxala.langutil.filedir.TextFile.RETURN_STR;

         for (int rr = 0; rr < data.length; rr ++)
         {
            POINT ("at row rr = " + rr);
            PASS (asTextArr[rr].equals (asArrayShould[rr]));

            // check ::getStrArray
            String [] lineArray = obj.getStrArray(rr);
            PASS (lineArray.length == data[rr].length);
            for (int cc = 0; cc < lineArray.length; cc ++)
            {
               POINT ("at row rr = " + rr + " col " + cc);
               PASS (lineArray[cc].equals (data[rr][cc]));
            }

            // formText to compare with ::AsText
            formText += ((rr == 0) ? "": RET);
            formText += asTextArr[rr];
         }

         POINT ("\n===ASTEXT====\n" + obj.getAsText () +
                   "\n===FORMEXT===\n" + formText +
                   "\n=============\n");
         String asText = obj.getAsText ();
         PASS (asText.equals (formText));
      }
	}
}
