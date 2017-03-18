/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2005  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.langutil;

import java.util.*;

/**
   class Cadena
   @author Alejandro Xalabarder Aulet
   @date   2001

   Cadena wraps a String and adds some functionality. The most important added (needed) functionality
   is to parse a string comma separated in Eva style (see EvaFormat.pdf).

   02.02.2005 12:00 EVA Text File Specification v1.0
*/
public class Cadena
{
   protected final static char ATOM_ENVOLVER = 34;  // "
   protected final static char ATOM_SEPARATOR = 44;    // ,

   private static final String START_LITERAL_1 = "'";
   private static final String START_LITERAL_2 = "//";

   public String o_str, o_lastToken, o_separ;

   public Cadena ()                     {  clear (); }
   public Cadena (String cad)           {  clear (); o_str = (cad != null) ? cad:"";  }
   public Cadena (String cad, String sepa) {  clear (); o_str = (cad != null) ? cad:"";  o_separ = (sepa != null) ? sepa:""; }

   public void clear ()
   {
      o_str = new String();
      o_separ = ",";
      o_lastToken = "";
   }

   public String getStr    ()              { return o_str; }
   public int    length    ()              { return o_str.length (); }
   public char   charAt    (int indx)      { return o_str.charAt (indx); }


   /**
      This method is important here because ::equals has a default implementation (in Object class) that could be
      missued accidentally.

         example
         <pre>
            Cadena cad = new Cadena ("hola");

            cad.equals ("hola"); // we would expect true here! although it is clear that a Cadena is not a String!
         </pre>

   */
   public boolean equals (String strval)
   {
      return o_str.equals (strval);
   }


   public void setStr   (String cad)
   {
      o_str = (cad != null) ? cad: "";
   }

   public void setSepar (String ssepar)
   {
      o_separ = ssepar;
   }

   public boolean getToken (String ssepar)
   {
      o_separ = ssepar;
      return getToken ();
   }

   public int del (int nchars, int offset)
   {
      int lena = length ();
      o_str = substrBE(0, offset-1) + substrBE (offset + nchars, length ());
      return lena - length ();
   }

   public int del (int nchars)
   {
      return del (nchars, 0);
   }

   public boolean InitialMatch (String str)
   {
      if (length () < str.length ())
         return false;
      return o_str.startsWith (str);
   }


     /*  Comprueba limites y desde hasta inclusive (sin -1 o +1 !!)
     *   (BE = begin end)
     */
   public String substrBE (int ini, int fin)
   {
      ini = Math.max (ini, 0);
      fin = Math.min (fin, length ()-1);

      if (ini > fin)
         return new String ("");

      return new String (o_str.substring (ini, fin + 1));
   }

   /*  indexOf any character within the String chars
   */
   public int indexOf (String chars, int desde)
   {
      int tt = desde;

      while (desde < length ())
      {
         for (int ii = 0; ii < chars.length (); ii ++)
            if (chars.charAt(ii) == o_str.charAt(desde)) return desde;
         desde ++;
      }
      return -1;
   }

     public int indexOf (String chars) {
      return indexOf (chars, 0);
     }

   /** indexOf any character within the String chars
   */
   public int indexOfsubstr (String substr, int desde)
   {
      int tt = desde;

      while (desde < length ()) {
         if (substr.equals (substrBE (desde, desde + substr.length () - 1)))
            return desde;
         desde ++;
      }
      return -1;
   }

   /** indexOf any character within the String chars
   */
   public int indexOfsubstr (String substr)
   {
      return indexOfsubstr (substr, 0);
   }

   public void replaceMe (char que, char por)
   {
      o_str = o_str.replace (que, por);
   }

   /** Example:

      Cadena cad = new Cadena ("This is \\\\r a %1 \\\\t");

      String [][] mapa = { { "\\\\", "\\" }, { "\\r", "retorno" }, { "%1", "test" } };

      //    original string is : [This is \\r a %1 \\t]
      // and we will replace :
      //    "\\" with "\"
      //    "\r" with "retorno"
      //    "%1" with "test"

      cad.replaceMeOnce (mapa);
      System.out.println (cad.s_str); // will print "This is \r a test"
   */
   public int replaceMeOnce (String [][] mapa)
   {
      int point = 0, miny = 0, sus = 0, indx;
      StringBuffer ss = new StringBuffer ();

      while (point < length ())
      {
         // look the replacement closest to the beginning
         miny = length ();
         indx = -1;
         for (int ii = 0; ii < mapa.length; ii ++)
         {
            int pt = indexOfsubstr (mapa [ii][0], point);
            if (pt != -1 && pt < miny)
            {
               miny = pt;
               indx = ii;
            }
         }

         if (indx == -1)
         {
            // nothing more to replace
            ss.append (substrBE (point, length ()));
            point = length ();
         }
         else
         {
            // something to replace
            ss.append (substrBE (point, miny - 1));
            ss.append (mapa [indx][1]);
            point = miny + mapa[indx][0].length ();
            sus ++;
         }
      }

      if (sus != 0)
         o_str = ss.toString ();

      return sus;
   }


   /*  Extension de replace con Strings
       returns the number of times the replace is done
   */
   public int replaceMe (String busca, String reemp)
   {
      // is there something to do ?
      //
      if (busca.length () == 0)
         return 0;

      // do it!
      //
      int pp = 0;
      int veces = 0;

      while (pp < length ())
      {
         pp = find (busca, pp);
         if (pp == -1) break; // >>>>>>>
         // found it !
         o_str = substrBE (0, pp-1) +
               reemp +
               substrBE (pp + busca.length (), length ());
         veces ++;
         pp += reemp.length ();
      }
      return veces;
   }


   /*  Macro substitution, example:

      Cadena template = new Cadena ("Hallo <<name>>, your name (<<name>>) is very <<adjective>>");
      Map mapo = new TreeMap ();

          mapo.put ("name",      "Alejandro");
        mapo.put ("adjective", "feo");
         mapo.put ("adjective", "bonito");

      template.replaceMe (mapo, "<<", ">>");

      System.out.println (template.s_out);

   */
   public int replaceMe (Map Pairs, String sOpen, String sClose)
   {
      // EL MAPA NO TIENE ITERATORS !!! que bien !!
      // hay que extraer las claves en un array con iterators y bla bla

      List llaves = new ArrayList(Pairs.keySet ());
      int parcial, kk, total = 0, vueltas = 0;

      do
      {
         if (vueltas >= 2)
         {
            System.err.println ("ERROR in Cadena::replaceMe (TreeMap ...)");
            return - total;
         }

         parcial = kk = 0;
         while (kk < llaves.size ())
         {
            String skey, skeyFin;

            skey    = (String) llaves.get (kk ++); // i.e. "Name"
            skeyFin = sOpen + skey + sClose;    // new key (i.e. "<<Name>>")
            parcial += replaceMe (skeyFin, (String) Pairs.get (skey));
         }
         total += parcial;
         vueltas ++;
      } while (parcial > 0);

      return total;
   }

   /*  find string a partir de 0
   */
   public int find (String busca)
   {
      return find (busca, 0);
   }

   /*  find string a partir de indice
   */
   public int find (String busca, int indx)
   {
      if (busca.length () == 0) return -1;

      int pp = indx;
      while (pp != -1)
      {
         if ( (-1 == (pp = o_str.indexOf(busca.charAt (0), pp))) ||
              busca.equals (substrBE (pp, pp + busca.length ()-1))
            )
            break;
         pp ++;
      }
      return pp;
   }

   public void trimMe ()
   {
      o_str = o_str.trim ();
   }

   public boolean getToken ()
   {
      int wo = indexOf (o_separ, 0);

      if (wo < 0)
      {
         o_lastToken = o_str;
         o_str = "";
         return (o_lastToken.length () > 0);
      }

      o_lastToken = o_str.substring (0, wo);
      o_str = o_str.substring (wo+1, length ());
      return true;
   }

   public String lastToken ()
   {
      return o_lastToken;
   }

   private void EnpaquetaMe ()
   {
      //_DBG_ System.out.println ("[>>] Cadena::EnpaquetaMe");
      //_DBG_ System.out.println ("o_str [" + o_str + "]");

      if (o_str.length () == 0)
      {
         // value is ""
         o_str = ATOM_ENVOLVER + "" + ATOM_ENVOLVER;
         return;
      }

      int tot = 0;
      tot += replaceMe ("\"", "\"\"");    // contains " (then replace with "")
      tot += replaceMe (",", ",");        // contains ,
      tot += replaceMe ("#", "#");        // contains #
      tot += replaceMe ("<", "<");        // contains <

      if (o_str.startsWith (START_LITERAL_1) ||
          o_str.startsWith (START_LITERAL_2) ||
          o_str.startsWith (" ") ||
          o_str.startsWith ("\t") ||
          o_str.endsWith (" ") ||
          o_str.endsWith ("\t"))
      {
         tot ++;
      }

      if (tot > 0)
         o_str = ATOM_ENVOLVER + o_str + ATOM_ENVOLVER;
      //_DBG_ System.out.println ("o_str [" + o_str + "]");
   }

   private void DesEnpaquetaMe ()
   {
      //_DBG_ System.out.println ("[>>] Cadena::DesEnpaquetaMe");
      //_DBG_ System.out.println ("o_str [" + o_str + "]");

      trimMe ();

      if (length () < 2)
      {
         //_DBG_ System.out.println ("[<<]  Cadena::DesEnpaquetaMe");
         return;
      }

      // take off ""
      //
      if (o_str.charAt(0) == ATOM_ENVOLVER)
         o_str = substrBE (1, length ());
      if (o_str.charAt(length ()-1) == ATOM_ENVOLVER)
         o_str = substrBE (0, length ()-2);

      //_DBG_ System.out.println ("o_str 2 [" + o_str + "]");

      int tot = 0;
      tot += replaceMe ("\"\"", "\"");

      //_DBG_ System.out.println ("o_str 3 [" + o_str + "]");
      //_DBG_ System.out.println ("[<<]  Cadena::DesEnpaquetaMe");
   }

   private static String getFirstToken (Cadena some)
   {
      //_DBG_ System.out.println ("[>>] Cadena::getFirstToken");
      //_DBG_ System.out.println ("some.o_str [" + some.o_str + "]");

      some.trimMe ();   // no puede contener espacios al principio!

      // is it a unique token ?
      //
      if (some.o_str.startsWith (START_LITERAL_1))
      {
         String str = some.o_str.substring (START_LITERAL_1.length ()); // remove literal1
         some.setStr ("");

         return str;
      }
      if (some.o_str.startsWith (START_LITERAL_2))
      {
         String str = some.o_str.substring (START_LITERAL_2.length ()); // remove literal2
         some.setStr ("");

         return str;
      }

      //
      // OJO !! Esta rutina emplea Cadenas !!
      //
      int Cenv=-2, Csep=-2;
      // poblema gordo :
      //       Tenemos que separar los tokens. Estos estan separados
      //       por comas, pero si un token contiene comas(,) (o comillas ",
      //       o espacios al principio)
      //    entonces se pone entre comillas (envoltura) y si el token contiene comillas
      //    estas se doblan (""). En resumen tenemos que buscar o bien una coma o bien
      //       encontrar   las comillas exclusivamente de envoltura.
      //

      Csep = some.o_str.indexOf (ATOM_SEPARATOR, 0);  // donde esta la coma (,)
      if (Csep == -1)
      {
         // NO HAY COMA "," => un solo token
         Cadena aux = new Cadena (some.o_str);
         aux.DesEnpaquetaMe ();        // unpack it
         some.setStr ("");

         //_DBG_ System.out.println ("some.o_str [" + some.o_str + "]");
         //_DBG_ System.out.println ("retorno (NO HAY COMA) [" + aux.o_str + "]");
         //_DBG_ System.out.println ("[<<] Cadena::getFirstToken");

         return aux.o_str;
      }
      //System.out.println ("primera fase Csep = [" + Csep + "] Cenv [" + Cenv + "]");

      // ... tenemos una coma en la posicion 'Csep'

      // buscamos la la posible envoltura (")
      Cenv = some.o_str.indexOf (ATOM_ENVOLVER, 0);
      if (Cenv == -1 || Cenv > Csep)
      {
         // el problema (") esta mas adelante, en otro token o no esta
         // p.e.   [blabla,otro,"este tiene problemas"]

         Cadena aux = new Cadena (some.substrBE (0, Csep-1));
         some.setStr (some.substrBE (Csep+1, some.length ()));

         aux.DesEnpaquetaMe ();        // unpack it

         //_DBG_ System.out.println ("some.o_str [" + some.o_str + "]");
         //_DBG_ System.out.println ("retorno (el problema (\") esta mas adelante) [" + aux.o_str + "]");
         //_DBG_ System.out.println ("[<<] Cadena::getFirstToken");

         return aux.o_str;
      }
      //System.out.println ("segunda fase Csep = [" + Csep + "] Cenv [" + Cenv + "]");

      // ... una comilla antes de la coma 'Csep' (debe ser en la posicion 0 puesto que hemos hecho un trim)
      // "....",
      // "..,..",
      // "..."",...",
      // ASSERT(Cenv == 0)

      //_DBG_ System.out.println ("Cenv 1 = " + Cenv);
      // Buscamos la siguiente envoltura valida saltandonos las dobles comillas [""]
      while (Cenv < some.length ()) {
         Cenv = some.o_str.indexOf (ATOM_ENVOLVER, Cenv+1);
         if (Cenv == -1) {
            // no esta bien cerrado .. problemas ... lo enchufamos todo
            Cenv = some.length ();
         }
         if (Cenv+1 >= some.length () ||
             some.o_str.charAt (Cenv+1) != ATOM_ENVOLVER) break;
         // era una doble comilla
         Cenv ++;
      }
      //System.out.println ("tercera fase Csep = [" + Csep + "] Cenv [" + Cenv + "]");
      //_DBG_ System.out.println ("Cenv 2 = " + Cenv);

      Cadena aux = new Cadena (some.substrBE (0, Cenv));
      some.setStr (some.substrBE (Cenv+1, some.length ()));
      some.trimMe();
      // todavia queda la posibilidad que ahora tengamos un separador!
      if (some.length() > 0 && some.o_str.charAt(0) == ATOM_SEPARATOR)
         some.del(1);
      aux.DesEnpaquetaMe ();        // unpack it

      //_DBG_ System.out.println ("some.o_str [" + some.o_str + "]");
      //_DBG_ System.out.println ("retorno (tratadas comillas) [" + aux.o_str + "]");
      //_DBG_ System.out.println ("[<<] Cadena::getFirstToken");

      return aux.o_str;
   }

   /**
      pass the content of a Cadena to an array of chars
      USING ALWAYS! the separator ',' and taking into account the '"' caracters
      If the String (contents of Cadena) ends with "," there is no element after the comma.

      <pre>
         Let's see some special cases

         CONTENT
         OF CADENA    ARRAY
         ---------    -----------------
            ""        []
            ","       []
            "a,"      [a]
            ",,"      []  []

      Example :

         Cadena o = new Cadena ("this,is,\"third,element,and last one\"");
         // that means the string [this,is,"third,element,and last one"]

         String [] arr = o.toStrArray ();

         then
         arr[0] == [this]
         arr[1] == [is]
         arr[2] == [third,element,and last one]

      </pre>
   */
   public String [] toStrArray ()
   {
      Cadena queda = new Cadena(o_str);
      List colec = new Vector ();

      // special case ! string "" has to retorn one element (like ",")
      //
      if (queda.length () == 0)
         queda.setStr (",");

      while (queda.length () > 0)
      {
         String quepo = (String) getFirstToken (queda);
         // System.out.println ("saco = [" + quepo + "] queda [" + queda.o_str + "]");
         colec.add (quepo);
      }

      // make the string
      //
      String [] reto = new String[colec.size ()];
      for (int ii = 0; ii < colec.size (); ii ++)
         reto[ii] = (String) colec.get(ii);

      /* Como utilizar el metodo toArray ?? ... es igual me ahorraria solo una linea
         reto = new String[colec.size ()];
         reto = (String []) colec.toArray ();
      */

      return reto;
   }

   /**
      pass the content of a String array 'slist' into the Cadena object (this)
      USING ALWAYS! the separator ',' and taking into account the '"' caracters

      @see toStrArray

      <pre>
      Example :

         String [] arr = new String [3];

         arr[0] = "this";
         arr[1] = "is";
         arr[2] = "third,element,and last one";

         Cadena cad = new Cadena ();
         cad.setStrArray (arr);

         System.out.println ("contents of cad [" + cad.o_str + "]"); // [this,is,"third,element,and last one",]
      </pre>
   */
   public boolean setStrArray (String [] slist)
   {
      Cadena aux = new Cadena();

      // more effective way, the whole line is a string
      if (slist.length == 1)
      {
         // we print out the escape column only if not ended with blank!
         // since this end blanks would be erroniously trimmed
         if (!slist[0].endsWith (" ") && !slist[0].endsWith ("\t"))
         {
            o_str = START_LITERAL_2 + slist[0];
            return true;
         }
      }

      // look one per one
      o_str = "";
      StringBuffer ss = new StringBuffer ("");
      for (int ii = 0; ii < slist.length; ii ++)
      {
         if (ii > 0)
            ss.append (ATOM_SEPARATOR);

         aux.setStr (slist[ii]);
         aux.EnpaquetaMe ();
         ss.append (aux.getStr ());
      }
      o_str = ss.toString ();
      return true;
   }

   /**
      pass the content of a "simple string list" that is a string of tokens
      separated by one or more separators (each of size 1) where the token cannot
      content any of the choosen separators.

      Examples :

         String s1 = ("this,is,\"\"third,element,and last one\"\""); //  [this,is,""third,element,and last one""]
         String s2 = ("another:possibility/of this");

         List l1 = simpleToList (s1, ",");
         List l2 = simpleToList (s2, " /:");

         then  l1:
         l1.get (0) => [this]
         l1.get (1) => [is]
         l1.get (2) => [""third]
         l1.get (3) => [element]
         l1.get (4) => [and last one""]

         and l2:
         l2.get (0) => [another]
         l2.get (1) => [possibility]
         l2.get (2) => [of]
         l2.get (3) => [this]
   */
   public static List simpleToList (String simpleStringList, String separator)
   {
      Cadena queda = new Cadena(simpleStringList, separator);
      List colec = new Vector ();

      while (queda.getToken ())
      {
         //System.out.println ("saco = [" + quepo + "] queda [" + queda.o_str + "]");
         colec.add (queda.lastToken ());
      }
      return colec;
   }

   public static List simpleToList (String simpleStringList)
   {
      return simpleToList (simpleStringList, ",");
   }

   /**
      the same as simpleToList but returning a String []
   */
   public static String [] simpleToArray (String simpleStringList, String separator)
   {
      List colec = simpleToList (simpleStringList, separator);

      // make the string array
      //
      String [] reto = new String[colec.size ()];
      for (int ii = 0; ii < colec.size (); ii ++)
         reto[ii] = (String) colec.get(ii);

      /* Como utilizar el metodo toArray ?? ... es igual me ahorraria solo una linea
         reto = new String[colec.size ()];
         reto = (String []) colec.toArray ();
      */

      return reto;
   }

   public static String [] simpleToArray (String simpleStringList)
   {
      return simpleToArray (simpleStringList, ",");
   }


   public static String [] simpleToArray (String [] stringAndSeparator)
   {
      String str = (stringAndSeparator != null && stringAndSeparator.length > 0) ? stringAndSeparator[0]: "";
      String separ = (stringAndSeparator != null && stringAndSeparator.length > 1) ? stringAndSeparator[1]: ",";
      return simpleToArray (str, separ);
   }

   public static String linkStrings (String s1, String s2, String link)
   {
      if (s1.length () == 0) return s2;
      if (s2.length () == 0) return s1;
      return s1 + link + s2;
   }
}
