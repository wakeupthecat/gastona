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
         8   ,   3       , //Converts the string date 'dateAsString' (greater than 1970-01-01) into a long value
         9   ,   3       , //Converts the date 'milisecSince1970' given as long into a string date
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

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample
      STRCONV basicSample
      Listix interactive sample


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
      //
      //	<aweek ago>  =, along - 1000*3600*24*7
      //
      //	<along>	STRCONV, DATE-LONG, @<ahora>

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
      //       Courier, 15, 0, TextField
      //
      //#data#
      //
      //   <xText>
      //      //LOOP, FOR, ii, 1, 3
      //      //"Hola @<:sys user.name>, que tal andas"
      //
      //#listix#
      //
      //   <-- bRun>
      //      @<convert text into eva>
      //      @<launch new format>
      //
      //   <convert text into eva> STRCONV, TEXT-EVA, xText, my new format
      //   <launch new format>     @<my new format>


#**FIN_EVA#
*/

package listix.cmds;

import java.io.File;
import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

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

      String oper = that.solveStrAsString (commands.getValue (indxComm, 1)).toUpperCase ();
      String p1   = that.solveStrAsString (commands.getValue (indxComm, 2));
      String p2   = that.solveStrAsString (commands.getValue (indxComm, 3));
      String p3   = that.solveStrAsString (commands.getValue (indxComm, 4));
      String strResult = "";

      if (oper.equals ("SUBSTR"))
      {
         int offset0 = Math.max (stdlib.atoi (p2)-1, 0);
         int lenmax  = Math.min (stdlib.atoi (p3), p1.length () - offset0);
         if (lenmax <= 0) lenmax = offset0 + 1;

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
      else if (oper.equals ("STR-TEXT") || oper.equals ("STRE-TEXT"))
      {
         String str = p1;
         if (oper.equals ("STRE-TEXT"))
         {
            // p1 = eva where the string is to be found
            if (! requiredEvas(that, "STRE-TEXT", p1, p2, false))
               return 1;

            // Eva srcEva = that.getVarEva (p1);
            str = that.getReadVarEva (p1).getValue (0,0);
         }

         String [] arr = de.elxala.db.utilEscapeStr.desEscapeStrToArray (str);

         if (p2.length () == 0)
         {
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
         //yyyy-MM-dd HH:mm:ss S
         strResult = "" + de.elxala.langutil.DateFormat.getDate (p1, "yyyy-MM-dd HH:mm:ss S", "1970-01-01 00:00:00 0").getTime ();
      }
      else if (oper.equals ("LONG-DATE"))
      {
         de.elxala.langutil.DateFormat df = new de.elxala.langutil.DateFormat (new java.util.Date (stdlib.atol (p1)));
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

         strResult = p1;
         strResult = strResult.replaceAll ("&", "&amp;");   // &#38;
         strResult = strResult.replaceAll ("<", "&lt;");
         strResult = strResult.replaceAll (">", "&gt;");
         strResult = strResult.replaceAll ("\"", "&quot;"); // &#34;
      }
      else if (oper.equals ("HTMLTEXT-TEXT") || oper.equals ("HTMLTXT-TEXT"))
      {
         //< &lt; Start HTML tag label
         //> &gt; End HTML tag label
         //" &quot; Double quote
         //& &amp; Ampersand

         //  [&lt;hola&quot;macho&quot; &amp; Cia&gt;] -> [<hola"macho" & Cia>]

         strResult = p1;
         strResult = strResult.replaceAll ("&lt;"  , "<");
         strResult = strResult.replaceAll ("&#60;" , "<");
         strResult = strResult.replaceAll ("&gt;"  , ">");
         strResult = strResult.replaceAll ("&#62;" , ">");
         strResult = strResult.replaceAll ("&quot;", "\"");  // &#34;
         strResult = strResult.replaceAll ("&#34;" , "\"");   // &#34;
         strResult = strResult.replaceAll ("&amp;" , "&");   // &#38;
         strResult = strResult.replaceAll ("&#38;" , "&");   // &#38;
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
            p1 = System.getProperty ("user.dir") + fileUtil.DIR_SEP + p1;

            that.log().dbg (2, "STRCONV", "path-canonical II [" + p1 + "]");
         }

         File fi = new File (p1);
         try
         {
            strResult = fi.getCanonicalPath ();
         }
         catch (Exception e) {}
      }
      else
      {
         that.log().err("STRCONV", "Operation [" + oper + "] not recognized!");
      }
      that.log().dbg (2, "STRCONV", "path-canonical III [" + strResult + "]");

//      System.out.println ("result string [" + strResult + "]");
      that.printTextLsx (strResult);
      return 1;
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

