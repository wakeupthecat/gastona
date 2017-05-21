/*
library de.elxala
Copyright (C) 2005-2017 Alejandro Xalabarder Aulet

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

import java.util.List;
import de.elxala.langutil.*;

/**
*/
public class utilEscapeStr
{
   private static final char SEPARATOR = utilEscapeConfig.SEPARATOR;

   private static String ENCODE_MODEL_NAME = utilEscapeConfig.ENCODE_MODEL_NAME;

   public static void setEscapeModel (String encodeName)
   {
      ENCODE_MODEL_NAME = encodeName;
   }

   private static strEncoder encoderClassicNativ = null;


   private static String [][] mapaEscapa =
   {
//     { "\~",   "~1"},  // A ~ character.
      { "~",   "~1"},   // A ~ character.
      { "|",   "~2"},   // A | character.
      { "\"",  "~3"},   // A " character.
      { "'",   "~4"},   // A ' character.
      { "\r",  "~5"},   // \r A carriage return character.
      { "\n",  "~6"},   // \n A newline character.
      { "\t",  "~7"},   // \t A tab character.
      { "%",   "~8"},   // % character.
      { "\000","~A"},   // ascci 0
      { "\007","~B"},   // ascci 7 (audible bell)
      { "\010","~C"},   // ascci 8 (backspace = \b)
      { "\013","~D"},   // ascci 11 (vertical tab)
      { "\014","~E"},   // ascci 12 (form feed = \f)
      { "\032","~F"},   // ascci 26 (end of file)
      { "\033","~G"},   // ascci 27 (escape)

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
      { "~A", "\000"},   // ascci 0
      { "~B", "\007"},   // ascci 7 (audible bell)
      { "~C", "\010"},   // ascci 8 (backspace = \b)
      { "~D", "\013"},   // ascci 11 (vertical tab)
      { "~E", "\014"},   // ascci 12 (form feed = \f)
      { "~F", "\032"},   // ascci 26 (end of file)
      { "~G", "\033"},   // ascci 27 (escape)
   };

   public static String escapeStrTruncate (String str, int bytesLimit)
   {
      if (str == null) return "null"; // or nothing ?, I think is better this

      boolean truncated = str.length() > bytesLimit;

      if (str.length() > bytesLimit)
         return gastNativeEscape (str.substring (0, bytesLimit)) + " **TRUNCATED!! TOTAL SIZE WAS " + str.length () + "**";

      return gastNativeEscape (str);
   }

   private static strEncoder getNewEncoderNativ ()
   {
      if (encoderClassicNativ == null)
      {
         encoderClassicNativ = new strEncoder ("~");
         encoderClassicNativ.addStrPairs  (new String [] {
               "~",   "~1",   // A ~ character.
               "|",   "~2",   // A | character.
               "\"",  "~3",   // A " character.
               "'",   "~4",   // A ' character.
               "\r",  "~5",   // \r A carriage return character.
               "\n",  "~6",   // \n A newline character.
               "\t",  "~7",   // \t A tab character.
               "%",   "~8",   // % character.
               "\000","~A",   // ascci 0
               "\007","~B",   // ascci 7 (audible bell)
               "\010","~C",   // ascci 8 (backspace = \b)
               "\013","~D",   // ascci 11 (vertical tab)
               "\014","~E",   // ascci 12 (form feed = \f)
               "\032","~F",   // ascci 26 (end of file)
               "\033","~G",   // ascci 27 (escape)
            });
      }
      return encoderClassicNativ;
   }

   private static String gastNativeEscape (String str)
   {
      return getNewEncoderNativ ().encode (str);
   }

   private static String gastNativeUnescape (String str)
   {
      return getNewEncoderNativ ().decode (str);
   }

   private static String OLD_gastNativeEscape (String str)
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

   private static String OLD_gastNativeUnescape (String str)
   {
      // if some it hasn't been scaped then return it as it is
      if (str.length () == 0 || str.charAt (0) != '~')
         return str;

      Cadena cad = new Cadena (str.substring (1));
      cad.replaceMeOnce (mapaDesEscapa);
      return cad.o_str;
   }


   public static String escapeStr (String str)
   {
      return escapeStr (str, ENCODE_MODEL_NAME);
   }

   public static String escapeStr (String str, String escapeModel)
   {
      //System.out.println ("escapa con [" + escapeModel + "]");
      if (escapeModel.length () > 0)
      {
         String result = str;
         try
         {
            result = java.net.URLEncoder.encode (str, escapeModel);
         }
         catch (Exception e) { }

         return result;
      }
      return gastNativeEscape (str);
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
      return desEscapeStr (str, ENCODE_MODEL_NAME);
   }

   public static String desEscapeStr (String str, String escapeModel)
   {
      //System.out.println ("descapa con [" + escapeModel + "]");
      if (escapeModel.length () > 0)
      {
         String result = str;
         try
         {
            result = java.net.URLDecoder.decode (str, escapeModel);
         }
         catch (Exception e) { }
         return result;
      }
      return gastNativeUnescape (str);
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
         ss.append (escapeStr (strArray [ii]) + SEPARATOR);
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

      String [] columnas = campsStr.split (strUtil.stringToRegexStr ("" + SEPARATOR));
      if (columnas.length - offset <= 0)
         return new String [0];
      String [] mat = new String [columnas.length - offset];

      if (offset >= 0 && offset < columnas.length)
         for (int cc = offset; cc < columnas.length; cc ++)
            mat[cc-offset] = desEscapeStr (columnas[cc]);

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