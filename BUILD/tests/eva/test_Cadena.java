import de.elxala.langutil.*;
import java.util.*;

/**
    @class   test_Cadena
    @author  Alejandro Xalabarder Aulet

*/
public class test_Cadena extends TestClass
{
   public boolean run ()
   {
      TESTCLASS ("test_Cadena");

      return testCreation () &&
             testTokens () &&
             testTransform ();
   }

   public boolean testCreation ()
   {
      METHOD ("testCreation");

      Cadena cad1 = new Cadena ("123456789.123456789.12345678");
      Cadena cad2 = new Cadena ();
      cad2.setStr ("123456789.123456789.12345678");

      PASS (cad1.length () == 28);
      PASS (cad1.getStr ().equals (cad2.getStr ()));
      PASS (cad1.charAt (9) == '.');
      PASS (cad1.charAt (19) == '.');
      PASS (cad1.charAt (27) == '8');

      cad1.clear ();
      cad1.setStr (cad2.getStr ());
      PASS (cad1.length () == 28);
      PASS (cad1.getStr ().equals (cad2.getStr ()));

      Cadena cad3 = new Cadena ("one&two/three,four!five", "&/,!");
      String [] arr = Cadena.simpleToArray(cad3.getStr (), "&/,!");

      PASS (arr.length == 5);

      PASS (cad3.getToken ());
      PASS (arr[0].equals ("one"));
      PASS (cad3.lastToken ().equals (arr[0]));

      PASS (cad3.getToken ());
      PASS (cad3.getToken ());
      PASS (cad3.getToken ());
      PASS (cad3.getToken ());

      PASS (arr[4].equals ("five"));
      PASS (cad3.lastToken ().equals (arr[4]));

      PASS (false == cad3.getToken ());

      return true;
   }

   public boolean testTokens ()
   {
      METHOD ("testTokens");

      Cadena cad = new Cadena (",/ 2  first,3,\",5 five/6six\"//8 eight,end");

      cad.setSepar ("/,");
      String [] arr = Cadena.simpleToArray(cad.o_str, "/,");
      String [] arr2 = Cadena.simpleToArray(cad.o_str);

      List list1 = Cadena.simpleToList(cad.o_str, "/,");
      List list2 = Cadena.simpleToList(cad.o_str);

      PASS (arr.length == list1.size ());
      PASS (arr2.length == list2.size ());

      PASS (arr2[0].equals ((String) list2.get (0)));
      PASS (arr2[5].equals ((String) list2.get (5)));

      PASS (arr[0].equals ((String) list1.get (0)));
      PASS (arr[9].equals ((String) list1.get (9)));


      // only using "," as separator
      //
      PASS (arr2.length == 6);
      PASS (arr2[1].equals ("/ 2  first"));
      PASS (arr2[4].equals ("5 five/6six\"//8 eight"));

      PASS (arr.length == 10);

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[0]));
      PASS (cad.lastToken ().equals (""));

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[1]));
      PASS (cad.lastToken ().equals (""));

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[2]));
      PASS (cad.lastToken ().equals (" 2  first"));

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[3]));
      PASS (cad.lastToken ().equals ("3"));

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[4]));
      PASS (cad.lastToken ().equals ("\""));

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[5]));
      PASS (cad.lastToken ().equals ("5 five"));

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[6]));
      PASS (cad.lastToken ().equals ("6six\""));

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[7]));
      PASS (cad.lastToken ().equals (""));

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[8]));
      PASS (cad.lastToken ().equals ("8 eight"));

      PASS (cad.getToken ());
      PASS (cad.lastToken ().equals (arr[9]));
      PASS (cad.lastToken ().equals ("end"));

      PASS (false == cad.getToken ());

      return true;
   }

   public boolean testTransform ()
   {
      METHOD ("testTransform");

                            //  0123456789.123456789.123456789.
      Cadena cad = new Cadena ("123456789.123456789.12345678");

      PASS (cad.del (300, 22) == 6);   // "123456789.123456789.12"
      PASS (cad.length () == 22);

                                       //  0123456789.12345678
      PASS (cad.del (4) == 4);   // "56789.123456789.12"
      PASS (cad.length () == 18);
      PASS (cad.InitialMatch ("5"));
      PASS (cad.InitialMatch ("56"));
      PASS (cad.InitialMatch ("567"));

      PASS (cad.substrBE (5, 7).equals (".12"));
      PASS (cad.substrBE (-5, 3).equals ("5678"));
      PASS (cad.substrBE (-5, 999).equals (cad.getStr ()));
      PASS (cad.substrBE (14, 999).equals ("9.12"));

      //                             0123456789.123456789.12345
      cad.setStr ("          \t \t   this is a sentence to test  \t \t  ");
      cad.trimMe ();
      PASS (cad.length () == 26);
      PASS (cad.indexOf ("ia") == 2);
      PASS (cad.indexOf ("it", 0) == 0);
      PASS (cad.indexOf ("it", 6) == 13);

      PASS (cad.find ("is") == 2);
      PASS (cad.find (" is") == 4);
      PASS (cad.find ("is", 3) == 5);
      PASS (cad.find ("te") == 13);

      PASS (cad.indexOfsubstr ("is a") == 5);
      PASS (cad.indexOfsubstr ("is a", 6) == -1);
      PASS (cad.indexOfsubstr ("en") == 11);
      PASS (cad.indexOfsubstr ("en", 11) == 11);
      PASS (cad.indexOfsubstr ("en", 12) == 14);

      cad.replaceMe ('i', 'I');
      cad.replaceMe ('t', 'T');
      PASS (cad.equals ("ThIs Is a senTence To TesT"));

      PASS (cad.replaceMe ("en", "(EN)") == 2);
      PASS (cad.replaceMe ("Th", "(TH)") == 1);
      PASS (cad.replaceMe ("esT", "(EST)") == 1);

      PASS (cad.equals ("(TH)Is Is a s(EN)T(EN)ce To T(EST)"));

      cad.setStr ("<<man>> is a man and has <<age>> years old. he (<<man>>) knows that he will <<age>> just for a year.");
      Map mapp = new HashMap ();

      mapp.put ("age", "111");
      mapp.put ("man", "Mathusalem(<<age>>)");

      cad.replaceMe (mapp, "<<", ">>");

      POINT ("[" + cad.getStr () + "]");
      PASS (cad.equals ("Mathusalem(111) is a man and has 111 years old. he (Mathusalem(111)) knows that he will 111 just for a year."));

      return true;
   }
}
/*
         //
         //   Comparando con StringTokenizer el mio es mas mejor
         //   porque como se vera no me retorna elementos nulos (entre comas ,,)
         //
          java.util.StringTokenizer ajo = new
          java.util.StringTokenizer("This, is /a cosa, und,, yaesta,,", "/,", false);

          while (ajo.hasMoreTokens())
               System.out.println ("Tok = [" + ajo.nextToken () + "]");

          String [] arro;

          cada.setStr ("    This, \"is /a cosa, und\",, yaesta,,");
          System.out.println ("\nArray de <" + cada.getStr () + ">\n");
          arro = cada.toStrArray ();
          for (int ii = 0; ii < arro.length; ii ++)
            System.out.println ("[" + arro[ii] + "]");

          System.out.println ("\nnuevamente...\n");
          cada.setStrArray (arro);
          arro = cada.toStrArray ();
          for (int ii = 0; ii < arro.length; ii ++)
            System.out.println ("[" + arro[ii] + "]");

          String [] arra = new String [] { " mi, primera", "impresion", "es \"buena\"" };
          String [] arre = new String [] { "\"asierto\"", ",atonito,", "&& 'mis mas respetos" };

      arro = arra;
          cada.setStrArray (arro);
          System.out.println ("\ntenemos <<" + cada.getStr () + ">>\n");
          arro = cada.toStrArray ();
          for (int ii = 0; ii < arro.length; ii ++)
            System.out.println ("[" + arro[ii] + "]");

      arro = arre;
          cada.setStrArray (arro);
          System.out.println ("\ntenemos <<" + cada.getStr () + ">>\n");
          arro = cada.toStrArray ();
          for (int ii = 0; ii < arro.length; ii ++)
            System.out.println ("[" + arro[ii] + "]");

      String [] lio = new String [2];
          cada.setStrArray (arra);
      lio[0] = cada.getStr ();
          cada.setStrArray (arre);
      lio[1] = cada.getStr ();

      arro = lio;
          cada.setStrArray (arro);
          System.out.println ("\ntenemos <<" + cada.getStr () + ">>\n");
          arro = cada.toStrArray ();
          for (int ii = 0; ii < arro.length; ii ++)
            System.out.println ("[" + arro[ii] + "]");

      String la1 = arro[0];
      String la2 = arro[1];

          cada.setStr (la1);
          System.out.println ("\ntenemos <<" + cada.getStr () + ">>\n");
          arro = cada.toStrArray ();
          for (int ii = 0; ii < arro.length; ii ++)
            System.out.println ("[" + arro[ii] + "]");

          cada.setStr (la2);
          System.out.println ("\ntenemos <<" + cada.getStr () + ">>\n");
          arro = cada.toStrArray ();
          for (int ii = 0; ii < arro.length; ii ++)
            System.out.println ("[" + arro[ii] + "]");
   }

     static void test2 () {
      Cadena cada = new Cadena ();
      int ti;

      cada.setStr ("this is something nuevo in the nuevo language");

          System.out.println ("\nOriginal string [" + cada.getStr () + "]\n");
      ti = cada.replaceMe ("nuevo", "new");
          System.out.println ("\nNew      string [" + cada.getStr () + "]\n");
          System.out.println ("(" + (ti) + " times)\n");

      cada.setStr ("aau ist die Richtige man fuer aau Arbeit!");

          System.out.println ("\nOriginal string [" + cada.getStr () + "]\n");
      ti = cada.replaceMe ("aau", "Alejandro Aulet (aau)");
          System.out.println ("\nNew      string [" + cada.getStr () + "]\n");
          System.out.println ("(" + (ti) + " times)\n");


      cada.setStr ("nuevo! this is something nuevo in the nuevo language nuevo");

          System.out.println ("\nOriginal string [" + cada.getStr () + "]\n");
      ti = cada.replaceMe ("", "dfs");
          System.out.println ("\nNew      string [" + cada.getStr () + "]\n");
          System.out.println ("(" + (ti) + " times)\n");
     }

     static void test3 () {
      Cadena a = new Cadena ("esto es lo que pasa");
      Cadena b = new Cadena (" esto es lo que pasa");

      System.out.println ("\ntest InitialMatch is it trueflase ?\n" +
                  a.InitialMatch ("esto") +
                  b.InitialMatch ("esto")
                  );
      Cadena c = new Cadena ("me gettoken ?");
      c.setSepar ("/");

      boolean uno = c.getToken ();
      boolean dos = c.getToken ();

      System.out.println ("\ntest getToken trueflase ?\n" + uno + dos);

      c = new Cadena ("buscameMaria por entre el Mar ia texto Maria a ver");
      System.out.print ("\n           0123456789.123456789.123456789.123456789.123456789.123456789.\n");
      System.out.print ("test find: " + c.getStr ());

      int indx = 0;
      while ((indx = c.find ("Maria", indx)) != -1) {
         System.out.print ("\nEncontrada en pos = " + indx + "\n");
         indx ++;
      }

      c = new Cadena ("This");
      int que = c.find("\"\"", 0);

      System.out.println ("\nSalida que = " + que);

      System.out.println ("\nReemplazos (0) = " + c.replaceMe ("\"\"", "maracaibo"));
     }

   static void test_replaceMap0 () {
      Cadena template = new Cadena ("Hallo <<name>>, your name (<<name>>) is very <<adjective>>");
      Map mapo = new TreeMap ();

          mapo.put ("name",      "Alejandro");
        mapo.put ("adjective", "feo");
      mapo.put ("adjective", "bonito <<name>>");

      template.replaceMe (mapo, "<<", ">>");

      System.out.println (template.getStr ());
   }

   static void test_replaceMap1 () {
      Cadena template = new Cadena ("<<yin>> y <<yan>>");
      Map mapo = new TreeMap ();

          mapo.put ("yin", "<<yan>>");
        mapo.put ("yan", "<<yin>>");

      template.replaceMe (mapo, "<<", ">>");

      System.out.println (template.getStr ());
   }

   static void testya () {
      Cadena que;
      String [] arra;

      que = new Cadena ("");
      arra = que.toStrArray ();
      //System.out.println ("toStrArray 1 = (" + arra.length + ") [" + arra[0] + "]");
      que.setStrArray (arra);
      System.out.println ("Resalido = [" + que.getStr () + "]");

      que = new Cadena ("\"\"");
      // PRIVADO! que.DesEnpaquetaMe ();
      // PRIVADO! System.out.println ("RECANCARNAS = [" + que.getStr () + "]");
      que = new Cadena ("\"\"");
      arra = que.toStrArray ();
      System.out.println ("toStrArray 2 = (" + arra.length + ") [" + arra[0] + "]");
      que.setStrArray (arra);
      System.out.println ("Resalido = [" + que.getStr () + "]");

      que = new Cadena ("\"\"\"");
      arra = que.toStrArray ();
      System.out.println ("toStrArray 3 = (" + arra.length + ") [" + arra[0] + "]");
      que.setStrArray (arra);
      System.out.println ("Resalido = [" + que.getStr () + "]");
   }

   public static void tienecohones ()
   {
      System.out.println ("PRUEBA 1 de Tiene cohones");
      Cadena ca = new Cadena ("16, 2");

      ca.setStr ("16, 2");
      ca.getToken ();
      System.out.println ("tok 1 = <" + ca.lastToken () + ">");
      ca.getToken ();
      System.out.println ("tok 2 = <" + ca.lastToken () + ">");

      System.out.println ("PRUEBA 2 de Tiene cohones");


      Cadena lin = new Cadena ();

      lin.setStr ("16, 2");

      Integer pide = new Integer (0), ancho = new Integer (0);
      String teniamos = lin.o_str;

      try {
         lin.getToken ();
         pide = new Integer (lin.lastToken ().trim ());
         lin.getToken ();
         ancho = new Integer (lin.lastToken ().trim ());
      }
      catch (java.lang.NumberFormatException ij) {
               System.out.println ("lisnea ???? [" + teniamos + "] pide = " + pide + " ancho = " + ancho);
         return;
      }

            System.out.println ("lisnea [" + teniamos + "] pide = " + pide + " ancho = " + ancho);
   }

   public static void test_simpleTo ()
   {
      String s1 = "this,is,\"\"third,element,and last one\"\"";   //  [this,is,"third,element,and last one"]
      String s2 = "another:possibility/of this";

      String [] a1 = Cadena.simpleToArray (s1, ",");
      String [] a2 = Cadena.simpleToArray (s2, " :/");

      int ii = 0;

      System.out.println ("input simple string [" + s1 + "]");
      for (ii = 0; ii < a1.length; ii++)
         System.out.println ("a1 [" + ii + "] = [" + a1[ii] + "]");

      System.out.println ("input simple string [" + s2 + "]");
      for (ii = 0; ii < a2.length; ii++)
         System.out.println ("a2 [" + ii + "] = [" + a2[ii] + "]");
   }

   public static void testReplaceMeOnce ()
   {
      Cadena cad = new Cadena ("This is \\\\r a %1 \\\\t \r");

      String [][] mapa = {
         { "\\\\", "\\" },
         { "\\r", "retorno fict" },
         { "\r", "retorno real" },
         { "\\t", "tabulator fict"},
         { "%1", "test" }
      };

      // that means replace :
      //    "\\" with "\"
      //    "\r" with "retorno"
      //    "%1" with "test"

      cad.replaceMeOnce (mapa);
      System.out.println (cad.o_str); // will print "This is \r a test"

      cad.replaceMeOnce (mapa);
      System.out.println (cad.o_str); // will print "This is \r a test"
   }


   static private boolean separa_y_array (String que)
   {
      Cadena cada = new Cadena ();
      String [] arra;

      // separacion normal
      System.out.println ("Origen = [" + que + "]");
      cada.setStr (que);
      while (cada.getToken ()) {
         System.out.println ("Tok = [" + cada.lastToken () + "]");
      }

      // ahora a array
      cada.o_str = que;
      arra = cada.toStrArray();
      System.out.println ("");
      for (int ii = 0; ii < arra.length; ii ++)
         System.out.println ("Arr(" + ii + ") = [" + arra[ii] + "]");
      System.out.println ("");

      return true;
   }

   static void testx () {
        separa_y_array ("\"#include <\", \">\"");
        separa_y_array ("\"#include \"\"\", \"\"\"\"");

        separa_y_array ("joder, \"/ \", ostias, putes");
        separa_y_array ("bien, /, one, two");
        separa_y_array ("G:\\Programme\\app\\UltraEdit\\UEDIT32.EXE, \"/\", one, to, tree");

        separa_y_array (" por la \" cara, segundo, \"tercero\", fin");
        separa_y_array (" por la \"\" cara2, segundo, \"tercero\", fin");
   }


*/
