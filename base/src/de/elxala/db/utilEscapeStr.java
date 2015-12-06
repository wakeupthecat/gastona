/*
library de.elxala
Copyright (C) 2005 Alejandro Xalabarder Aulet

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/

package de.elxala.db;

import java.util.Vector;
import de.elxala.langutil.*;

/**
*/
public class utilEscapeStr
{
   public static final String SEPARATOR = "|";

   private static String [][] mapaEscapa =
   {
      { "~",   "~1"},   // A ~ character.
      { "|",   "~2"},   // A | character.
      { "\"",  "~3"},   // A " character.
      { "'",   "~4"},   // A ' character.
      { "\r",  "~5"},   // \r A carriage return character.
      { "\n",  "~6"},   // \n A newline character.
      { "\t",  "~7"},   // \t A tab character.
      { "%",   "~8"},   // % character.
   };

   private static String [][] mapaDesEscapa =
   {
      { "~1", "~"},   // A ~ character.
      { "~2", "|"},   // A | character.
      { "~3", "\""},  // A double quote " character.
      { "~4", "'"},   // A ' character.
      { "~5", "\r"},  // \r A carriage return character.
      { "~6", "\n"},  // \n A newline character.
      { "~7", "\t"},  // \t A tab character.
      { "~8", "%"},   // % character.
   };

   /**
   */
   public static String escapeStr222 (String str)
   {
      Cadena cad = new Cadena (str);
      if (cad.replaceMeOnce (mapaEscapa) > 0)
      {
         // if some character is scaped the whole string begins with ~
         return "~" + cad.o_str;
      }
      else
      {
         // not altered
         return str;
      }
   }

   public static String escapeStrTruncate (String str, int bytesLimit)
   {
      if (str == null) return "null"; // or nothing ?, I think is better this

      boolean truncated = str.length() > bytesLimit;
      Cadena cad = new Cadena (truncated ? str.substring (0, bytesLimit): str);
      if (cad.replaceMeOnce (mapaEscapa) > 0)
      {
         // if some character is scaped the whole string begins with ~
         if (! truncated)
            return "~" + cad.o_str;
         return "~" + cad.o_str + " **TRUNCATED!! TOTAL SIZE WAS " + str.length () + "**";
      }
      else
      {
         // not altered
         return str;
      }
   }

   /**
   */
   public static String escapeStr (String str)
   {
      Cadena cad = new Cadena (str);
      if (cad.replaceMeOnce (mapaEscapa) > 0)
      {
         // if some character is scaped the whole string begins with ~
         return "~" + cad.o_str;
      }
      else
      {
         // not altered
         return str;
      }
   }

   public static String escapeStrArray (String [] textstring)
   {
      StringBuffer sbuff = new StringBuffer ();
      for (int ii = 0; ii < textstring.length; ii ++)
      {
         sbuff.append ((ii > 0 ? "\n": "") + textstring[ii]);
      }

      return escapeStr (sbuff.toString ());
   }

   public static String desEscapeStr (String str)
   {
      // if some it hasn't been scaped then return it as it is
      if (str.length () == 0 || str.charAt (0) != '~')
         return str;

      Cadena cad = new Cadena (str.substring (1));
      cad.replaceMeOnce (mapaDesEscapa);
      return cad.o_str;
   }

   /**
      converts an escaped String into a text returned as an String array
   */
   public static String [] desEscapeStrToArray (String str)
   {
      // if some it hasn't been scaped then return it as it is
      if (str.length () == 0 || str.charAt (0) != '~')
      {
         return new String [] { str };
      }

      String [] lines = str.split ("~6|~5");
      for (int ii = 0; ii < lines.length; ii ++)
      {
         // since the whole string is scaped (start with ~)
         // we indicate it to the splited strings except the first one
         //
         lines [ii] = desEscapeStr ((ii > 0 ? "~": "") + lines [ii]);
      }
      return lines;
   }

   public static String compactRow (String [] strArray)
   {
      if (strArray == null) return "";
      StringBuffer ss = new StringBuffer ();
      for (int ii = 0; ii < strArray.length; ii ++)
      {
         ss.append (escapeStr (strArray [ii]));
         ss.append (SEPARATOR);
      }
      return ss.toString ();
   }


   public static String [] expandRow (String campsStr)
   {
      return expandRow (campsStr, 0);
   }

   // 29.05.2009
   // Note : the parameter offset is to ignore initial columns (see rowViewTableModel::loadRowsFromOffset function)
   public static String [] expandRow (String campsStr, int offset)
   {
      if (campsStr == null || campsStr.length() == 0)
         return new String[1];

      // System.out.println ("la row es = " + row + " el tamanyo en si es " + ptrStr.length);
      Cadena c = new Cadena (campsStr);
      c.setSepar (SEPARATOR);

      Vector columnas = new Vector ();
      while (c.getToken ())
         if (offset -- <= 0)
            columnas.add (c.lastToken ());

      String [] mat = new String [columnas.size ()];
      for (int cc = 0; cc < columnas.size (); cc ++)
         mat[cc] = desEscapeStr ((String) columnas.get (cc));

      return mat;
   }

   public static void main (String args[])
   {
      String [] testM = new String [3];

      testM[0] = "esto es un\t texto | surtidito\ncon varias % lineas y \"~destructores\"";
      testM[1] = "probando ~~11111 y otros";
      testM[2] = "bueno adios";

      for (int ii = 0; ii < testM.length; ii ++)
      {
         System.out.println ("str [" + ii + "] [" + testM[ii] + "] escaped = [" + escapeStr(testM[ii]) + "]");
         System.out.println ("str [" + ii + "] [" + desEscapeStr (escapeStr(testM[ii])) + "] (restored)");
      }

      String enUno = compactRow (testM);
      System.out.println ("todo en uno [" + enUno + "]");

      String [] otrez = expandRow (compactRow (testM));

      System.out.print ("test : ");
      System.out.print ((otrez.length == testM.length) ? ".": "!wrong!");
      System.out.print ((otrez[0].equals (testM[0])) ? ".": "!wrong!");
      System.out.print ((otrez[1].equals (testM[1])) ? ".": "!wrong!");
      System.out.print ((otrez[2].equals (testM[2])) ? ".": "!wrong!");
   }
}

/**
Conversion routines for DOORS (dxl language)


//      { "~",   "~1"},   // A ~ character.
//      { "|",   "~2"},   // A | character.
//      { "\"",  "~3"},   // A " character.
//      { "'",   "~4"},   // A ' character.
//      { "\r",  "~5"},   // \r A carriage return character.
//      { "\n",  "~6"},   // \n A newline character.
//      { "\t",  "~7"},   // \t A tab character.
//      { "%",   "~8"},   // % character.
//      { "\f",  "~9"},   // \f A form feed character.



string convertText2DBstr (string text)
{
   int tok = 0
   int off = 0
   int len = 0
   string resto = text
   string novo = ""

   // first once substitution
   while (findPlainText(resto, "~", off, len, true, false))
   {
      novo = novo resto[0:off-1] "~1"
      resto = resto[off+len:]
      tok ++
   }
   novo = novo resto

   while (findPlainText(novo, "|", off, len, true, false))  { novo = novo[0:off-1] "~2" novo[off+len:];  tok ++  }
   while (findPlainText(novo, "\"", off, len, true, false)) { novo = novo[0:off-1] "~3" novo[off+len:];  tok ++  }
   while (findPlainText(novo, "'", off, len, true, false)) { novo = novo[0:off-1] "~4" novo[off+len:];  tok ++  }
   while (findPlainText(novo, "\n", off, len, true, false)) { novo = novo[0:off-1] "~6" novo[off+len:];  tok ++  }
   while (findPlainText(novo, "\r", off, len, true, false)) { novo = novo[0:off-1] "~5" novo[off+len:];  tok ++  }
   while (findPlainText(novo, "\f", off, len, true, false)) { novo = novo[0:off-1] "~9" novo[off+len:];  tok ++  }
   while (findPlainText(novo, "\t", off, len, true, false)) { novo = novo[0:off-1] "~7" novo[off+len:];  tok ++  }
   while (findPlainText(novo, "%", off, len, true, false))  { novo = novo[0:off-1] "~8" novo[off+len:];  tok ++ }

   if (tok > 0)
      return "~" novo
   else return text
}

string convertDBstr2Text (string DBstr)
{
   if (DBstr[0:0] != "~") return DBstr;

   int tok = 0
   int off = 0
   int len = 0
   string novo = DBstr[1:]

   while (findPlainText(novo, "~9", off, len, true, false))  novo = novo[0:off-1] "\f" novo[off+len:]
   while (findPlainText(novo, "~8", off, len, true, false))  novo = novo[0:off-1] "%" novo[off+len:]
   while (findPlainText(novo, "~7", off, len, true, false))  novo = novo[0:off-1] "\t" novo[off+len:]
   while (findPlainText(novo, "~6", off, len, true, false))  novo = novo[0:off-1] "\n" novo[off+len:]
   while (findPlainText(novo, "~5", off, len, true, false))  novo = novo[0:off-1] "\r" novo[off+len:]
   while (findPlainText(novo, "~4", off, len, true, false))  novo = novo[0:off-1] "'" novo[off+len:]
   while (findPlainText(novo, "~3", off, len, true, false))  novo = novo[0:off-1] "\"" novo[off+len:]
   while (findPlainText(novo, "~2", off, len, true, false))  novo = novo[0:off-1] "|" novo[off+len:]
   while (findPlainText(novo, "~1", off, len, true, false))  novo = novo[0:off-1] "~" novo[off+len:]

   return novo
}

void testDBstrConversion ()
{
   string origa = "~musaka~que\"relativa\" la 'macha'\nPanacorta\tRota"
   string sousa = convertText2DBstr (origa)

   print sousa "\n"
   print convertDBstr2Text (sousa) "\n"
}

*/