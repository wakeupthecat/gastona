/*
library listix (www.listix.org)
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


/*
   //(o) WelcomeGastona_source_listix_command STRCONV

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    listix_command
   <name>       STRCONV
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdStrconvert
   <importance> 3
   <desc>       //String and Eva variable conversions

   <help>
      //
      // A set of string, numeric, date and Eva variable conversions. Some convertions accept a
      // variable name where to place the result in and all of them can print out the result onto the
      // current Listix target (standard output, console, file or variable). Even if the conversion
      // does not have an eva variable where to put the result in, it is always possible to do that,
      // for example
      //
      //    <xyz>
      //       SET VAR, myResult, @<upperStr>
      //
      //    <upperStr> STRCONV, UPPER, "Convert this to upper case"
      //
      // Standard conversions
      // ----------------------
      //
      // The syntaxes SUBSTR, UPPER, UPPER1, LOWER, DATE-LONG, LONG-DATE, NUM-FIX, NUM-ENG are general
      // and typical data and number conversions. Note that there are also ways of using more standard
      // conversion provided by sqlite. For example to get the data a week ago
      //
      //    <main> //A week ago it was @<today-7>
      //
      //    <today-7>
      //       LOOP, SQL,, //SELECT date('now', '-7 day') AS value;
      //       @<value>
      //
      // in this example we have used "LOOP, SQL" but note that the select use no database or table at all
      // For more sqlite functions try the links
      //       http://sqlite.org/lang_corefunc.html
      //       http://sqlite.org/lang_datefunc.html
      //
      //
      // Other conversions
      // ----------------------
      //
      // The syntaxes EVA-TEXT, TEXT-EVA, GEN EVA work with Eva variables.
      // Finally STR-TEXT, STRE-TEXT and TEXT-STR are special conversions Text to string
      // and viceversa where new lines together with other few characters like ', % are coded resulting
      // a string with no one of these special characters. These conversions are used internally in Gastona
      // native databases (sqlite) for texts and it happens transparently, nevertheless it might be
      // useful to perform them manually in some kind of applications.
      //

   <aliases>
      alias
      STR
      STRCONVERT


   <syntaxHeader>
      synIndx, importance, desc
         1   ,   3       , //Returns a substring of 'string' starting on 'offset' and of size 'length'
         2   ,   3       , //Returns the upper case of 'string'
         3   ,   3       , //Returns the 'string' with the first character upper cased
         4   ,   3       , //Returns the lower case of 'string'
         5   ,   3       , //Converts 'stringSource' into a text, the result will be placed in the variable 'EvaTarget' if given
         6   ,   3       , //Converts the string contained in the variable 'EvaSource' into a text, the result will be placed in the variable 'EvaTarget' if given. Note that the source string is treated simply as string and no listix "@" replacement is done, in oder to use this replacements use "GEN EVA" instead of STRE-TEXT
         7   ,   3       , //Converts the text given in 'EvaSource' into a string, the result is printed out into the current file or standard output or place into the Eva 'EvaTarget' if given
         8   ,   3       , //Converts the string UTC date 'dateAsString' (greater than 1970-01-01) into a long value
         9   ,   3       , //Converts the UTC date 'milisecSince1970' given as long into a string date
        10   ,   3       , //Formats 'number' to a fix point of 'nDecimals'
        11   ,   3       , //Formats 'number' to a typical engineer format
        12   ,   3       , //Converts the Eva variable 'EvaSource' into a text placing it into 'EvaTarget'
        13   ,   3       , //Parses an eva varible given as text in 'EvaSource' placing the result into 'EvaTarget'
        14   ,   3       , //generates a text into EvaTarget from the listix format given in EvaSource. Note that if there are listix "@" variables in the source text the listix replacement will be performed.
        15   ,   3       , //Converts a string into a text to be inserted in html (mainly convert < > & and ")
        16   ,   3       , //Converts a html text into a string (e.g. "&lt;" will be converted to <)
        17   ,   3       , //Solve whenever possible the relative path symbols (. and ..) from a path
        18   ,   3       , //Concat to paths using the native directory separator if needed
        19   ,   3       , //Returns the canonical path from a one given (see java )
        20   ,   3       , //Converts a text into a valid name for a variable, file name, sql column name etc (i.e. ISO 9660 Joliet compliant)
        21   ,   3       , //Encode a string using a encoding model (e.g. UTF-8)
        22   ,   3       , //Encode a text from a given variable using a encoding model (e.g. UTF-8)
        23   ,   3       , //Encode a text from file using a encoding model (e.g. UTF-8)
        24   ,   3       , //Decode a string using a encoding model (e.g. UTF-8)
        25   ,   3       , //Decode the content of a variable using a encoding model (e.g. UTF-8)
        26   ,   3       , //Escape a string using a escape style (supported HTML and LATEX)
        27   ,   3       , //Unescape a string using a escape style (supported HTML and LATEX)
        28   ,   3       , //Xor encryption with shift mechanism
        29   ,   3       , //Generate java script code for painting a specific scence where using "trazos"
        30   ,   3       , //Saves into a file an image given by paths

   <syntaxParams>
      synIndx, name         , defVal, desc
      1      , SUBSTR       ,       , //
      1      , string       ,       , //String source
      1      , offset       ,       , //zero based offset position start of the resulting substring to obtain
      1      , length       ,       , //maximal length of the desired substring

      2      , UPPER        ,       , //
      2      , string       ,       , //String to convert into upper case

      3      , UPPER1       ,       , //
      3      , string       ,       , //String which first charater will be upper cased

      4      , LOWER        ,       , //
      4      , string       ,       , //String to convert into lower case

      5      , STR-TEXT     ,       , //
      5      , source       ,       , //Coded text string to be converted into a text onto an Eva variable
      5      , [EvaTarget]  ,       , //If given then the result will be written into the specified Eva

      6      , STRE-TEXT    ,       , //
      6      , EvaSource    ,       , //Eva name that contains the string to be converted
      6      , [EvaTarget]  ,       , //If given then the result will be written into the specified Eva

      7      , TEXT-STR     ,        , //
      7      , EvaSource    ,        , //Eva name containing the text to convert into a string text
      7      , [EvaTarget]  ,        , //If given then the result will be write into the specified Eva

      8      , DATE-LONG     ,    , //
      8      , dateAsString  ,    , //Date given in the format "yyyy-MM-dd HH:mm:ss S" (year-month-day hour:minute:second milliseconds)

      9      , LONG-DATE           ,     , //
      9      , milisecSince1970    ,     , //Date given as a long representing the milliseconds from 1-Jan-1970

      10     , NUM-FIX          ,      , //
      10     , number           ,      , //Number to convert
      10     , nDecimals        ,      , //Decimal positions to get

      11     , NUM-ENG          ,      , //
      11     , number           ,      , //Number to convert

      12     , EVA-TEXT         ,      , //
      12     , EvaSource        ,      , //Name of Eva variable containing to be converted to text
      12     , EvaTarget        ,      , //Name of Eva variable where the text will be placed

      13     , TEXT-EVA         ,      , //
      13     , EvaSource        ,      , //Name of Eva variable containing the text to be parsed as Eva
      13     , EvaTarget        ,      , //Name of Eva variable to place the parsed text

      14     , GEN EVA          ,      , //
      14     , EvaTarget        ,      , //Name of Eva variable where to place the generation
      14     , EvaSource        ,      , //Name of Eva variable containing the listix format to generate

      15     , TEXT-HTMLTEXT    ,      , //
      15     , text             ,      , //Text to convert into Html text (e.g. "<" to "&lt;")

      16     , HTMLTEXT-TEXT    ,      , //
      16     , htmlText         ,      , //Html text to convert into normal text (e.g. "&lt;" to "<")

      17     , PATH-SOLVE-POINTS,      , //
      17     , filePath         ,      , //Path to be solved

      18     , PATH-CONCAT      ,      , //
      18     , filePath1        ,      , //Firts part of the path to concatenate
      18     , filePath2        ,      , //Second part of the path to concatenate

      19     , PATH-CANONICAL   ,      , //
      19     , filePath         ,      , //Path to be converted into canonical path

      20     , TEXT-VARNAME     ,      , //
      20     , text             ,      , //text to be converted

      21     , ENCODE STR       ,       , //
      21     , encodingModel    ,       , //Encoding model, for example UTF-8 or ISO-8859-1, the value * will use the current encoding model (see DB CONFIG command), no value will use the intern encoding model
      21     , stringToEncode   ,       , //String to be encoded

      22     , ENCODE VAR       ,       , //
      22     , encodingModel    ,       , //Encoding model, for example UTF-8 or ISO-8859-1, the value * will use the current encoding model (see DB CONFIG command), no value will use the intern encoding model
      22     , evaSource        ,       , //Name of Eva variable containing the text to be encoded

      23     , ENCODE FILE      ,       , //
      23     , encodingModel    ,       , //Encoding model, for example UTF-8 or ISO-8859-1, the value * will use the current encoding model (see DB CONFIG command), no value will use the intern encoding model
      23     , fileName         ,       , //Name of file containing the text to be encoded

      24     , DECODE STR       ,       , //
      24     , encodingModel    ,       , //Encoding model, for example UTF-8 or ISO-8859-1, the value * will use the current encoding model (see DB CONFIG command), no value will use the intern encoding model
      24     , stringToDecode   ,       , //String to be decoded

      25     , DECODE VAR       ,       , //
      25     , encodingModel    ,       , //Encoding model, for example UTF-8 or ISO-8859-1, the value * will use the current encoding model (see DB CONFIG command), no value will use the intern encoding model
      25     , evaSource        ,       , //Name of Eva variable containing the text to be decoded

      26     , ESCAPE STR       ,       , //
      26     , escapeStyle      ,       , //Supported escape styles HTML and LATEX, for URL use ENCODE STR, for exaple "ENCODE STR, UTF-8"
      26     , stringToEscape   ,       , //String to be escaped

      27     , UNESCAPE STR    ,       , //
      27     , escapeStyle     ,       , //Supported escape styles HTML and LATEX, for URL use ENCODE STR, for exaple "ENCODE STR, UTF-8"
      27     , stringToUnescape,       , //String to be unescaped

      28     , XOR ENCRYPT     ,       , //
      28     , keyString       ,       , //String containig the key
      28     , offset1         , 0     , //Offset used to shift the key
      28     , mult            , 0     , //Multiplier to shift the key
      28     , offset2         , 0     , //Second offset used to shift the key

      29     , TRAZOS-JS       ,       , //
      29     , evaName         ,       , //Variable name (eva) containing the trazos
      29     , useLib          , 1     , //If 1 it is assumed that the library META-GASTONA/js/trazos2D.js is included
      29     , canvasX         , 0     , //If > 0 width of canvas where the image has to fit
      29     , canvasY         , 0     , //If > 0 height of canvas where the image has to fit
      29     , offset0         , 0     , //If 1 then clear the offsets to 0, 0

      
      30     , 2DPATHS-FILE    ,       , //
      30     , evaName         ,       , //Variable name (eva) containing the 2d paths
      30     , imageFilename   , 1     , //Target file name for image
      30     , fileType        , 0     , //File type, default is "png"
      30     , sizeX           , 0     , //If > 0 sizeX (width) for the final image
      30     , sizeY           , 0     , //If > 0 sizeY (height) for the final image

<! XOR ENCRYPT, KEY, offset1, mult, offset2
<!            , IN FILE KEY,
<!            , IN STR, asdkljflfskdj ldksjlskd
<!            , IN VAR, lalala
<!            , IN FILE, c:\temparito.txt
<!            , OUT VAR, lololo
<!            , OUT FILE, :mem encripa



   <options>
      synIndx, optionName  , parameters, defVal, desc
      22     , SOLVE VAR   , 1 / 0   ,  1, //Set to 1 if want to solve variables, e.g. @<myvar> or set to 0 to treat it as literal
      23     , SOLVE VAR   , 1 / 0   ,  1, //Set to 1 if want to solve variables, e.g. @<myvar> or set to 0 to treat it as literal
      25     , SOLVE VAR   , 1 / 0   ,  1, //Set to 1 if want to solve variables, e.g. @<myvar> or set to 0 to treat it as literal

      28     , *IN FILE KEY, filename, , //NOT IMPLEMENTED!  file to be used as key
      28     , IN STR      , string  , , //Input string with the message to be en/de-crypted
      28     , IN VAR      , variable name  , , //Input variable name containing the message to be en/de-crypted
      28     , *IN FILE     , file name      , , //NOT IMPLEMENTED! Input file name containing the message to be en/de-crypted
      28     , *OUT VAR     , variable name  , , //NOT IMPLEMENTED! Variable to put the output
      28     , *OUT FILE    , file name      , , //NOT IMPLEMENTED! File name to put the output

   <examples>
      gastSample
      STRCONV basicSample
      Listix interactive sample
      caballoJs


   <STRCONV basicSample>
      // #javaj#
      //
      //    <frames> oConsole, STRCONV Basic sample
      //
      // #listix#
      //
      //    <main0>
      //       //Substring of ABCDE 3, 2 is "
      //       STRCONV, SUBSTR, "ABCDEF", 3, 2
      //       //"
      //       //
      //       //Upper of AbcDef is "
      //       STRCONV, UPPER, "AbcDef"
      //       //"
      //       //
      //       //Upper1 of abcdef is "
      //       STRCONV, UPPER1, "abcdef"
      //       //"
      //       //
      //       //The text ["Hola" <tio & Cia>] in html ... [
      //       STRCONV, TEXT-HTMLTEXT, //"Hola" <tio & Cia>
      //       //]
      //       //
      //       SETVAR, ahora, @<:lsx date yyyy-MM-dd HH:mm:ss S>
      //       //Now is @<ahora>
      //       //
      //       //Milliseconds since 1970 =
      //       STRCONV, DATE-LONG, @<ahora>
      //       //
      //       //A week ago it was = (@<aweek ago>) ms since 1970 =
      //       STRCONV, LONG-DATE, @<aweek ago>
      //       //
      //       //Normalizing a path =
      //       STRCONV, PATH-SOLVE, '/firsr/second/./ignorethis/removeit/../.././third/./final/byedir/..
      //       //
      //       //Building a valid file name from a text =
      //       STRCONV, TEXT-VARNAME, 'This is%20%NOT a valid (��) file name, is it?
      //
      //   <aweek ago>  =, along - 1000*3600*24*7
      //
      //   <along>  STRCONV, DATE-LONG, @<ahora>

   <Listix interactive sample>
      //#javaj#
      //
      //   <frames> Fmain, Convert Text to Evas sample
      //
      //   <layout of Fmain>
      //    EVA, 10, 10, 7, 7
      //
      //       , X
      //       , lListix interactiv
      //     X , xText
      //       , bRun
      //     X , oConsole
      //
      //    <sysDefaultFonts>
      //       Consolas, 15, 0, TextArea
      //
      //#data#
      //
      //   <xText>
      //      //LOOP, FOR, ii, 1, 3
      //      //    ,, //Hola @<:sys user.name>, que tal andas
      //
      //#listix#
      //
      //   <-- bRun>
      //      LSX, convert text into eva
      //      LSX. launch new format
      //
      //   <convert text into eva> STRCONV, TEXT-EVA, xText, my new format
      //   <launch new format>     LSX, my new format

   <caballoJs>
      //#javaj#
      //
      //   <frames> main, Converting trazos to javascript canvas
      //
      //   <layout of main>
      //    EVA, 10, 10, 7, 7
      //
      //       , X
      //       , lTrazos
      //     X , xTrazos
      //       , bRun
      //     X , oConsole
      //
      //    <sysDefaultFonts>
      //       Consolas, 14, 0, TextArea
      //
      //#data#
      //
      //   <xTrazos>
      //       //defstyle, piel, "fc:+255127039"
      //       //defstyle, pelo, "fc:+234234234"
      //       //
      //       //z ,238, 121, "piel", //jau,84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81
      //       //z ,196, 223, "piel", //jau,-43,-81,-10,-36,9,-19,39,8,64,37
      //       //z ,155,  84, "piel", //jau,-47,7,-34,48,-16,29,20,19,36,-29,40,-15
      //       //z ,468, 148, "piel", //jau,26,22,14,27,1,46,-5,49,12,56,-12,73,-7,33,-25,0,13,-32,-10,-93,-45,-57,-16,-49
      //       //z ,196, 213, "piel", //jau,4,52,29,42,18,65,0,54,-12,28,10,4,18,-1,-2,-18,11,-18,-4,-85,5,-86
      //       //z ,473, 152, "pelo", //jau,51,14,23,59,8,86,-10,0,-3,27,-12,-28,5,38,-11,-2,-6,-37,5,-80,-11,-45,-19,-14
      //       //z ,128,  83, "piel", //jau,-22,-23,3,31
      //       //z ,123,  83, "pelo", //jau,44,-3,65,19,28,27,30,25,-20,19,6,13,-15,-3,-7,-26,-13,7,-17,-24,-28,-28,-21,4,-23,-20,-31,-9
      //
      //#listix#
      //
      //   <-- bRun>
      //      MSG, oConsole clear
      //      STRCONV, TEXT-EVA, xTrazos, varTrazos
      //      STRCONV, TRAZOS-JS, varTrazos, 0, 1000, 1000, 1
      //

#**FIN_EVA#
*/

package listix.cmds;

import java.io.File;
import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.DateFormat;
import de.elxala.db.*;

import java.awt.image.*;
import de.elxala.langutil.graph.*;

public class cmdStrconvert implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "STRCONV",
          "STR",
          "STRCONVERT",
       };
   }

   protected strEncoder encoderHTML = null;
   protected strEncoder encoderLATEX = null;

   protected strEncoder getHtmlEncoder ()
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

   protected strEncoder getLatexEncoder ()
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



   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      //
      // "STRCONV", "SUBSTR", "string", "offset", "length" } ));
      // "STRCONV", "UPPER",  "string"  } ));
      // "STRCONV", "UPPER1", "string"  } ));
      // "STRCONV", "LOWER",  "string"  } ));
      // "STRCONV", "STR-TEXT",  "string", "EvaVarName"  } ));
      // "STRCONV", "TEXT-STR",  "EvaVarName", "string"  } ));
      // "STRCONV", "DATE-LONG", "dateYYYYMMDD_HH_MM_SS_MLS"  } ));
      // "STRCONV", "LONG-DATE", "milisecSince1970"  } ));
      // "STRCONV", "NUM-FIX", "number", "nDecimals"  } ));
      // "STRCONV", "NUM-ENG", "string"  } ));
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String oper = cmd.getArg(0).toUpperCase ();
      String p1   = cmd.getArg(1);
      String p2   = cmd.getArg(2);
      String p3   = cmd.getArg(3);
      String strResult = "";

      boolean OptSolveVar = ("1".equals (cmd.takeOptionString(new String [] {"SOLVE", "SOLVEVAR", "SOLVELSX", "SOLVELISTIX" }, "1"))) &&
                            ("0".equals (cmd.takeOptionString(new String [] {"ASTEXT" }, "0")));

      if (oper.equals ("SUBSTR"))
      {
         int offset0 = Math.max (stdlib.atoi (p2)-1, 0);
         int lenmax  = Math.min (stdlib.atoi (p3), p1.length () - offset0);
         if (lenmax <= 0) lenmax = p1.length() - offset0;

         that.log().dbg(2, "STRCONV", "SUBSTR offset0 = " + offset0 + " lenmax = " + lenmax);

         if (offset0 >= p1.length ())
              strResult = "";
         else strResult = p1.substring (offset0, offset0 + lenmax);  //remember String.substring (index1, index2+1) !!
      }
      else if (oper.equals ("UPPER"))
         strResult = p1.toUpperCase ();
      else if (oper.equals ("UPPER1") && p1.length() > 0)
      {
         strResult = p1.substring (0,1).toUpperCase() + p1.substring (1);
      }
      else if (oper.equals ("LOWER"))
         strResult = p1.toLowerCase ();
      else if (oper.equals ("ENCODE STR") || oper.equals ("ENCODE"))
      {
         // strconv, encode str, utf-8, bla bla
         if (p1.equals ("*"))
              strResult = utilEscapeStr.escapeStr (p2);
         else strResult = utilEscapeStr.escapeStr (p2, p1);
      }
      else if (oper.equals ("ENCODE VAR") || oper.equals ("ENCODE EVA"))
      {
         // strconv, encode var, utf-8, bla bla

         Eva srcEva = that.getReadVarEva (p2);
         if (srcEva == null)
         {
            that.log().err ("ENCODE VAR", "Variable \"" + p2 + "\" could not be found!");
            return 1;
         }
         StringBuffer str = new StringBuffer ();
         for (int rr = 0; rr < srcEva.rows (); rr ++)
         {
            //(o) TODO document this behaviour (if variable contain more rows, columns ... etc)
            str.append (rr != 0 ? "\n":  "");
            if (OptSolveVar)
                 str.append (that.solveStrAsString (srcEva.getValue (rr, 0)));
            else str.append (srcEva.getValue (rr, 0));
         }

         if (p1.equals ("*"))
              strResult = utilEscapeStr.escapeStr (str.toString ());
         else strResult = utilEscapeStr.escapeStr (str.toString (), p1);
      }
      else if (oper.equals ("ENCODE FILE"))
      {
         // strconv, encode var, utf-8, bla bla
         TextFile fix = new TextFile ();

         //(o) TODO check if possible, and if it would make sense, to treat the file as binary
         if (!fix.fopen (p2, "r"))
         {
            that.log().err ("ENCODE FILE", "File \"" + p2 + "\" could not be opened!");
            fix.fclose ();
            return 1;
         }
         StringBuffer str = new StringBuffer ();
         while (fix.readLine ())
         {
            str.append (str.length () == 0 ? "" : "\n");
            if (OptSolveVar)
                 str.append (that.solveStrAsString (fix.TheLine ()));
            else str.append (fix.TheLine ());
         }

         if (p1.equals ("*"))
              strResult = utilEscapeStr.escapeStr (str.toString ());
         else strResult = utilEscapeStr.escapeStr (str.toString (), p1);
      }
      else if (oper.equals ("DECODE STR") || oper.equals ("DECODE"))
      {
         // strconv, decode str, utf-8, bla bla
         if (p1.equals ("*"))
              strResult = utilEscapeStr.desEscapeStr (p2);
         else strResult = utilEscapeStr.desEscapeStr (p2, p1);
      }
      else if (oper.equals ("DECODE VAR") || oper.equals ("DECODE"))
      {
         // strconv, decode str, utf-8, bla bla

         Eva srcEva = that.getReadVarEva (p2);
         if (srcEva == null)
         {
            that.log().err ("DECODE VAR", "Variable \"" + p2 + "\" could not be found!");
            return 1;
         }
         StringBuffer str = new StringBuffer ();;
         for (int rr = 0; rr < srcEva.rows (); rr ++)
         {
            //(o) TODO document this behaviour (if variable contain more rows, columns ... etc)
            // here it hardly make sense to include a return character since an encoded string should not have any
            //str += (rr != 0 ? "\n":  "");
            if (OptSolveVar)
                 str.append (that.solveStrAsString (srcEva.getValue (rr, 0)));
            else str.append (srcEva.getValue (rr, 0));
         }

         if (p1.equals ("*"))
              strResult = utilEscapeStr.desEscapeStr (str.toString ());
         else strResult = utilEscapeStr.desEscapeStr (str.toString (), p1);
      }
      else if (oper.startsWith ("ESCAPE"))
      {
         if (p1.equalsIgnoreCase ("HTML"))
         {
            strResult = getHtmlEncoder ().encode (p2);
            utilEscapeStr.escapeStr (p2);
         }
         else if (p1.equalsIgnoreCase ("LATEX"))
         {
            strResult = getLatexEncoder ().encode (p2);
         }
         else
         {
            that.log().err ("STRCONV", oper + " escapeStyle [] not supported! (only HTML and LATEX)");
            strResult = p2;
         }
      }
      else if (oper.startsWith ("UNESCAPE"))
      {
         if (p1.equalsIgnoreCase ("HTML"))
         {
            strResult = getHtmlEncoder ().decode (p2);

         utilEscapeStr.escapeStr (p2);
         }
         else if (p1.equalsIgnoreCase ("LATEX"))
         {
            strResult = getLatexEncoder ().decode (p2);
         }
         else
         {
            that.log().err ("STRCONV", oper + " escapeStyle [] not supported! (only HTML and LATEX)");
            strResult = p2;
         }
      }
      else if (oper.equals ("STR-TEXT") || oper.equals ("STRE-TEXT"))
      {
         String str = p1;
         if (oper.equals ("STRE-TEXT"))
         {
            // p1 = eva where the string is to be found
            if (! requiredEvas(that, "STRE-TEXT", p1, p2, false))
               return 1;

            // No use case! if the string is "escaped" can be given in a single line
            //
            // // a text content is expected (only column 0)
            // Eva srcEva = that.getReadVarEva (p1);
            // str = "";
            // for (int rr = 0; rr < srcEva.rows (); rr ++)
            //    str += (rr != 0 ? "\n":  "") + srcEva.getValue (rr, 0);
            str = that.getReadVarEva (p1).getValue (0,0);
         }

         String [] arr = de.elxala.db.utilEscapeStr.desEscapeStrToArray (str);

         if (p2.length () == 0)
         {
            //(o) TODO unify these conversions!! here is implemented as implicit "SOLVE VAR, 0" (writeStringOnTarget instead of printTextLsx)
            //     STRE-TEXT should be DEPRECATED !!!

            // print out the result
            for (int ii = 0; ii < arr.length; ii ++)
            {
               if (ii > 0) that.newLineOnTarget();
               that.writeStringOnTarget (arr[ii]);
            }
         }
         else
         {
            // place the result into an Eva
            Eva theVar = that.getSomeHowVarEva (p2);
            theVar.clear ();
            for (int ii = 0; ii < arr.length; ii ++)
               theVar.setValue (arr[ii], ii, 0);
         }
      }
      else if (oper.equals ("TEXT-STR"))
      {
         // System.out.println ("getting eva [" + p1 + "]");
         // second parameter is mandatory!
         //    this is because the coded string could contain @ variables
         //    and calling printTextLsx with it will produce them to be solved

         if (! requiredEvas(that, "TEXT-STR", p1, p2, false)) return 1;
         Eva theVar = that.getReadVarEva (p1);

         //Eva theVar = that.getVarEva (p1);
         String sResult = de.elxala.db.utilEscapeStr.escapeStrArray (theVar.getAsArray ());

         that.log().dbg(2, "STRCONV", "TEXT-STR result [" + sResult + "]");

         if (p2.length () == 0)
         {
            //(o) TODO unify these conversions!! here is implemented as implicit "SOLVE VAR, 0" (writeStringOnTarget instead of printTextLsx)
            //     TEXT-STR should be DEPRECATED !!!

            // no @ replacement !
            that.writeStringOnTarget (sResult);
         }
         else
         {
            Eva evaTarget = that.getSomeHowVarEva (p2);

            // place the result into an Eva
            evaTarget.clear ();
            evaTarget.setValue (sResult, 0, 0);
         }
      }
      else if (oper.equals ("DATE-LONG"))
      {
         strResult = "" + DateFormat.getAsLong (p1);
      }
      else if (oper.equals ("LONG-DATE"))
      {
         // NOTE : FIX the timezone offset to be consequent with DATE-LONG
         //
         java.util.TimeZone tz = java.util.TimeZone.getDefault ();
         de.elxala.langutil.DateFormat df = new de.elxala.langutil.DateFormat (new java.util.Date (stdlib.atol (p1)-tz.getOffset (0)));
         strResult = df.get ();
      }
      else if (oper.equals ("NUM-FIX"))
      {
         double number = stdlib.atof (p1);
         int fix    = stdlib.atoi (p2);

         strResult = formatFix (number, fix);

//         int mantisa = 0;
//
//         if (fix != 0)
//         {
//            mantisa = (int) (number * Math.pow(10,fix) + (number > 0 ? .5: -.5));
//            that.log().dbg(2, "STRCONV", "NUM-FIX fix = " + fix + " mantisa = " + mantisa);
//            number = mantisa / Math.pow(10,fix);
//         }
//
//         strResult = "" + (fix == 0 ? (int) number: number);
      }
      else if (oper.equals ("NUM-ENG"))
      {
         strResult = formatEngineering (stdlib.atof (p1));
      }
      else if (oper.equals ("EVA-TEXT"))
      {
         // second parameter is mandatory!
         if (! requiredEvas(that, oper, p1, p2, true)) return 1;

         Eva source = that.getReadVarEva (p1);
         Eva target = that.getSomeHowVarEva (p2);
         target.clear ();

         for (int ii = 0; ii < source.rows (); ii ++)
            target.addLine (new EvaLine (new String [] { source.get(ii).toString () }));
      }
      else if (oper.equals ("TEXT-EVA"))
      {
         if (! requiredEvas(that, oper, p1, p2, true)) return 1;

         Eva source = that.getReadVarEva (p1);
         Eva target = that.getSomeHowVarEva (p2);
         target.clear ();

         EvaFile.textLines2Eva (source.getAsArray (), target);
      }
      else if (oper.equals ("GEN EVA"))
      {
         // second parameter (p1 !) is mandatory!
         if (! requiredEvas(that, oper, p2, p1, true)) return 1;

         // NOTE: targetEva will be generated ALWAYS in "data" unit
         //       doing it this way we ensure that javaj will have access to it
         //       when working with gastona
         //
         Eva evaTarget = that.getGlobalData ().getSomeHowEva (p1);
         Eva evaResult = that.solveLsxFormatAsEva (p2);

         // assign it to target. (NOTE: if evaResult were kept alive we should merge with copy=true)
         evaTarget.merge (evaResult, Eva.MERGE_REPLACE, false);
      }
      else if (oper.equals ("TEXT-HTMLTEXT") || oper.equals ("TEXT-HTMLTXT"))
      {
         //< &lt; Start HTML tag label
         //> &gt; End HTML tag label
         //" &quot; Double quote
         //& &amp; Ampersand

         //  [<hola"macho" & Cia>] -> [&lt;hola&quot;macho&quot; &amp; Cia&gt;]


         strResult = getHtmlEncoder ().encode (p1);
      }
      else if (oper.equals ("HTMLTEXT-TEXT") || oper.equals ("HTMLTXT-TEXT"))
      {
         //< &lt; Start HTML tag label
         //> &gt; End HTML tag label
         //" &quot; Double quote
         //& &amp; Ampersand

         //  [&lt;hola&quot;macho&quot; &amp; Cia&gt;] -> [<hola"macho" & Cia>]
         strResult = getHtmlEncoder ().decode (p1);
         // strResult = p1;
         // strResult = strResult.replaceAll ("&lt;"  , "<");
         // strResult = strResult.replaceAll ("&#60;" , "<");
         // strResult = strResult.replaceAll ("&gt;"  , ">");
         // strResult = strResult.replaceAll ("&#62;" , ">");
         // strResult = strResult.replaceAll ("&quot;", "\"");  // &#34;
         // strResult = strResult.replaceAll ("&#34;" , "\"");   // &#34;
         // strResult = strResult.replaceAll ("&amp;" , "&");   // &#38;
         // strResult = strResult.replaceAll ("&#38;" , "&");   // &#38;
      }
      else if (oper.equals ("PATH-SOLVE") || oper.equals ("PATH-SOLVE-POINTS"))
      {
         strResult = fileUtil.resolveRelativePointsInPath (p1);
      }
      else if (oper.equals ("PATH-JOIN") || oper.equals ("PATH-CONCAT"))
      {
         strResult = fileUtil.concatPaths (p1, p2);
      }
      else if (oper.equals ("PATH-CANONICAL"))
      {
         strResult = p1;

         that.log().dbg (2, "STRCONV", "path-canonical I [" + p1 + "]");

         if (p1.length () == 0 || p1.startsWith("./") || p1.startsWith(".\\") ||
              (!p1.startsWith("/") && !p1.startsWith("\\")))
         {
            // should be a relative path, convert it into absolute path
            p1 = fileUtil.getApplicationDir () + fileUtil.DIR_SEP + p1;

            that.log().dbg (2, "STRCONV", "path-canonical II [" + p1 + "]");
         }

         File fi = new File (p1);
         try
         {
            strResult = fi.getCanonicalPath ();
         }
         catch (Exception e) {}
      }
      else if (oper.equals ("TEXT-VARNAME") || oper.equals ("TEXT2VARNAME") || oper.equals ("2VARNAME"))
      {
         strResult = naming.toVariableName (p1);
      }
      else if (oper.equals ("XOR ENCRYPT") || oper.equals ("XOR"))
      {
         String p4   = cmd.getArg(4);

         String inVar = (cmd.takeOptionString(new String [] {"INVAR" }, null));
         String inStr = (cmd.takeOptionString(new String [] {"INSTR" }, null));

         if (!(inVar == null ^ inStr == null))
         {
            that.log().err("STRCONV", "XOR ENCRYPT one and only one input message has to be given! [" + inStr + "]["+ inVar + "]");
         }
         else
         {
            if (inVar != null)
               inStr = inVar.toString ();
            strResult = xorEncrypt (inStr, p1, stdlib.atoi (p2), stdlib.atoi (p3), stdlib.atoi (p4));
            OptSolveVar = false; // it makes no sense, I think, to want possible @ variables from the output to be solved
         }
      }
      else if (oper.equals ("TRAZOS-JS"))
      {
         //    STRCONV, TRAZOS-JS, evaname, uselib

         Eva source = that.getReadVarEva (p1);
         boolean optimize = ! p2.equals ("0"); // default is 1

         int telaX = stdlib.atoi (cmd.getArg(3));
         int telaY = stdlib.atoi (cmd.getArg(4));
         boolean offset0 = 1 == stdlib.atoi (cmd.getArg(5));

         strResult = javaj.widgets.graphics.objects.editablePaths.trazosToJavaScript (source, optimize, telaX, telaY, offset0);
      }
      else if (oper.equals ("2DPATHS-FILE"))
      {
         //    STRCONV, 2DPATHS-PNG, evaname, pngFileName, filetype[png], dx, dy

         Eva source = that.getReadVarEva (p1);
         String fileName = p2;
         String fileFormat = p3.equals ("") ? "png": p3;
         
         int dx = stdlib.atoi (cmd.getArg(4));
         int dy = stdlib.atoi (cmd.getArg(5));
         
         BufferedImage ima = null;
         if (dx > 0)
              ima = uniUtilImage.graffitiToBufferedImage (source, dx, dy == 0 ? dx: dy, null);
         else ima = uniUtilImage.graffitiToBufferedImage (source);
         
         uniUtilImage.saveBufferedImageTofile (ima, fileName, fileFormat);
         strResult = "";
      }
      else
      {
         that.log().err("STRCONV", "Operation [" + oper + "] not recognized!");
      }
      that.log().dbg (2, "STRCONV", "path-canonical III [" + strResult + "]");

//      System.out.println ("result string [" + strResult + "]");

      if (OptSolveVar)
           that.printTextLsx (strResult);
      else that.writeStringOnTarget (strResult);
      return 1;
   }

   public static String xorEncrypt (String message, String key, int off1, int mult, int off2)
   {
      String sal = "";
      int lon = key.length ();
      for (int ii = 0; ii < message.length (); ii ++)
      {
         sal += (char) (message.charAt (ii) ^ (char) ((key.charAt ((ii + off1) % lon) + mult * ii + off2) % 256));
      }
      return sal;
   }

   private boolean requiredEvas(listix that, String subCmdName, String e1, String e2, boolean e2Mandatory)
   {
      if (that.getReadVarEva (e1) == null)
      {
         that.log().err ("STRCONV", subCmdName + " source eva <" + e1 + "> not found!");
         return false;
      }

      if (e2Mandatory && e2.length () == 0)
      {
         that.log().err ("STRCONV", subCmdName + " requires the parameter EvaTarget!");
         return false;
      }
      return true;
   }

   public static String formatFix(double nu, int ndec)
   {
      String form = "0." + "#####################".substring(0, Math.min(ndec, 20));
      java.text.DecimalFormat decFormat1 = new java.text.DecimalFormat (form);
      String str = decFormat1.format(nu);
      str = str.replaceAll (",", ".");       // avoid localization (, instead of .)

      return str;
   }


   public static String formatEngineering(double nu)
   {
      java.text.DecimalFormat decFormatEng = new java.text.DecimalFormat ("##0.0##E0");
      String str = decFormatEng.format (nu);

      str = str.replaceAll ("E", " E+");     // separate E symbol and add always symbol
      str = str.replaceAll ("E\\+-", "E-");  //
      str = str.replaceAll (",", ".");       // avoid localization (, instead of .)

      return str;
   }

}

