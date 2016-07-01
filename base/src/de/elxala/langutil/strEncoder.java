/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2014-2016  Alejandro Xalabarder Aulet

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;


/**
   class strEncoder
   @author Alejandro Xalabarder Aulet
   @date   2014


   NOTA:
      no se puede hacer genérico!!
        - hay que saber construir las regular expressions de encode y decode
        - los mapas no tienen porque ser de un carácter!
        - la regular expresion de decode no es un grupo de un carácter (e.g. ~1 ~2 etc)

*/
public class strEncoder
{
   private Pattern patternEncode = null;
   private Pattern patternDecode = null;

   private Map map4Encode = new TreeMap ();
   private Map map4Decode = new TreeMap ();

   private String strPatEncode = "";
   private String strPatDecode = "";

   private String encodeMark = null;

   public strEncoder ()
   {
   }

   public strEncoder (String encodeMarkStart)
   {
      encodeMark = encodeMarkStart;
   }

   public void clear ()
   {
      patternEncode = null;
      patternDecode = null;
      map4Encode = new TreeMap ();
      map4Decode = new TreeMap ();
      strPatEncode = "";
      strPatDecode = "";
   }

   private String escapeRegExp (String str)
   {
      if (str.equals ("|")) return "[|]";
      if (str.equals ("\\")) return "\\\\";
      if (str.equals ("_")) return "\\x5F";
      if (str.equals ("$")) return "\\$";
      if (str.equals ("^")) return "\\^";
      if (str.equals ("{")) return "\\{";
      if (str.equals ("}")) return "\\}";
      return str;
   }

   public void addStrPairs (String [] pairs)
   {
      if (pairs.length == 0 && pairs.length % 2 != 0)
      {
         System.err.println ("ERROR in strEncoder::addStrPairs incorrect size of array " + pairs.length + ", it has to be an even number");
         return;
      }
      for (int pp = 0; pp < pairs.length; pp += 2)
         addStrPair (pairs[pp], pairs[pp + 1]);
   }

   public void addStrPair (String str, String replace)
   {
      if (strPatEncode.length () > 0)
         strPatEncode += "|";
      strPatEncode += escapeRegExp (str);

      if (strPatDecode.length () > 0)
         strPatDecode += "|";
      strPatDecode += escapeRegExp (replace);

      map4Encode.put (str, replace);
      map4Decode.put (replace, str);
   }

   public String encode (String intro)
   {
      if (patternEncode == null)
      {
         patternEncode = Pattern.compile (strPatEncode);
      }
      return codeDecodeGeneral (true, intro, patternEncode, map4Encode);
   }

   public String decode (String str)
   {
      if (patternDecode == null)
      {
         patternDecode = Pattern.compile (strPatDecode);
      }

      if (str == null || str.length () == 0 || (encodeMark != null && ! str.startsWith (encodeMark)))
         return str;

      if (encodeMark != null)
         str = str.substring (encodeMark.length ());
      return codeDecodeGeneral (false, str, patternDecode, map4Decode);
   }

   protected String codeDecodeGeneral (boolean encode, String strIn, Pattern patt, Map mapStr)
   {
      if (strIn == null) return "";

      StringBuffer buf = new StringBuffer ();
      Matcher matcher = patt.matcher (strIn);

      int vamos = 0;
      while (matcher.find())
      {
         if (buf.length () == 0 && encode && encodeMark != null)
            buf.append (encodeMark);

         buf.append (strIn.substring (vamos, matcher.start()));

         String strReemp = (String) mapStr.get (matcher.group());
         if (strReemp != null)
         {
            buf.append (strReemp);
         }
         else
         {
            System.err.println ("ERROR in strEncoder::" + (encode ? "en":"de") + "code matcher.group() = [" + matcher.group() + "] on [" + buf.toString () + "]");
         }
         vamos = matcher.end();
      }
      buf.append (strIn.substring (vamos));
      return buf.toString ();
   }

   protected static strEncoder encoderHTML = null;
   protected static strEncoder encoderLATEX = null;

   public static strEncoder getHtmlEncoder ()
   {
      if (encoderHTML != null) return encoderHTML;

      encoderHTML = new strEncoder ();

      // date 2014.05.18 16:39
      // NOTE: this has to be edited using UTF-8!
      //
      encoderHTML.addStrPairs (new String [] {
               "&", "&amp;",
               "<", "&lt;",
               ">", "&gt;",
               "\"", "&quot;",
               //" ", "&nbsp;",

               "¦", "&brvbar;",
               "¡", "&iexcl;",
               "°", "&deg;",

               "æ", "&aelig;",
               "ç", "&ccedil;",
               "ñ", "&ntilde;",

               "ý", "&yacute;",
               "ð", "&eth;",
               "þ", "&thorn;",

               "¿", "&iquest;",
               "º", "&ordm;",

               "à", "&agrave;",
               "á", "&aacute;",
               "â", "&acirc;",
               "ä", "&auml;",
               "å", "&aring;",

               "è", "&egrave;",
               "é", "&eacute;",
               "ê", "&ecirc;",
               "ë", "&euml;",

               "ì", "&igrave;",
               "í", "&iacute;",
               "î", "&icirc;",
               "ï", "&iuml;",

               "ò", "&ograve;",
               "ó", "&oacute;",
               "ô", "&ocirc;",
               "ö", "&ouml;",
               "õ", "&otilde;",
               "ø", "&oslash;",

               "ù", "&ugrave;",
               "ú", "&uacute;",
               "û", "&ucirc;",
               "ü", "&uuml;",

              });
      return encoderHTML;
   }

   public static strEncoder getLatexEncoder ()
   {
      if (encoderLATEX != null) return encoderLATEX;

      encoderLATEX = new strEncoder ();
      encoderLATEX.addStrPairs (new String [] {
            "\\", "\\textbackslash",
            "#", "\\#",
            "$", "\\$",
            "%", "\\%",
            "&", "\\&",
            "^", "\\textasciicircum",
            "_", "\\_",
            "~", "\\textasciitilde",
            "<", "$<$",
            ">", "$>$",
            "{", "\\{",
            "}", "\\}"
            });

/*
#listix#

	<head_desc>
		// <Extracts> #the# {schema} of & the data_base (only table & $ % ^structure) )


	<main>
		STRCONV, ESCAPE, LATEX, @<head_desc>

*/

      //no encontrado para � � y �
      return encoderLATEX;
   }
   

   public static StringBuffer xorEncrypt (StringBuffer message, String key)
   {
      return xorEncrypt (message, key, -1, -1, -1);
   }
   
   public static StringBuffer xorEncrypt (StringBuffer message, String key, int off1, int mult, int off2)
   {
      // DO NOT CHANGE THESE VALUES OR OLD ENCRYPTED STUFF WILL BE LOST!!
      off1 = off1 >= 0 ? off1: key.charAt (key.length () / 2) % 37;
      off2 = off2 >= 0 ? off2: key.charAt (key.length () / 3) % 7;
      mult = mult >= 0 ? mult: key.charAt (key.length () / 5) % 13;

      StringBuffer sal = new StringBuffer ();
      int lon = key.length ();
      for (int ii = 0; ii < message.length (); ii ++)
      {
         sal.append ((char) (message.charAt (ii) ^ (char) ((key.charAt ((ii + off1) % lon) + mult * ii + off2) % 256)));
      }
      return sal;
   }
   
}
