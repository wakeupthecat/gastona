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
      // see for example https://www.ascii-code.com/
      // for octal code

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

               // added on 2020-03-15

               "\140", "~H",  //  96   140   60    01100000 \   &#96;    Grave accent
               "\300", "~I",  // 192   300   C0    11000000 A\  &#192;  &Agrave;  Latin capital letter A with grave
               "\301", "~J",  // 193   301   C1    11000001 A/  &#193;  &Aacute;  Latin capital letter A with acute
               "\304", "~K",  // 196   304   C4    11000100 A:  &#196;  &Auml; Latin capital letter A with diaeresis
               "\307", "~L",  // 199   307   C7    11000111 Cz  &#199;  &Ccedil;  Latin capital letter C with cedilla
               "\310", "~M",  // 200   310   C8    11001000 E\  &#200;  &Egrave;  Latin capital letter E with grave
               "\311", "~N",  // 201   311   C9    11001001 E/  &#201;  &Eacute;  Latin capital letter E with acute
               "\313", "~O",  // 203   313   CB    11001011 E:  &#203;  &Euml; Latin capital letter E with diaeresis
               "\314", "~P",  // 204   314   CC    11001100 I\  &#204;  &Igrave;  Latin capital letter I with grave
               "\315", "~Q",  // 205   315   CD    11001101 I/  &#205;  &Iacute;  Latin capital letter I with acute
               "\317", "~R",  // 207   317   CF    11001111 I:  &#207;  &Iuml; Latin capital letter I with diaeresis
               "\321", "~S",  // 209   321   D1    11010001 N~  &#209;  &Ntilde;  Latin capital letter N with tilde
               "\322", "~T",  // 210   322   D2    11010010 O\  &#210;  &Ograve;  Latin capital letter O with grave
               "\323", "~U",  // 211   323   D3    11010011 O/  &#211;  &Oacute;  Latin capital letter O with acute
               "\326", "~W",  // 214   326   D6    11010110 O:  &#214;  &Ouml; Latin capital letter O with diaeresis
               "\330", "~X",  // 216   330   D8    11011000 O-  &#216;  &Oslash;  Latin capital letter O with slash
               "\331", "~Y",  // 217   331   D9    11011001 U\  &#217;  &Ugrave;  Latin capital letter U with grave
               "\332", "~Z",  // 218   332   DA    11011010 U/  &#218;  &Uacute;  Latin capital letter U with acute
               "\334", "~a",  // 220   334   DC    11011100 U:  &#220;  &Uuml; Latin capital letter U with diaeresis
               "\337", "~b",  // 223   337   DF    11011111 SS  &#223;  &szlig;   Latin small letter sharp s - ess-zed
               "\340", "~c",  // 224   340   E0    11100000 a\  &#224;  &agrave;  Latin small letter a with grave
               "\341", "~d",  // 225   341   E1    11100001 a/  &#225;  &aacute;  Latin small letter a with acute
               "\344", "~e",  // 228   344   E4    11100100 a:  &#228;  &auml; Latin small letter a with diaeresis
               "\350", "~f",  // 232   350   E8    11101000 e\  &#232;  &egrave;  Latin small letter e with grave
               "\351", "~g",  // 233   351   E9    11101001 e/  &#233;  &eacute;  Latin small letter e with acute
               "\353", "~h",  // 235   353   EB    11101011 e:  &#235;  &euml; Latin small letter e with diaeresis
               "\354", "~i",  // 236   354   EC    11101100 i\  &#236;  &igrave;  Latin small letter i with grave
               "\355", "~j",  // 237   355   ED    11101101 i/  &#237;  &iacute;  Latin small letter i with acute
               "\357", "~k",  // 239   357   EF    11101111 i:  &#239;  &iuml; Latin small letter i with diaeresis
               "\361", "~l",  // 241   361   F1    11110001 n~  &#241;  &ntilde;  Latin small letter n with tilde
               "\362", "~m",  // 242   362   F2    11110010 o\  &#242;  &ograve;  Latin small letter o with grave
               "\363", "~n",  // 243   363   F3    11110011 o/  &#243;  &oacute;  Latin small letter o with acute
               "\364", "~o",  // 244   364   F4    11110100 o^  &#244;  &ocirc;   Latin small letter o with circumflex
               "\366", "~p",  // 246   366   F6    11110110 o:  &#246;  &ouml; Latin small letter o with diaeresis
               "\367", "~q",  // 247   367   F7    11110111 ./. &#247;  &divide;  Division sign
               "\370", "~r",  // 248   370   F8    11111000 o-  &#248;  &oslash;  Latin small letter o with slash
               "\371", "~s",  // 249   371   F9    11111001 u\  &#249;  &ugrave;  Latin small letter u with grave
               "\372", "~t",  // 250   372   FA    11111010 u/  &#250;  &uacute;  Latin small letter u with acute
               "\373", "~u",  // 251   373   FB    11111011 u^  &#251;  &ucirc;   Latin small letter u with circumflex
               "\374", "~v",  // 252   374   FC    11111100 u:  &#252;  &uuml; Latin small letter u with diaeresis
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